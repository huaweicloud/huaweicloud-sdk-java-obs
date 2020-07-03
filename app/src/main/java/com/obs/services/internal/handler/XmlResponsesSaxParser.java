/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.obs.services.internal.handler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.internal.Constants;
import com.obs.services.internal.ServiceException;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.AbstractNotification;
import com.obs.services.model.AccessControlList;
import com.obs.services.model.BucketCors;
import com.obs.services.model.BucketCorsRule;
import com.obs.services.model.BucketDirectColdAccess;
import com.obs.services.model.BucketEncryption;
import com.obs.services.model.BucketLoggingConfiguration;
import com.obs.services.model.BucketNotificationConfiguration;
import com.obs.services.model.BucketQuota;
import com.obs.services.model.BucketStorageInfo;
import com.obs.services.model.BucketStoragePolicyConfiguration;
import com.obs.services.model.BucketTagInfo;
import com.obs.services.model.BucketTypeEnum;
import com.obs.services.model.BucketVersioningConfiguration;
import com.obs.services.model.CanonicalGrantee;
import com.obs.services.model.CopyPartResult;
import com.obs.services.model.DeleteObjectsResult;
import com.obs.services.model.EventTypeEnum;
import com.obs.services.model.FunctionGraphConfiguration;
import com.obs.services.model.GrantAndPermission;
import com.obs.services.model.GranteeInterface;
import com.obs.services.model.GroupGrantee;
import com.obs.services.model.HistoricalObjectReplicationEnum;
import com.obs.services.model.InitiateMultipartUploadResult;
import com.obs.services.model.LifecycleConfiguration;
import com.obs.services.model.Multipart;
import com.obs.services.model.MultipartUpload;
import com.obs.services.model.ObsBucket;
import com.obs.services.model.ObsObject;
import com.obs.services.model.Owner;
import com.obs.services.model.Permission;
import com.obs.services.model.ProtocolEnum;
import com.obs.services.model.Redirect;
import com.obs.services.model.RedirectAllRequest;
import com.obs.services.model.ReplicationConfiguration;
import com.obs.services.model.RequestPaymentConfiguration;
import com.obs.services.model.RequestPaymentEnum;
import com.obs.services.model.RouteRule;
import com.obs.services.model.RouteRuleCondition;
import com.obs.services.model.RuleStatusEnum;
import com.obs.services.model.SSEAlgorithmEnum;
import com.obs.services.model.StorageClassEnum;
import com.obs.services.model.TopicConfiguration;
import com.obs.services.model.VersionOrDeleteMarker;
import com.obs.services.model.VersioningStatusEnum;
import com.obs.services.model.WebsiteConfiguration;
import com.obs.services.model.fs.FolderContentSummary;

public class XmlResponsesSaxParser {

    private static final ILogger log = LoggerBuilder.getLogger("com.obs.services.internal.RestStorageService");

    private XMLReader xmlReader;

    public XmlResponsesSaxParser() throws ServiceException {
        this.xmlReader = ServiceUtils.loadXMLReader();
    }

    protected void parseXmlInputStream(DefaultHandler handler, InputStream inputStream) throws ServiceException {
        if (inputStream == null) {
            return;
        }
        try {
            xmlReader.setErrorHandler(handler);
            xmlReader.setContentHandler(handler);
            xmlReader.parse(new InputSource(inputStream));
        } catch (Exception t) {
            throw new ServiceException("Failed to parse XML document with handler " + handler.getClass(), t);
        } finally {
            ServiceUtils.closeStream(inputStream);
        }
    }

