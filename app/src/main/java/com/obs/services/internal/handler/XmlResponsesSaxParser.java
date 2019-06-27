/**
 * 
 * JetS3t : Java S3 Toolkit
 * Project hosted at http://bitbucket.org/jmurty/jets3t/
 *
 * Copyright 2006-2010 James Murty
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
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
import com.obs.services.model.BucketVersioningConfiguration;
import com.obs.services.model.CanonicalGrantee;
import com.obs.services.model.CopyPartResult;
import com.obs.services.model.DeleteObjectsResult;
import com.obs.services.model.EventTypeEnum;
import com.obs.services.model.FunctionGraphConfiguration;
import com.obs.services.model.GrantAndPermission;
import com.obs.services.model.GranteeInterface;
import com.obs.services.model.GroupGrantee;
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
import com.obs.services.model.RouteRule;
import com.obs.services.model.RouteRuleCondition;
import com.obs.services.model.RuleStatusEnum;
import com.obs.services.model.SSEAlgorithmEnum;
import com.obs.services.model.StorageClassEnum;
import com.obs.services.model.TopicConfiguration;
import com.obs.services.model.VersionOrDeleteMarker;
import com.obs.services.model.VersioningStatusEnum;
import com.obs.services.model.WebsiteConfiguration;

public class XmlResponsesSaxParser {
	
	private static final ILogger log = LoggerBuilder.getLogger("com.obs.services.internal.RestStorageService");

	private XMLReader xr;

	public XmlResponsesSaxParser() throws ServiceException {
		this.xr = ServiceUtils.loadXMLReader();
	}

	protected void parseXmlInputStream(DefaultHandler handler, InputStream inputStream) throws ServiceException {
		if (inputStream == null) {
			return;
		}
		try {
			if (log.isDebugEnabled()) {
				log.debug("Parsing XML response document with handler: " + handler.getClass());
			}
			xr.setContentHandler(handler);
			xr.setErrorHandler(handler);
			xr.parse(new InputSource(inputStream));
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
			// Replace any carriage return (\r) characters with explicit XML
			// character entities, to prevent the SAX parser from
			// misinterpreting 0x0D characters as 0x0A.
			String listingDoc = listingDocBuffer.toString().replaceAll("\r", "&#013;");
			if(log.isTraceEnabled()) {
				log.trace("Response entity: " + listingDoc);
			}
			return new ByteArrayInputStream(listingDoc.getBytes(Constants.DEFAULT_ENCODING));
		} catch (Throwable t) {
			throw new ServiceException("Failed to sanitize XML document destined",
					t);
		} finally {
			ServiceUtils.closeStream(br);
			ServiceUtils.closeStream(inputStream);
		}
	}
	
	public <T>T parse(InputStream inputStream, Class<T> handlerClass, boolean sanitize) throws ServiceException{
		try {
			T handler = null;
			if(SimpleHandler.class.isAssignableFrom(handlerClass)) {
				Constructor<T> c = handlerClass.getConstructor(XMLReader.class);
				handler = c.newInstance(this.xr);
			}else {
				handler = handlerClass.getConstructor().newInstance();
			}
			if(handler instanceof DefaultHandler) {
				if(sanitize) {
					inputStream = sanitizeXmlDocument(inputStream);
				}
				parseXmlInputStream((DefaultHandler)handler, inputStream);
			}
			return handler;
		}catch (ServiceException e) {
			throw e;
		}catch(Exception e) {
			throw new ServiceException(e);
		}
	}


	public static class ListObjectsHandler extends DefaultXmlHandler {
		private ObsObject currentObject;
		
		private Owner currentOwner;

		private boolean insideCommonPrefixes = false;

		private final List<ObsObject> objects = new ArrayList<ObsObject>();

		private final List<String> commonPrefixes = new ArrayList<String>();

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
			}
		}

		@Override
		public void endElement(String name, String elementText) {
			try {
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
				}else if (name.equals("Contents")) {
					objects.add(currentObject);
				} else if (name.equals("Key")) {
					currentObject.setObjectKey(elementText);
					lastKey = elementText;
				} else if (name.equals("LastModified")) {
					try {
						currentObject.getMetadata().setLastModified(ServiceUtils.parseIso8601Date(elementText));
					} catch (ParseException e) {
						if(log.isErrorEnabled()) {
							log.error("Non-ISO8601 date for LastModified in bucket's object listing output: " + elementText, e);
						}
					}
				} else if (name.equals("ETag")) {
					currentObject.getMetadata().setEtag(elementText);
				} else if (name.equals("Size")) {
					currentObject.getMetadata().setContentLength(Long.parseLong(elementText));
				} else if (name.equals("StorageClass")) {
					currentObject.getMetadata().setObjectStorageClass(StorageClassEnum.getValueFromCode(elementText));
				}else if (name.equals("ID")) {
					if (currentOwner == null) {
						currentOwner = new Owner();
					}
					currentObject.setOwner(currentOwner);
					currentOwner.setId(elementText);
				}else if(name.equals("Type")) {
					currentObject.getMetadata().setAppendable("Appendable".equals(elementText));
				} else if (name.equals("DisplayName")) {
					if(currentOwner != null) {
						currentOwner.setDisplayName(elementText);
					}
				}else if (insideCommonPrefixes && name.equals("Prefix")) {
					commonPrefixes.add(elementText);
				} else if (name.equals("CommonPrefixes")) {
					insideCommonPrefixes = false;
				}
			}catch (NullPointerException e) {
				if(log.isErrorEnabled()) {
					log.error("Response xml is not well-formt", e);
				}
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
			try {
				if (name.equals("ID")) {
					bucketsOwner.setId(elementText);
				} else if (name.equals("DisplayName")) {
					bucketsOwner.setDisplayName(elementText);
				} else if (name.equals("Bucket")) {
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
						if(log.isWarnEnabled()) {
							log.warn("Non-ISO8601 date for CreationDate in list buckets output: " + elementText, e);
						}
					}
				}
			}catch (NullPointerException e) {
				if(log.isErrorEnabled()) {
					log.error("Response xml is not well-formt", e);
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
				if(currentGrantee instanceof CanonicalGrantee) {
					((CanonicalGrantee) currentGrantee).setDisplayName(elementText);
				}
			} else if(name.equals("Delivered")) {
				currentDelivered = Boolean.parseBoolean(elementText);
			}  else if (name.equals("Permission")) {
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
			return lastModified;
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
					if(log.isErrorEnabled()) {
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
		public void endElement(String name, String elementText) {
			try {
				if (name.equals("Name")) {
					bucketName = elementText;
				} else if (!insideCommonPrefixes && name.equals("Prefix")) {
					requestPrefix = elementText;
				} else if (name.equals("KeyMarker")) {
					keyMarker = elementText;
				} else if (name.equals("NextKeyMarker")) {
					nextMarker = elementText;
				} else if (name.equals("VersionIdMarker")) {
					versionIdMarker = elementText;
				} else if (name.equals("NextVersionIdMarker")) {
					nextVersionIdMarker = elementText;
				} else if (name.equals("MaxKeys")) {
					requestMaxKeys = Long.parseLong(elementText);
				} else if (name.equals("IsTruncated")) {
					listingTruncated = Boolean.valueOf(elementText);
				} else if (name.equals("Delimiter")) {
					delimiter = elementText;
				} else if (name.equals("Version")) {
					VersionOrDeleteMarker item = new VersionOrDeleteMarker(bucketName, key, versionId, isLatest, lastModified, owner, etag, size, StorageClassEnum.getValueFromCode(storageClass), false, isAppendable);
					items.add(item);
					this.reset();
				} else if (name.equals("DeleteMarker")) {
					VersionOrDeleteMarker item = new VersionOrDeleteMarker(bucketName, key, versionId, isLatest, lastModified, owner, null, 0, null, true, false);
					items.add(item);
					this.reset();
				} else if (name.equals("Key")) {
					key = elementText;
				} else if (name.equals("VersionId")) {
					versionId = elementText;
				} else if (name.equals("IsLatest")) {
					isLatest = Boolean.valueOf(elementText);
				} else if (name.equals("LastModified")) {
					try {
						lastModified = ServiceUtils.parseIso8601Date(elementText);
					} catch (ParseException e) {
						if(log.isWarnEnabled()) {
							log.warn("Non-ISO8601 date for LastModified in bucket's versions listing output: " + elementText, e);
						}
					}
				} else if (name.equals("ETag")) {
					etag = elementText;
				} else if (name.equals("Size")) {
					size = Long.parseLong(elementText);
				} else if (name.equals("StorageClass")) {
					storageClass = elementText;
				} else if (name.equals("Type")) {
					isAppendable = "Appendable".equals(elementText);
				} else if (name.equals("ID")) {
					if(owner == null) {
						owner = new Owner();
					}
					owner.setId(elementText);
				} else if (name.equals("DisplayName")) {
					if(owner != null) {
						owner.setDisplayName(elementText);
					}
				}else if (insideCommonPrefixes && name.equals("Prefix")) {
					commonPrefixes.add(elementText);
				} else if (name.equals("CommonPrefixes")) {
					insideCommonPrefixes = false;
				}
			}catch (NullPointerException e) {
				if(log.isErrorEnabled()) {
					log.error("Response xml is not well-formt", e);
				}
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

		public void endID(String text) {
			this.id = text;
		}

		public void endDisplayName(String text) {
			this.displayName = text;
		}

		public void endOwner(String text) {
			returnControlToParentHandler();
		}

		public void endInitiator(String text) {
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

		public void endUploadId(String text) {
			this.uploadId = text;
		}

		public void endBucket(String text) {
			this.bucketName = text;
		}

		public void endKey(String text) {
			this.objectKey = text;
		}
	}
	
	public static class MultipartUploadHandler extends SimpleHandler {
		private String uploadId;

		private String objectKey;

		private String storageClass;

		private Owner owner;

		private Owner initiator;

		private Date initiatedDate;

		private boolean inInitiator = false;

		public MultipartUploadHandler(XMLReader xr) {
			super(xr);
		}

		public MultipartUpload getMultipartUpload() {
			MultipartUpload multipartUpload = new MultipartUpload(uploadId, objectKey, initiatedDate, StorageClassEnum.getValueFromCode(storageClass), owner, initiator);
			return multipartUpload;
		}

		public void endUploadId(String text) {
			this.uploadId = text;
		}

		public void endKey(String text) {
			this.objectKey = text;
		}

		public void endStorageClass(String text) {
			this.storageClass = text;
		}

		public void endInitiated(String text) {
			try {
				this.initiatedDate = ServiceUtils.parseIso8601Date(text);
			} catch (ParseException e) {
			}
		}

		public void startOwner() {
			inInitiator = false;
			transferControlToHandler(new OwnerHandler(xr));
		}

		public void startInitiator() {
			inInitiator = true;
			transferControlToHandler(new OwnerHandler(xr));
		}

		@Override
		public void controlReturned(SimpleHandler childHandler) {
			if (inInitiator) {
				this.owner = ((OwnerHandler) childHandler).getOwner();
			} else {
				this.initiator = ((OwnerHandler) childHandler).getOwner();
			}
		}
		public void endUpload(String text) {
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
			transferControlToHandler(new MultipartUploadHandler(xr));
		}

		public void startCommonPrefixes() {
			insideCommonPrefixes = true;
		}

		@Override
		public void controlReturned(SimpleHandler childHandler) {
			uploads.add(((MultipartUploadHandler) childHandler).getMultipartUpload());
		}

		public void endDelimiter(String text) {
			this.delimiter = text;
		}

		public void endBucket(String text) {
			this.bucketName = text;
		}

		public void endKeyMarker(String text) {
			this.keyMarker = text;
		}

		public void endUploadIdMarker(String text) {
			this.uploadIdMarker = text;
		}

		public void endNextKeyMarker(String text) {
			this.nextKeyMarker = text;
		}

		public void endNextUploadIdMarker(String text) {
			this.nextUploadIdMarker = text;
		}

		public void endMaxUploads(String text) {
			try {
				this.maxUploads = Integer.parseInt(text);
			}catch (Exception e) {
				if(log.isErrorEnabled()) {
					log.error("Response xml is not well-format", e);
				}
			}
		}

		public void endIsTruncated(String text) {
			this.isTruncated = Boolean.parseBoolean(text);
		}

		public void endPrefix(String text) {
			if (insideCommonPrefixes) {
				commonPrefixes.add(text);
			}else {
				this.prefix = text;
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

		public void endLastModified(String text) {
			try {
				this.lastModified = ServiceUtils.parseIso8601Date(text);
			} catch (ParseException e) {
			}
		}

		public void endETag(String text) {
			this.etag = text;
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

		public void endPartNumber(String text) {
			this.partNumber = Integer.parseInt(text);
		}

		public void endLastModified(String text) {
			try {
				this.lastModified = ServiceUtils.parseIso8601Date(text);
			} catch (ParseException e) {
			}
		}

		public void endETag(String text) {
			this.etag = text;
		}

		public void endSize(String text) {
			this.size = Long.parseLong(text);
		}

		public void endPart(String text) {
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

		private boolean inInitiator = false;

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
			transferControlToHandler(new PartResultHandler(xr));
		}

		@Override
		public void controlReturned(SimpleHandler childHandler) {
			if (childHandler instanceof PartResultHandler) {
				parts.add(((PartResultHandler) childHandler).getMultipartPart());
			} else {
				if (inInitiator) {
					initiator = ((OwnerHandler) childHandler).getOwner();
				} else {
					owner = ((OwnerHandler) childHandler).getOwner();
				}
			}
		}

		public void startInitiator() {
			inInitiator = true;
			transferControlToHandler(new OwnerHandler(xr));
		}

		public void startOwner() {
			inInitiator = false;
			transferControlToHandler(new OwnerHandler(xr));
		}

		public void endBucket(String text) {
			this.bucketName = text;
		}

		public void endKey(String text) {
			this.objectKey = text;
		}

		public void endStorageClass(String text) {
			this.storageClass = text;
		}

		public void endUploadId(String text) {
			this.uploadId = text;
		}

		public void endPartNumberMarker(String text) {
			this.partNumberMarker = text;
		}

		public void endNextPartNumberMarker(String text) {
			this.nextPartNumberMarker = text;
		}

		public void endMaxParts(String text) {
			this.maxParts = Integer.parseInt(text);
		}

		public void endIsTruncated(String text) {
			this.isTruncated = Boolean.parseBoolean(text);
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

		public void endLocation(String text) {
			this.location = text;
		}

		public void endBucket(String text) {
			this.bucketName = text;
		}

		public void endKey(String text) {
			this.objectKey = text;
		}

		public void endETag(String text) {
			this.etag = text;
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
			}else if (name.equals("RoutingRule")) {
				currentRoutingRule = new RouteRule();
				this.config.getRouteRules().add(currentRoutingRule);
			}else if (name.equals("Condition")) {
				currentCondition = new RouteRuleCondition();
				currentRoutingRule.setCondition(currentCondition);
			}else if (name.equals("Redirect")) {
				currentRedirectRule = new Redirect();
				currentRoutingRule.setRedirect(currentRedirectRule);
			}
		}

		@Override
		public void endElement(String name, String elementText) {
			try {
				if (name.equals("Suffix")) {
					config.setSuffix(elementText);
				}else if (name.equals("Key")) {
					config.setKey(elementText);
				}else if (name.equals("KeyPrefixEquals")) {
					currentCondition.setKeyPrefixEquals(elementText);
				} else if (name.equals("HttpErrorCodeReturnedEquals")) {
					currentCondition.setHttpErrorCodeReturnedEquals(elementText);
				}else if (name.equals("Protocol")) {
					if (currentRedirectAllRule != null) {
						currentRedirectAllRule.setRedirectProtocol(ProtocolEnum.getValueFromCode(elementText));
					} else if (currentRedirectRule != null) {
						currentRedirectRule.setRedirectProtocol(ProtocolEnum.getValueFromCode(elementText));
					}
				} else if (name.equals("HostName")) {
					if (currentRedirectAllRule != null) {
						currentRedirectAllRule.setHostName(elementText);
					} else if (currentRedirectRule != null) {
						currentRedirectRule.setHostName(elementText);
					}
				} else if (name.equals("ReplaceKeyPrefixWith")) {
					currentRedirectRule.setReplaceKeyPrefixWith(elementText);
				} else if (name.equals("ReplaceKeyWith")) {
					currentRedirectRule.setReplaceKeyWith(elementText);
				} else if (name.equals("HttpRedirectCode")) {
					currentRedirectRule.setHttpRedirectCode(elementText);
				}
			}catch (NullPointerException e) {
				if(log.isErrorEnabled()) {
					log.error("Response xml is not well-formt", e);
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
		public void endElement(String name, String elementText) {
			if ("Key".equals(name)) {
				key = elementText;
			} else if ("VersionId".equals(name)) {
				version = elementText;
			} else if ("DeleteMarker".equals(name)) {
				withDeleteMarker = Boolean.parseBoolean(elementText);
			} else if ("DeleteMarkerVersionId".equals(name)) {
				deleteMarkerVersion = elementText;
			} else if ("Code".equals(name)) {
				errorCode = elementText;
			} else if ("Message".equals(name)) {
				message = elementText;
			}else if ("Deleted".equals(name)) {
				DeleteObjectsResult.DeleteObjectResult r = result.new DeleteObjectResult(key, version, withDeleteMarker, deleteMarkerVersion);
				deletedObjectResults.add(r);
				key = version = deleteMarkerVersion = null;
				withDeleteMarker = false;
			} else if ("Error".equals(name)) {
				errorResults.add(result.new ErrorResult(key, version, errorCode, message));
				key = version = errorCode = message = null;
			}else if (name.equals("DeleteResult")) {
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
		public void endElement(String name, String elementText) {
			if ("Key".equals(name)) {
				currentKey = elementText;
			} else if ("Value".equals(name)) {
				currentValue = elementText;
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
		public void endElement(String name, String elementText) {
			try {
				if ("Id".equals(name)) {
				    id = elementText;
				} else if ("Topic".equals(name) || "FunctionGraph".equals(name)) {
				    urn = elementText;
				} else if ("Event".equals(name)) {
					events.add(EventTypeEnum.getValueFromCode(elementText));
				} else if ("Name".equals(name)) {
					ruleName = elementText;
				} else if ("Value".equals(name)) {
					ruleValue = elementText;
				} else if ("FilterRule".equals(name)) {
				    filter.addFilterRule(ruleName, ruleValue);
				} else if ("TopicConfiguration".equals(name)) {
					bucketNotificationConfiguration.addTopicConfiguration(new TopicConfiguration(id, filter, urn, events));
					events = new ArrayList<EventTypeEnum>();
				} else if ("FunctionGraphConfiguration".equals(name)) {
				    bucketNotificationConfiguration.addFunctionGraphConfiguration(new FunctionGraphConfiguration(id, filter, urn, events));
				    events = new ArrayList<EventTypeEnum>();
				}
			} catch (NullPointerException e) {
				if(log.isErrorEnabled()) {
					log.error("Response xml is not well-formt", e);
				}
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

		public void endStorageClass(String text) {
			LifecycleConfiguration.setStorageClass(latestTimeEvent, StorageClassEnum.getValueFromCode(text));
		}

		public void endDate(String text) throws ParseException {
			LifecycleConfiguration.setDate(latestTimeEvent, ServiceUtils.parseIso8601Date(text));
		}

		public void endNoncurrentDays(String text) {
			LifecycleConfiguration.setDays(latestTimeEvent, Integer.parseInt(text));
		}

		public void endDays(String text) {
			LifecycleConfiguration.setDays(latestTimeEvent, Integer.parseInt(text));
		}

		public void startRule() {
			latestRule = config.new Rule();
		}

		public void endID(String text) {
			latestRule.setId(text);
		}

		public void endPrefix(String text) {
			latestRule.setPrefix(text);
		}

		public void endStatus(String text) {
			latestRule.setEnabled("Enabled".equals(text));
		}

		public void endRule(String text) {
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
			if(name.equals("AccessControlPolicy")) {
				accessControlList = new AccessControlList();
			}else if (name.equals("Owner")) {
				owner = new Owner();
				accessControlList.setOwner(owner);
			} else if (name.equals("AccessControlList")) {
				insideACL = true;
			}
		}
		@Override
		public void endElement(String name, String elementText) {
			if (name.equals("ID") && !insideACL) {
				owner.setId(elementText);
			} else if (name.equals("DisplayName") && !insideACL) {
				owner.setDisplayName(elementText);
			}else if (name.equals("ID")) {
				currentGrantee = new CanonicalGrantee();
				currentGrantee.setIdentifier(elementText);
			} else if (name.equals("URI") || name.equals("Canned")) {
				currentGrantee = new GroupGrantee();
				currentGrantee.setIdentifier(elementText);
			}else if (name.equals("DisplayName")) {
				if (currentGrantee instanceof CanonicalGrantee) {
					((CanonicalGrantee) currentGrantee).setDisplayName(elementText);
				}
			} else if (name.equals("Permission")) {
				currentPermission = Permission.parsePermission(elementText);
			} else if(name.equals("Delivered")) {
				if(insideACL) {
					currentDelivered = Boolean.parseBoolean(elementText);
				}else {
					accessControlList.setDelivered(Boolean.parseBoolean(elementText));
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
		public void endElement(String name, String elementText) {
			if (name.equals("StorageQuota")) {
				if(quota != null) {
					quota.setBucketQuota(Long.parseLong(elementText));
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
            if(name.equals("ApplyServerSideEncryptionByDefault")) {
                encryption = new BucketEncryption();
            }
        }

        @Override
        public void endElement(String name, String elementText) {
            try {
                if (name.equals("SSEAlgorithm")) {
                    encryption.setSseAlgorithm(SSEAlgorithmEnum.getValueFromCode(elementText.replace("aws:", "")));
                } else if(name.equals("KMSMasterKeyID")) {
                    encryption.setKmsKeyId(elementText);
                }
            } catch (NullPointerException e) {
                if(log.isWarnEnabled()) {
                    log.warn("Response xml is not well-formt", e);
                }
            }
        }
	    
	}
	
	public static class BucketStoragePolicyHandler extends DefaultXmlHandler{
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
		public void endElement(String name, String elementText) {
			if (name.equals("DefaultStorageClass") || name.equals("StorageClass")) {
				if(storagePolicyConfiguration != null) {
					storagePolicyConfiguration.setBucketStorageClass(StorageClassEnum.getValueFromCode(elementText));
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
		public void endElement(String name, String elementText) {
			try {
				if (name.equals("Size")) {
					storageInfo.setSize(Long.parseLong(elementText));
				} else if (name.equals("ObjectNumber")) {
					storageInfo.setObjectNumber(Long.parseLong(elementText));
				}
			}catch (NullPointerException e) {
				if(log.isWarnEnabled()) {
					log.warn("Response xml is not well-formt", e);
				}
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
		public void endElement(String name, String elementText) {
			try {
				if ("Agency".equals(name)) {
					replicationConfiguration.setAgency(elementText);
				} else if ("Rule".equals(name)) {
					replicationConfiguration.getRules().add(currentRule);
				} else if ("ID".equals(name)) {
					currentRule.setId(elementText);
				} else if ("Status".equals(name)) {
					currentRule.setStatus(RuleStatusEnum.getValueFromCode(elementText));
				} else if ("Prefix".equals(name)) {
					currentRule.setPrefix(elementText);
				} else if ("Bucket".equals(name)) {
					currentRule.getDestination().setBucket(elementText);
				} else if ("StorageClass".equals(name)) {
					currentRule.getDestination().setObjectStorageClass(StorageClassEnum.getValueFromCode(elementText));
				}
			}catch (NullPointerException e) {
				if(log.isErrorEnabled()) {
					log.error("Response xml is not well-formt", e);
				}
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