    protected InputStream sanitizeXmlDocument(InputStream inputStream) throws ServiceException {
        if (inputStream == null) {
            return null;
        }
        BufferedReader br = null;
        try {
            StringBuilder listingDocBuffer = new StringBuilder();
            br = new BufferedReader(new InputStreamReader(inputStream, Constants.DEFAULT_ENCODING));

            char[] buf = new char[8192];
            int read = -1;
            while ((read = br.read(buf)) != -1) {
                listingDocBuffer.append(buf, 0, read);
            }

            String listingDoc = listingDocBuffer.toString().replaceAll("\r", "&#013;");
            if (log.isTraceEnabled()) {
                log.trace("Response entity: " + listingDoc);
            }
            return new ByteArrayInputStream(listingDoc.getBytes(Constants.DEFAULT_ENCODING));
        } catch (Throwable t) {
            throw new ServiceException("Failed to sanitize XML document destined", t);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    if (log.isWarnEnabled()) {
                        log.warn(e);
                    }
                }
            }
            ServiceUtils.closeStream(inputStream);
        }
    }

    public <T> T parse(InputStream inputStream, Class<T> handlerClass, boolean sanitize) throws ServiceException {
        try {
            T handler = null;
            if (SimpleHandler.class.isAssignableFrom(handlerClass)) {
                Constructor<T> c = handlerClass.getConstructor(XMLReader.class);
                handler = c.newInstance(this.xmlReader);
            } else {
                handler = handlerClass.getConstructor().newInstance();
            }
            if (handler instanceof DefaultHandler) {
                if (sanitize) {
                    inputStream = sanitizeXmlDocument(inputStream);
                }
                parseXmlInputStream((DefaultHandler) handler, inputStream);
            }
            return handler;
        } catch (NoSuchMethodException e) {
            throw new ServiceException(e);
        } catch (InvocationTargetException e) {
            throw new ServiceException(e);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public static class ListObjectsHandler extends DefaultXmlHandler {
        private ObsObject currentObject;

        private Owner currentOwner;

        private boolean insideCommonPrefixes = false;

        private final List<ObsObject> objects = new ArrayList<ObsObject>();

        private final List<String> commonPrefixes = new ArrayList<String>();

        private ObsObject currentExtenedCommonPrefix;

        private final List<ObsObject> extenedCommonPrefixes = new ArrayList<ObsObject>();

        private String bucketName;

        private String requestPrefix;

        private String requestMarker;

        private String requestDelimiter;

        private int requestMaxKeys = 0;

        private boolean listingTruncated = false;

        private String lastKey;

        private String nextMarker;

        public String getMarkerForNextListing() {
            return listingTruncated ? nextMarker == null ? lastKey : nextMarker : null;
        }

        public String getBucketName() {
            return bucketName;
        }

        public boolean isListingTruncated() {
            return listingTruncated;
        }

        public List<ObsObject> getObjects() {
            return this.objects;
        }

        public List<String> getCommonPrefixes() {
            return commonPrefixes;
        }

        public List<ObsObject> getExtenedCommonPrefixes() {
            return extenedCommonPrefixes;
        }

        public String getRequestPrefix() {
            return requestPrefix;
        }

        public String getRequestMarker() {
            return requestMarker;
        }

        public String getNextMarker() {
            return nextMarker;
        }

        public int getRequestMaxKeys() {
            return requestMaxKeys;
        }

        public String getRequestDelimiter() {
            return requestDelimiter;
        }

        @Override
        public void startElement(String name) {
            if (name.equals("Contents")) {
                currentObject = new ObsObject();
                currentObject.setBucketName(bucketName);
            } else if (name.equals("Owner")) {
                currentOwner = new Owner();
            } else if (name.equals("CommonPrefixes")) {
                insideCommonPrefixes = true;
                currentExtenedCommonPrefix = new ObsObject();
            }
        }

        @Override
        public void endElement(String name, String elementText) {
            if (name.equals("Name")) {
                bucketName = elementText;
            } else if (!insideCommonPrefixes && name.equals("Prefix")) {
                requestPrefix = elementText;
            } else if (name.equals("Marker")) {
                requestMarker = elementText;
            } else if (name.equals("NextMarker")) {
                nextMarker = elementText;
            } else if (name.equals("MaxKeys")) {
                requestMaxKeys = Integer.parseInt(elementText);
            } else if (name.equals("Delimiter")) {
                requestDelimiter = elementText;
            } else if (name.equals("IsTruncated")) {
                listingTruncated = Boolean.valueOf(elementText);
            } else if (name.equals("Contents")) {
                objects.add(currentObject);
            } else if (name.equals("DisplayName")) {
                if (currentOwner != null) {
                    currentOwner.setDisplayName(elementText);
                }
            }

            if (null != currentObject) {
                if (name.equals("Key")) {
                    currentObject.setObjectKey(elementText);
                    lastKey = elementText;
                } else if (name.equals("LastModified")) {
                    try {
                        currentObject.getMetadata().setLastModified(ServiceUtils.parseIso8601Date(elementText));
                    } catch (ParseException e) {
                        if (log.isErrorEnabled()) {
                            log.error("Non-ISO8601 date for LastModified in bucket's object listing output: "
                                    + elementText, e);
                        }
                    }
                } else if (name.equals("ETag")) {
                    currentObject.getMetadata().setEtag(elementText);
                } else if (name.equals("Size")) {
                    currentObject.getMetadata().setContentLength(Long.parseLong(elementText));
                } else if (name.equals("StorageClass")) {
                    currentObject.getMetadata().setObjectStorageClass(StorageClassEnum.getValueFromCode(elementText));
                } else if (name.equals("ID")) {
                    if (currentOwner == null) {
                        currentOwner = new Owner();
                    }
                    currentObject.setOwner(currentOwner);
                    currentOwner.setId(elementText);
                } else if (name.equals("Type")) {
                    currentObject.getMetadata().setAppendable("Appendable".equals(elementText));
                }
            }

            if (null != currentExtenedCommonPrefix) {
                if (insideCommonPrefixes && name.equals("Prefix")) {
                    commonPrefixes.add(elementText);
                    currentExtenedCommonPrefix.setObjectKey(elementText);
                } else if (insideCommonPrefixes && name.equals("MTime")) {
                    currentExtenedCommonPrefix.getMetadata()
                            .setLastModified(new Date(Long.parseLong(elementText) * 1000));
                }
            }

            if (name.equals("CommonPrefixes")) {
                extenedCommonPrefixes.add(currentExtenedCommonPrefix);
                insideCommonPrefixes = false;
            }
        }
    }

    public static class ListContentSummaryHandler extends DefaultXmlHandler {
        private FolderContentSummary currentFolderContentSummary;

        private FolderContentSummary.LayerSummary currentLayerSummary;

        private final List<FolderContentSummary> folderContentSummaries = new ArrayList<FolderContentSummary>();

        private String bucketName;

        private String requestPrefix;

        private String requestMarker;

        private String requestDelimiter;

        private int requestMaxKeys = 0;

        private boolean listingTruncated = false;

        private String nextMarker;

        private String lastFolder;


        public String getBucketName() {
            return bucketName;
        }

        public boolean isListingTruncated() {
            return listingTruncated;
        }

        public List<FolderContentSummary> getFolderContentSummaries() {
            return folderContentSummaries;
        }

        public String getRequestPrefix() {
            return requestPrefix;
        }

        public String getRequestMarker() {
            return requestMarker;
        }

        public String getNextMarker() {
            return nextMarker;
        }

        public int getRequestMaxKeys() {
            return requestMaxKeys;
        }

        public String getRequestDelimiter() {
            return requestDelimiter;
        }

        public String getMarkerForNextListing() {
            return listingTruncated ? nextMarker == null ? lastFolder : nextMarker : null;
        }

        @Override
        public void startElement(String name) {
            if (name.equals("Contents")) {
                currentFolderContentSummary = new FolderContentSummary();
            } else if (name.equals("LayerSummary")) {
                currentLayerSummary = new FolderContentSummary.LayerSummary();
            }
        }

        @Override
        public void endElement(String name, String elementText) {
            if (name.equals("BucketName")) {
                bucketName = elementText;
            } else if (name.equals("Prefix")) {
                requestPrefix = elementText;
            } else if (name.equals("Marker")) {
                requestMarker = elementText;
            } else if (name.equals("NextMarker")) {
                nextMarker = elementText;
            } else if (name.equals("MaxKeys")) {
                requestMaxKeys = Integer.parseInt(elementText);
            } else if (name.equals("Delimiter")) {
                requestDelimiter = elementText;
            } else if (name.equals("IsTruncated")) {
                listingTruncated = Boolean.valueOf(elementText);
            } else if (name.equals("Directory")) {
                currentFolderContentSummary.setDir(elementText);
                lastFolder = elementText;
            } else if (name.equals("DirHeight")) {
                currentFolderContentSummary.setDirHeight(Long.parseLong(elementText));
            } else if (name.equals("SummaryHeight")) {
                currentLayerSummary.setSummaryHeight(Long.parseLong(elementText));
            } else if (name.equals("DirCount")) {
                currentLayerSummary.setDirCount(Long.parseLong(elementText));
            } else if (name.equals("FileCount")) {
                currentLayerSummary.setFileCount(Long.parseLong(elementText));
            } else if (name.equals("FileSize")) {
                currentLayerSummary.setFileSize(Long.parseLong(elementText));
            } else if (name.equals("LayerSummary")) {
                currentFolderContentSummary.getLayerSummaries().add(currentLayerSummary);
            } else if (name.equals("Contents")) {
                folderContentSummaries.add(currentFolderContentSummary);
            }
        }
    }

    public static class ListBucketsHandler extends DefaultXmlHandler {
        private Owner bucketsOwner;

        private ObsBucket currentBucket;

        private final List<ObsBucket> buckets = new ArrayList<ObsBucket>();

        public List<ObsBucket> getBuckets() {
            return this.buckets;
        }

        public Owner getOwner() {
            return bucketsOwner;
        }

        @Override
        public void startElement(String name) {
            if (name.equals("Bucket")) {
                currentBucket = new ObsBucket();
            } else if (name.equals("Owner")) {
                bucketsOwner = new Owner();
            }
        }

        @Override
        public void endElement(String name, String elementText) {
            if (null != bucketsOwner) {
                if (name.equals("ID")) {
                    bucketsOwner.setId(elementText);
                } else if (name.equals("DisplayName")) {
                    bucketsOwner.setDisplayName(elementText);
                }
            }

            if (null != currentBucket) {
                if (name.equals("Bucket")) {
                    currentBucket.setOwner(bucketsOwner);
                    buckets.add(currentBucket);
                } else if (name.equals("Name")) {
                    currentBucket.setBucketName(elementText);
                } else if (name.equals("Location")) {
                    currentBucket.setLocation(elementText);
                } else if (name.equals("CreationDate")) {
                    elementText += ".000Z";
                    try {
                        currentBucket.setCreationDate(ServiceUtils.parseIso8601Date(elementText));
                    } catch (ParseException e) {
                        if (log.isWarnEnabled()) {
                            log.warn("Non-ISO8601 date for CreationDate in list buckets output: " + elementText, e);
                        }
                    }
                } else if (name.equals("BucketType")) {
                    if (Constants.POSIX.equals(elementText)) {
                        currentBucket.setBucketType(BucketTypeEnum.PFS);
                    } else {
                        currentBucket.setBucketType(BucketTypeEnum.OBJECT);
                    }
                }
            }
        }
    }

    public static class BucketLoggingHandler extends DefaultXmlHandler {

        private BucketLoggingConfiguration bucketLoggingStatus = new BucketLoggingConfiguration();

        private String targetBucket;

        private String targetPrefix;

        private GranteeInterface currentGrantee;

        private Permission currentPermission;

        private boolean currentDelivered;

        public BucketLoggingConfiguration getBucketLoggingStatus() {
            return bucketLoggingStatus;
        }

        @Override
        public void endElement(String name, String elementText) {
            if (name.equals("TargetBucket")) {
                targetBucket = elementText;
            } else if (name.equals("TargetPrefix")) {
                targetPrefix = elementText;
            } else if (name.equals("LoggingEnabled")) {
                bucketLoggingStatus.setTargetBucketName(targetBucket);
                bucketLoggingStatus.setLogfilePrefix(targetPrefix);
            } else if (name.equals("Agency")) {
                bucketLoggingStatus.setAgency(elementText);
            } else if (name.equals("ID")) {
                currentGrantee = new CanonicalGrantee();
                currentGrantee.setIdentifier(elementText);
            } else if (name.equals("URI") || name.equals("Canned")) {
                currentGrantee = new GroupGrantee();
                currentGrantee.setIdentifier(elementText);
            } else if (name.equals("DisplayName")) {
                if (currentGrantee instanceof CanonicalGrantee) {
                    ((CanonicalGrantee) currentGrantee).setDisplayName(elementText);
                }
            } else if (name.equals("Delivered")) {
                currentDelivered = Boolean.parseBoolean(elementText);
            } else if (name.equals("Permission")) {
                currentPermission = Permission.parsePermission(elementText);
            } else if (name.equals("Grant")) {
                GrantAndPermission gap = new GrantAndPermission(currentGrantee, currentPermission);
                gap.setDelivered(currentDelivered);
                bucketLoggingStatus.addTargetGrant(gap);
            }
        }
    }

    public static class BucketLocationHandler extends DefaultXmlHandler {
        private String location;

        public String getLocation() {
            return location;
        }

        @Override
        public void endElement(String name, String elementText) {
            if (name.equals("LocationConstraint") || name.equals("Location")) {
                location = elementText;
            }
        }
    }

    public static class BucketCorsHandler extends DefaultXmlHandler {

        private final BucketCors configuration = new BucketCors();

        private BucketCorsRule currentRule;

        private List<String> allowedMethods = null;

        private List<String> allowedOrigins = null;

        private List<String> exposedHeaders = null;

        private List<String> allowedHeaders = null;

        public BucketCors getConfiguration() {
            return configuration;
        }

        @Override
        public void startElement(String name) {

            if ("CORSRule".equals(name)) {
                currentRule = new BucketCorsRule();
            }
            if ("AllowedOrigin".equals(name)) {
                if (allowedOrigins == null) {
                    allowedOrigins = new ArrayList<String>();
                }
            } else if ("AllowedMethod".equals(name)) {
                if (allowedMethods == null) {
                    allowedMethods = new ArrayList<String>();
                }
            } else if ("ExposeHeader".equals(name)) {
                if (exposedHeaders == null) {
                    exposedHeaders = new ArrayList<String>();
                }
            } else if ("AllowedHeader".equals(name)) {
                if (allowedHeaders == null) {
                    allowedHeaders = new LinkedList<String>();
                }
            }
        }

        @Override
        public void endElement(String name, String elementText) {
            if (name.equals("CORSRule")) {
                currentRule.setAllowedHeader(allowedHeaders);
                currentRule.setAllowedMethod(allowedMethods);
                currentRule.setAllowedOrigin(allowedOrigins);
                currentRule.setExposeHeader(exposedHeaders);
                configuration.getRules().add(currentRule);
                allowedHeaders = null;
                allowedMethods = null;
                allowedOrigins = null;
                exposedHeaders = null;
                currentRule = null;
            }
            if (name.equals("ID") && (null != currentRule)) {
                currentRule.setId(elementText);

            } else if (name.equals("AllowedOrigin") && (null != allowedOrigins)) {
                allowedOrigins.add(elementText);

            } else if (name.equals("AllowedMethod") && (null != allowedMethods)) {
                allowedMethods.add(elementText);

            } else if (name.equals("MaxAgeSeconds") && (null != currentRule)) {
                currentRule.setMaxAgeSecond(Integer.parseInt(elementText));

            } else if (name.equals("ExposeHeader") && (null != exposedHeaders)) {
                exposedHeaders.add(elementText);

            } else if (name.equals("AllowedHeader") && (null != allowedHeaders)) {
                allowedHeaders.add(elementText);
            }
        }
    }

    public static class CopyObjectResultHandler extends DefaultXmlHandler {
        private String etag;

        private Date lastModified;

        public Date getLastModified() {
            return ServiceUtils.cloneDateIgnoreNull(lastModified);
        }

        public String getETag() {
            return etag;
        }

        @Override
        public void endElement(String name, String elementText) {
            if (name.equals("LastModified")) {
                try {
                    lastModified = ServiceUtils.parseIso8601Date(elementText);
                } catch (ParseException e) {
                    if (log.isErrorEnabled()) {
                        log.error("Non-ISO8601 date for LastModified in copy object output: " + elementText, e);
                    }
                }
            } else if (name.equals("ETag")) {
                etag = elementText;
            }
        }
    }

    public static class RequestPaymentConfigurationHandler extends DefaultXmlHandler {
        private String payer = null;

        public boolean isRequesterPays() {
            return "Requester".equals(payer);
        }

        @Override
        public void endElement(String name, String elementText) {
            if (name.equals("Payer")) {
                payer = elementText;
            }
        }
    }

    public static class BucketVersioningHandler extends DefaultXmlHandler {

        private BucketVersioningConfiguration versioningStatus;

        private String status;

        public BucketVersioningConfiguration getVersioningStatus() {
            return this.versioningStatus;
        }

        @Override
        public void endElement(String name, String elementText) {
            if (name.equals("Status")) {
                this.status = elementText;
            } else if (name.equals("VersioningConfiguration")) {
                this.versioningStatus = new BucketVersioningConfiguration(
                        VersioningStatusEnum.getValueFromCode(this.status));
            }
        }
    }

    public static class RequestPaymentHandler extends DefaultXmlHandler {

        private RequestPaymentConfiguration requestPaymentConfiguration;

        private String payer;

        public RequestPaymentConfiguration getRequestPaymentConfiguration() {
            return this.requestPaymentConfiguration;
        }

        @Override
        public void endElement(String name, String elementText) {
            if (name.equals("Payer")) {
                this.payer = elementText;
            } else if (name.equals("RequestPaymentConfiguration")) {
                this.requestPaymentConfiguration = new RequestPaymentConfiguration(
                        RequestPaymentEnum.getValueFromCode(this.payer));
            }
        }
    }

    public static class ListVersionsHandler extends DefaultXmlHandler {

        private final List<VersionOrDeleteMarker> items = new ArrayList<VersionOrDeleteMarker>();

        private final List<String> commonPrefixes = new ArrayList<String>();

        private String key;

        private String versionId;

        private boolean isLatest = false;

        private Date lastModified;

        private Owner owner;

        private String etag;

        private long size = 0;

        private String storageClass;

        private boolean isAppendable;

        private boolean insideCommonPrefixes = false;

        // Listing properties.
        private String bucketName;

        private String requestPrefix;

        private String keyMarker;

        private String versionIdMarker;

        private long requestMaxKeys = 0;

        private boolean listingTruncated = false;

        private String nextMarker;

        private String nextVersionIdMarker;

        private String delimiter;

        public String getDelimiter() {
            return this.delimiter;
        }

        public String getBucketName() {
            return this.bucketName;
        }

        public boolean isListingTruncated() {
            return listingTruncated;
        }

        public List<VersionOrDeleteMarker> getItems() {
            return this.items;
        }

        public List<String> getCommonPrefixes() {
            return commonPrefixes;
        }

        public String getRequestPrefix() {
            return requestPrefix;
        }

        public String getKeyMarker() {
            return keyMarker;
        }

        public String getVersionIdMarker() {
            return versionIdMarker;
        }

        public String getNextKeyMarker() {
            return nextMarker;
        }

        public String getNextVersionIdMarker() {
            return nextVersionIdMarker;
        }

        public long getRequestMaxKeys() {
            return requestMaxKeys;
        }

        private void reset() {
            this.key = null;
            this.versionId = null;
            this.isLatest = false;
            this.lastModified = null;
            this.etag = null;
            this.isAppendable = false;
            this.size = 0;
            this.storageClass = null;
            this.owner = null;
        }

        @Override
        public void startElement(String name) {
            if (name.equals("Owner")) {
                owner = new Owner();
            } else if (name.equals("CommonPrefixes")) {
                insideCommonPrefixes = true;
            }
        }

        @Override
        public void endElement(String name, String content) {
            if (name.equals("Name")) {
                bucketName = content;
            } else if (!insideCommonPrefixes && name.equals("Prefix")) {
                requestPrefix = content;
            } else if (name.equals("KeyMarker")) {
                keyMarker = content;
            } else if (name.equals("NextKeyMarker")) {
                nextMarker = content;
            } else if (name.equals("VersionIdMarker")) {
                versionIdMarker = content;
            } else if (name.equals("NextVersionIdMarker")) {
                nextVersionIdMarker = content;
            } else if (name.equals("MaxKeys")) {
                requestMaxKeys = Long.parseLong(content);
            } else if (name.equals("IsTruncated")) {
                listingTruncated = Boolean.valueOf(content);
            } else if (name.equals("Delimiter")) {
                delimiter = content;
            } else if (name.equals("Version")) {
                VersionOrDeleteMarker item = new VersionOrDeleteMarker(bucketName, key, versionId, isLatest,
                        lastModified, owner, etag, size, StorageClassEnum.getValueFromCode(storageClass), false,
                        isAppendable);
                items.add(item);
                this.reset();
            } else if (name.equals("DeleteMarker")) {
                VersionOrDeleteMarker item = new VersionOrDeleteMarker(bucketName, key, versionId, isLatest,
                        lastModified, owner, null, 0, null, true, false);
                items.add(item);
                this.reset();
            } else if (name.equals("Key")) {
                key = content;
            } else if (name.equals("VersionId")) {
                versionId = content;
            } else if (name.equals("IsLatest")) {
                isLatest = Boolean.valueOf(content);
            } else if (name.equals("LastModified")) {
                try {
                    lastModified = ServiceUtils.parseIso8601Date(content);
                } catch (ParseException e) {
                    if (log.isWarnEnabled()) {
                        log.warn(
                                "Non-ISO8601 date for LastModified in bucket's versions listing output: " + content,
                                e);
                    }
                }
            } else if (name.equals("ETag")) {
                etag = content;
            } else if (name.equals("Size")) {
                size = Long.parseLong(content);
            } else if (name.equals("StorageClass")) {
                storageClass = content;
            } else if (name.equals("Type")) {
                isAppendable = "Appendable".equals(content);
            } else if (name.equals("ID")) {
                if (owner == null) {
                    owner = new Owner();
                }
                owner.setId(content);
            } else if (name.equals("DisplayName")) {
                if (owner != null) {
                    owner.setDisplayName(content);
                }
            } else if (insideCommonPrefixes && name.equals("Prefix")) {
                commonPrefixes.add(content);
            } else if (name.equals("CommonPrefixes")) {
                insideCommonPrefixes = false;
            }
        }
    }

    public static class OwnerHandler extends SimpleHandler {
        private String id;

        private String displayName;

        public OwnerHandler(XMLReader xr) {
            super(xr);
        }

        public Owner getOwner() {
            Owner owner = new Owner();
            owner.setId(id);
            owner.setDisplayName(displayName);
            return owner;
        }

        public void endID(String content) {
            this.id = content;
        }

        public void endDisplayName(String content) {
            this.displayName = content;
        }

        public void endOwner(String content) {
            returnControlToParentHandler();
        }

        public void endInitiator(String content) {
            returnControlToParentHandler();
        }
    }

    public static class InitiateMultipartUploadHandler extends SimpleHandler {
        private String uploadId;

        private String bucketName;

        private String objectKey;

        public InitiateMultipartUploadHandler(XMLReader xr) {
            super(xr);
        }

        public InitiateMultipartUploadResult getInitiateMultipartUploadResult() {
            InitiateMultipartUploadResult result = new InitiateMultipartUploadResult(bucketName, objectKey, uploadId);
            return result;
        }

        public void endUploadId(String content) {
            this.uploadId = content;
        }

        public void endBucket(String content) {
            this.bucketName = content;
        }

        public void endKey(String content) {
            this.objectKey = content;
        }
    }

    public static class MultipartUploadHandler extends SimpleHandler {
        private String uploadId;

        private String objectKey;

        private String storageClass;

        private Owner owner;

        private Owner initiator;

        private Date initiatedDate;

        private boolean isInInitiator = false;

        public MultipartUploadHandler(XMLReader xr) {
            super(xr);
        }

        public MultipartUpload getMultipartUpload() {
            MultipartUpload multipartUpload = new MultipartUpload(uploadId, objectKey, initiatedDate,
                    StorageClassEnum.getValueFromCode(storageClass), owner, initiator);
            return multipartUpload;
        }

        public void endUploadId(String content) {
            this.uploadId = content;
        }

        public void endKey(String content) {
            this.objectKey = content;
        }

        public void endStorageClass(String content) {
            this.storageClass = content;
        }

        public void endInitiated(String content) {
            try {
                this.initiatedDate = ServiceUtils.parseIso8601Date(content);
            } catch (ParseException e) {
            }
        }

        public void startOwner() {
            isInInitiator = false;
            transferControl(new OwnerHandler(xr));
        }

        public void startInitiator() {
            isInInitiator = true;
            transferControl(new OwnerHandler(xr));
        }

        @Override
        public void controlReturned(SimpleHandler childHandler) {
            if (isInInitiator) {
                this.initiator = ((OwnerHandler) childHandler).getOwner();
            } else {
                this.owner = ((OwnerHandler) childHandler).getOwner();
            }
        }

        public void endUpload(String content) {
            returnControlToParentHandler();
        }
    }

    public static class ListMultipartUploadsHandler extends SimpleHandler {

        private final List<MultipartUpload> uploads = new ArrayList<MultipartUpload>();

        private final List<String> commonPrefixes = new ArrayList<String>();

        private boolean insideCommonPrefixes;

        private String bucketName;

        private String keyMarker;

        private String uploadIdMarker;

        private String nextKeyMarker;

        private String nextUploadIdMarker;

        private String delimiter;

        private int maxUploads;

        private String prefix;

        private boolean isTruncated = false;

        public ListMultipartUploadsHandler(XMLReader xr) {
            super(xr);
        }

        public List<MultipartUpload> getMultipartUploadList() {
            for (MultipartUpload upload : uploads) {
                upload.setBucketName(bucketName);
            }
            return uploads;
        }

        public String getBucketName() {
            return this.bucketName;
        }

        public boolean isTruncated() {
            return isTruncated;
        }

        public String getKeyMarker() {
            return keyMarker;
        }

        public String getUploadIdMarker() {
            return uploadIdMarker;
        }

        public String getNextKeyMarker() {
            return nextKeyMarker;
        }

        public String getNextUploadIdMarker() {
            return nextUploadIdMarker;
        }

        public int getMaxUploads() {
            return maxUploads;
        }

        public List<String> getCommonPrefixes() {
            return commonPrefixes;
        }

        public String getDelimiter() {
            return this.delimiter;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public void startUpload() {
            transferControl(new MultipartUploadHandler(xr));
        }

        public void startCommonPrefixes() {
            insideCommonPrefixes = true;
        }

        @Override
        public void controlReturned(SimpleHandler childHandler) {
            uploads.add(((MultipartUploadHandler) childHandler).getMultipartUpload());
        }

        public void endDelimiter(String content) {
            this.delimiter = content;
        }

        public void endBucket(String content) {
            this.bucketName = content;
        }

        public void endKeyMarker(String content) {
            this.keyMarker = content;
        }

        public void endUploadIdMarker(String content) {
            this.uploadIdMarker = content;
        }

        public void endNextKeyMarker(String content) {
            this.nextKeyMarker = content;
        }

        public void endNextUploadIdMarker(String content) {
            this.nextUploadIdMarker = content;
        }

        public void endMaxUploads(String content) {
            try {
                this.maxUploads = Integer.parseInt(content);
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Response xml is not well-format", e);
                }
            }
        }

        public void endIsTruncated(String content) {
            this.isTruncated = Boolean.parseBoolean(content);
        }

        public void endPrefix(String content) {
            if (insideCommonPrefixes) {
                commonPrefixes.add(content);
            } else {
                this.prefix = content;
            }
        }

        public void endCommonPrefixes() {
            insideCommonPrefixes = false;
        }

    }

    public static class CopyPartResultHandler extends SimpleHandler {
        private Date lastModified;

        private String etag;

        public CopyPartResultHandler(XMLReader xr) {
            super(xr);
        }

        public CopyPartResult getCopyPartResult(int partNumber) {
            CopyPartResult result = new CopyPartResult(partNumber, etag, lastModified);
            return result;
        }

        public void endLastModified(String content) {
            try {
                this.lastModified = ServiceUtils.parseIso8601Date(content);
            } catch (ParseException e) {
            }
        }

        public void endETag(String content) {
            this.etag = content;
        }

    }

    public static class PartResultHandler extends SimpleHandler {
        private int partNumber;

        private Date lastModified;

        private String etag;

        private long size;

        public PartResultHandler(XMLReader xr) {
            super(xr);
        }

        public Multipart getMultipartPart() {
            return new Multipart(partNumber, lastModified, etag, size);
        }

        public void endPartNumber(String content) {
            this.partNumber = Integer.parseInt(content);
        }

        public void endLastModified(String content) {
            try {
                this.lastModified = ServiceUtils.parseIso8601Date(content);
            } catch (ParseException e) {
            }
        }

        public void endETag(String content) {
            this.etag = content;
        }

        public void endSize(String content) {
            this.size = Long.parseLong(content);
        }

        public void endPart(String content) {
            returnControlToParentHandler();
        }
    }

    public static class ListPartsHandler extends SimpleHandler {
        private final List<Multipart> parts = new ArrayList<Multipart>();

        private String bucketName;

        private String objectKey;

        private String uploadId;

        private Owner initiator;

        private Owner owner;

        private String storageClass;

        private String partNumberMarker;

        private String nextPartNumberMarker;

        private int maxParts;

        private boolean isTruncated = false;

        private boolean isInInitiator = false;

        public ListPartsHandler(XMLReader xr) {
            super(xr);
        }

        public List<Multipart> getMultiPartList() {
            return parts;
        }

        public boolean isTruncated() {
            return isTruncated;
        }

        public String getBucketName() {
            return bucketName;
        }

        public String getObjectKey() {
            return objectKey;
        }

        public String getUploadId() {
            return uploadId;
        }

        public Owner getInitiator() {
            return initiator;
        }

        public Owner getOwner() {
            return owner;
        }

        public String getStorageClass() {
            return storageClass;
        }

        public String getPartNumberMarker() {
            return partNumberMarker;
        }

        public String getNextPartNumberMarker() {
            return nextPartNumberMarker;
        }

        public int getMaxParts() {
            return maxParts;
        }

        public void startPart() {
            transferControl(new PartResultHandler(xr));
        }

        @Override
        public void controlReturned(SimpleHandler childHandler) {
            if (childHandler instanceof PartResultHandler) {
                parts.add(((PartResultHandler) childHandler).getMultipartPart());
            } else {
                if (isInInitiator) {
                    initiator = ((OwnerHandler) childHandler).getOwner();
                } else {
                    owner = ((OwnerHandler) childHandler).getOwner();
                }
            }
        }

        public void startInitiator() {
            isInInitiator = true;
            transferControl(new OwnerHandler(xr));
        }

        public void startOwner() {
            isInInitiator = false;
            transferControl(new OwnerHandler(xr));
        }

        public void endBucket(String content) {
            this.bucketName = content;
        }

        public void endKey(String content) {
            this.objectKey = content;
        }

        public void endStorageClass(String content) {
            this.storageClass = content;
        }

        public void endUploadId(String content) {
            this.uploadId = content;
        }

        public void endPartNumberMarker(String content) {
            this.partNumberMarker = content;
        }

        public void endNextPartNumberMarker(String content) {
            this.nextPartNumberMarker = content;
        }

        public void endMaxParts(String content) {
            this.maxParts = Integer.parseInt(content);
        }

        public void endIsTruncated(String content) {
            this.isTruncated = Boolean.parseBoolean(content);
        }
    }

    public static class CompleteMultipartUploadHandler extends SimpleHandler {

        private String location;

        private String bucketName;

        private String objectKey;

        private String etag;

        public CompleteMultipartUploadHandler(XMLReader xr) {
            super(xr);
        }

        public void endLocation(String content) {
            this.location = content;
        }

        public void endBucket(String content) {
            this.bucketName = content;
        }

        public void endKey(String content) {
            this.objectKey = content;
        }

        public void endETag(String content) {
            this.etag = content;
        }

        public String getLocation() {
            return location;
        }

        public String getBucketName() {
            return bucketName;
        }

        public String getObjectKey() {
            return objectKey;
        }

        public String getEtag() {
            return etag;
        }

    }

    public static class BucketWebsiteConfigurationHandler extends DefaultXmlHandler {
        private WebsiteConfiguration config = new WebsiteConfiguration();

        private Redirect currentRedirectRule;

        private RedirectAllRequest currentRedirectAllRule;

        private RouteRule currentRoutingRule;

        private RouteRuleCondition currentCondition;

        public WebsiteConfiguration getWebsiteConfig() {
            return config;
        }

        @Override
        public void startElement(String name) {
            if (name.equals("RedirectAllRequestsTo")) {
                currentRedirectAllRule = new RedirectAllRequest();
                this.config.setRedirectAllRequestsTo(currentRedirectAllRule);
            } else if (name.equals("RoutingRule")) {
                currentRoutingRule = new RouteRule();
                this.config.getRouteRules().add(currentRoutingRule);
            } else if (name.equals("Condition")) {
                currentCondition = new RouteRuleCondition();
                currentRoutingRule.setCondition(currentCondition);
            } else if (name.equals("Redirect")) {
                currentRedirectRule = new Redirect();
                currentRoutingRule.setRedirect(currentRedirectRule);
            }
        }

        @Override
        public void endElement(String name, String content) {
            if (null != config) {
                if (name.equals("Suffix")) {
                    config.setSuffix(content);
                } else if (name.equals("Key")) {
                    config.setKey(content);
                }
            }

            if (null != currentCondition) {
                if (name.equals("KeyPrefixEquals")) {
                    currentCondition.setKeyPrefixEquals(content);
                } else if (name.equals("HttpErrorCodeReturnedEquals")) {
                    currentCondition.setHttpErrorCodeReturnedEquals(content);
                }
            }

            if (name.equals("Protocol")) {
                if (currentRedirectAllRule != null) {
                    currentRedirectAllRule.setRedirectProtocol(ProtocolEnum.getValueFromCode(content));
                } else if (currentRedirectRule != null) {
                    currentRedirectRule.setRedirectProtocol(ProtocolEnum.getValueFromCode(content));
                }
            } else if (name.equals("HostName")) {
                if (currentRedirectAllRule != null) {
                    currentRedirectAllRule.setHostName(content);
                } else if (currentRedirectRule != null) {
                    currentRedirectRule.setHostName(content);
                }
            }

            if (null != currentRedirectRule) {
                if (name.equals("ReplaceKeyPrefixWith")) {
                    currentRedirectRule.setReplaceKeyPrefixWith(content);
                } else if (name.equals("ReplaceKeyWith")) {
                    currentRedirectRule.setReplaceKeyWith(content);
                } else if (name.equals("HttpRedirectCode")) {
                    currentRedirectRule.setHttpRedirectCode(content);
                }
            }
        }
    }

    public static class DeleteObjectsHandler extends DefaultXmlHandler {

        private DeleteObjectsResult result;

        private List<DeleteObjectsResult.DeleteObjectResult> deletedObjectResults = new ArrayList<DeleteObjectsResult.DeleteObjectResult>();

        private List<DeleteObjectsResult.ErrorResult> errorResults = new ArrayList<DeleteObjectsResult.ErrorResult>();

        private String key;

        private String version;

        private String deleteMarkerVersion;

        private String errorCode;

        private String message;

        private boolean withDeleteMarker;

        public DeleteObjectsResult getMultipleDeleteResult() {
            return result;
        }

        @Override
        public void startElement(String name) {
            if (name.equals("DeleteResult")) {
                result = new DeleteObjectsResult();
            }
        }

        @Override
        public void endElement(String name, String content) {
            if ("Key".equals(name)) {
                key = content;
            } else if ("VersionId".equals(name)) {
                version = content;
            } else if ("DeleteMarker".equals(name)) {
                withDeleteMarker = Boolean.parseBoolean(content);
            } else if ("DeleteMarkerVersionId".equals(name)) {
                deleteMarkerVersion = content;
            } else if ("Code".equals(name)) {
                errorCode = content;
            } else if ("Message".equals(name)) {
                message = content;
            } else if ("Deleted".equals(name)) {
                DeleteObjectsResult.DeleteObjectResult r = new DeleteObjectsResult.DeleteObjectResult(key, version, withDeleteMarker,
                        deleteMarkerVersion);
                deletedObjectResults.add(r);
                key = version = deleteMarkerVersion = null;
                withDeleteMarker = false;
            } else if ("Error".equals(name)) {
                errorResults.add(new DeleteObjectsResult.ErrorResult(key, version, errorCode, message));
                key = version = errorCode = message = null;
            } else if (name.equals("DeleteResult")) {
                result.getDeletedObjectResults().addAll(deletedObjectResults);
                result.getErrorResults().addAll(errorResults);
            }
        }
    }

    public static class BucketTagInfoHandler extends DefaultXmlHandler {
        private BucketTagInfo tagInfo = new BucketTagInfo();

        private String currentKey;

        private String currentValue;

        public BucketTagInfo getBucketTagInfo() {
            return tagInfo;
        }

        @Override
        public void endElement(String name, String content) {
            if ("Key".equals(name)) {
                currentKey = content;
            } else if ("Value".equals(name)) {
                currentValue = content;
            } else if ("Tag".equals(name)) {
                tagInfo.getTagSet().addTag(currentKey, currentValue);
            }
        }

    }

    public static class BucketNotificationConfigurationHandler extends DefaultXmlHandler {

        private BucketNotificationConfiguration bucketNotificationConfiguration = new BucketNotificationConfiguration();

        private String id;

        private String urn;

        private AbstractNotification.Filter filter;

        private List<EventTypeEnum> events = new ArrayList<EventTypeEnum>();

        private String ruleName;

        private String ruleValue;

        public BucketNotificationConfiguration getBucketNotificationConfiguration() {
            return bucketNotificationConfiguration;
        }

        @Override
        public void startElement(String name) {
            if ("Filter".equals(name)) {
                filter = new AbstractNotification.Filter();
            }
        }

        @Override
        public void endElement(String name, String content) {
            if ("Id".equals(name)) {
                id = content;
            } else if ("Topic".equals(name) || "FunctionGraph".equals(name)) {
                urn = content;
            } else if ("Event".equals(name)) {
                events.add(EventTypeEnum.getValueFromCode(content));
            } else if ("Name".equals(name)) {
                ruleName = content;
            } else if ("Value".equals(name)) {
                ruleValue = content;
            } else if ("FilterRule".equals(name)) {
                if (null == filter) {
                    if (log.isErrorEnabled()) {
                        log.error("Response xml is not well-formt");
                    }
                    return;
                }
                filter.addFilterRule(ruleName, ruleValue);
            } else if ("TopicConfiguration".equals(name)) {
                if (null == bucketNotificationConfiguration) {
                    if (log.isErrorEnabled()) {
                        log.error("Response xml is not well-formt");
                    }
                    return;
                }
                bucketNotificationConfiguration.addTopicConfiguration(new TopicConfiguration(id, filter, urn, events));
                events = new ArrayList<EventTypeEnum>();
            } else if ("FunctionGraphConfiguration".equals(name)) {
                if (null == bucketNotificationConfiguration) {
                    if (log.isErrorEnabled()) {
                        log.error("Response xml is not well-formt");
                    }
                    return;
                }
                bucketNotificationConfiguration
                        .addFunctionGraphConfiguration(new FunctionGraphConfiguration(id, filter, urn, events));
                events = new ArrayList<EventTypeEnum>();
            }
        }

    }

    public static class BucketLifecycleConfigurationHandler extends SimpleHandler {
        private LifecycleConfiguration config = new LifecycleConfiguration();

        private LifecycleConfiguration.Rule latestRule;

        private LifecycleConfiguration.TimeEvent latestTimeEvent;

        public BucketLifecycleConfigurationHandler(XMLReader xr) {
            super(xr);
        }

        public LifecycleConfiguration getLifecycleConfig() {
            return config;
        }

        public void startExpiration() {
            latestTimeEvent = config.new Expiration();
            latestRule.setExpiration(((LifecycleConfiguration.Expiration) latestTimeEvent));
        }

        public void startNoncurrentVersionExpiration() {
            latestTimeEvent = config.new NoncurrentVersionExpiration();
            latestRule.setNoncurrentVersionExpiration(
                    ((LifecycleConfiguration.NoncurrentVersionExpiration) latestTimeEvent));
        }

        public void startTransition() {
            latestTimeEvent = config.new Transition();
            latestRule.getTransitions().add(((LifecycleConfiguration.Transition) latestTimeEvent));
        }

        public void startNoncurrentVersionTransition() {
            latestTimeEvent = config.new NoncurrentVersionTransition();
            latestRule.getNoncurrentVersionTransitions()
                    .add(((LifecycleConfiguration.NoncurrentVersionTransition) latestTimeEvent));
        }

        public void endStorageClass(String content) {
            LifecycleConfiguration.setStorageClass(latestTimeEvent, StorageClassEnum.getValueFromCode(content));
        }

        public void endDate(String content) throws ParseException {
            LifecycleConfiguration.setDate(latestTimeEvent, ServiceUtils.parseIso8601Date(content));
        }

        public void endNoncurrentDays(String content) {
            LifecycleConfiguration.setDays(latestTimeEvent, Integer.parseInt(content));
        }

        public void endDays(String content) {
            LifecycleConfiguration.setDays(latestTimeEvent, Integer.parseInt(content));
        }

        public void startRule() {
            latestRule = config.new Rule();
        }

        public void endID(String content) {
            latestRule.setId(content);
        }

        public void endPrefix(String content) {
            latestRule.setPrefix(content);
        }

        public void endStatus(String content) {
            latestRule.setEnabled("Enabled".equals(content));
        }

        public void endRule(String content) {
            config.addRule(latestRule);
        }
    }

    public static class AccessControlListHandler extends DefaultXmlHandler {
        protected AccessControlList accessControlList;

        protected Owner owner;
        protected GranteeInterface currentGrantee;
        protected Permission currentPermission;
        protected boolean currentDelivered;

        protected boolean insideACL = false;

        public AccessControlList getAccessControlList() {
            return accessControlList;
        }

        @Override
        public void startElement(String name) {
            if (name.equals("AccessControlPolicy")) {
                accessControlList = new AccessControlList();
            } else if (name.equals("Owner")) {
                owner = new Owner();
                accessControlList.setOwner(owner);
            } else if (name.equals("AccessControlList")) {
                insideACL = true;
            }
        }

        @Override
        public void endElement(String name, String content) {
            if (name.equals("ID") && !insideACL) {
                owner.setId(content);
            } else if (name.equals("DisplayName") && !insideACL) {
                owner.setDisplayName(content);
            } else if (name.equals("ID")) {
                currentGrantee = new CanonicalGrantee();
                currentGrantee.setIdentifier(content);
            } else if (name.equals("URI") || name.equals("Canned")) {
                currentGrantee = new GroupGrantee();
                currentGrantee.setIdentifier(content);
            } else if (name.equals("DisplayName")) {
                if (currentGrantee instanceof CanonicalGrantee) {
                    ((CanonicalGrantee) currentGrantee).setDisplayName(content);
                }
            } else if (name.equals("Permission")) {
                currentPermission = Permission.parsePermission(content);
            } else if (name.equals("Delivered")) {
                if (insideACL) {
                    currentDelivered = Boolean.parseBoolean(content);
                } else {
                    accessControlList.setDelivered(Boolean.parseBoolean(content));
                }
            } else if (name.equals("Grant")) {
                GrantAndPermission obj = accessControlList.grantPermission(currentGrantee, currentPermission);
                obj.setDelivered(currentDelivered);
            } else if (name.equals("AccessControlList")) {
                insideACL = false;
            }
        }

    }

    public static class BucketQuotaHandler extends DefaultXmlHandler {
        protected BucketQuota quota;

        public BucketQuota getQuota() {
            return quota;
        }

        @Override
        public void startElement(String name) {
            if (name.equals("Quota")) {
                quota = new BucketQuota();
            }
        }

        @Override
        public void endElement(String name, String content) {
            if (name.equals("StorageQuota")) {
                if (quota != null) {
                    quota.setBucketQuota(Long.parseLong(content));
                }
            }
        }
    }

    public static class BucketEncryptionHandler extends DefaultXmlHandler {
        protected BucketEncryption encryption;

        public BucketEncryption getEncryption() {
            return encryption;
        }

        @Override
        public void startElement(String name) {
            if (name.equals("ApplyServerSideEncryptionByDefault")) {
                encryption = new BucketEncryption();
            }
        }

        @Override
        public void endElement(String name, String content) {
            if (null == encryption) {
                if (log.isWarnEnabled()) {
                    log.warn("Response xml is not well-formt");
                }
                return;
            }
            if (name.equals("SSEAlgorithm")) {
                encryption.setSseAlgorithm(SSEAlgorithmEnum.getValueFromCode(content.replace("aws:", "")));
            } else if (name.equals("KMSMasterKeyID")) {
                encryption.setKmsKeyId(content);
            }
        }
    }

    public static class BucketStoragePolicyHandler extends DefaultXmlHandler {
        protected BucketStoragePolicyConfiguration storagePolicyConfiguration;

        public BucketStoragePolicyConfiguration getStoragePolicy() {
            return storagePolicyConfiguration;
        }

        @Override
        public void startElement(String name) {
            if (name.equals("StoragePolicy") || name.equals("StorageClass")) {
                storagePolicyConfiguration = new BucketStoragePolicyConfiguration();
            }
        }

        @Override
        public void endElement(String name, String content) {
            if (name.equals("DefaultStorageClass") || name.equals("StorageClass")) {
                if (storagePolicyConfiguration != null) {
                    storagePolicyConfiguration.setBucketStorageClass(StorageClassEnum.getValueFromCode(content));
                }
            }
        }
    }

    public static class BucketStorageInfoHandler extends DefaultXmlHandler {
        private BucketStorageInfo storageInfo;

        public BucketStorageInfo getStorageInfo() {
            return storageInfo;
        }

        @Override
        public void startElement(String name) {
            if (name.equals("GetBucketStorageInfoResult")) {
                storageInfo = new BucketStorageInfo();
            }
        }

        @Override
        public void endElement(String name, String content) {
            if (null == storageInfo) {
                if (log.isWarnEnabled()) {
                    log.warn("Response xml is not well-formt");
                }
                return;
            }

            if (name.equals("Size")) {
                storageInfo.setSize(Long.parseLong(content));
            } else if (name.equals("ObjectNumber")) {
                storageInfo.setObjectNumber(Long.parseLong(content));
            }
        }
    }

    public static class BucketReplicationConfigurationHandler extends DefaultXmlHandler {
        private ReplicationConfiguration replicationConfiguration = new ReplicationConfiguration();

        private ReplicationConfiguration.Rule currentRule;

        public ReplicationConfiguration getReplicationConfiguration() {
            return replicationConfiguration;
        }

        @Override
        public void startElement(String name) {
            if ("Rule".equals(name)) {
                currentRule = new ReplicationConfiguration.Rule();
            } else if ("Destination".equals(name)) {
                currentRule.setDestination(new ReplicationConfiguration.Destination());
            }
        }

        @Override
        public void endElement(String name, String content) {
            if (null != replicationConfiguration) {
                if ("Agency".equals(name)) {
                    replicationConfiguration.setAgency(content);
                } else if ("Rule".equals(name)) {
                    replicationConfiguration.getRules().add(currentRule);
                }
            }

            if (null == currentRule) {
                if (log.isErrorEnabled()) {
                    log.error("Response xml is not well-formt");
                }
                return;
            }

            if ("ID".equals(name)) {
                currentRule.setId(content);
            } else if ("Status".equals(name)) {
                currentRule.setStatus(RuleStatusEnum.getValueFromCode(content));
            } else if ("Prefix".equals(name)) {
                currentRule.setPrefix(content);
            } else if ("Bucket".equals(name)) {
                currentRule.getDestination().setBucket(content);
            } else if ("StorageClass".equals(name)) {
                currentRule.getDestination().setObjectStorageClass(StorageClassEnum.getValueFromCode(content));
            } else if ("HistoricalObjectReplication".equals(name)) {
                currentRule
                        .setHistoricalObjectReplication(HistoricalObjectReplicationEnum.getValueFromCode(content));
            }
        }
    }

    public static class BucketDirectColdAccessHandler extends DefaultXmlHandler {
        private BucketDirectColdAccess access = new BucketDirectColdAccess();

        public BucketDirectColdAccess getBucketDirectColdAccess() {
            return access;
        }

        @Override
        public void endElement(String name, String elementText) {
            if ("Status".equals(name)) {
                access.setStatus(RuleStatusEnum.getValueFromCode(elementText));
            }
        }

    }
}
