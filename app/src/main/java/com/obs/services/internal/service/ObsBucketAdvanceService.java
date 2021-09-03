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


package com.obs.services.internal.service;

import java.util.HashMap;
import java.util.Map;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.internal.Constants;
import com.obs.services.internal.ServiceException;
import com.obs.services.internal.Constants.CommonHeaders;
import com.obs.services.internal.handler.XmlResponsesSaxParser;
import com.obs.services.internal.io.HttpMethodReleaseInputStream;
import com.obs.services.internal.utils.Mimetypes;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.AccessControlList;
import com.obs.services.model.AuthTypeEnum;
import com.obs.services.model.BaseBucketRequest;
import com.obs.services.model.BucketCors;
import com.obs.services.model.BucketCustomDomainInfo;
import com.obs.services.model.BucketDirectColdAccess;
import com.obs.services.model.BucketEncryption;
import com.obs.services.model.BucketLoggingConfiguration;
import com.obs.services.model.BucketNotificationConfiguration;
import com.obs.services.model.BucketQuota;
import com.obs.services.model.BucketTagInfo;
import com.obs.services.model.BucketVersioningConfiguration;
import com.obs.services.model.DeleteBucketCustomDomainRequest;
import com.obs.services.model.GrantAndPermission;
import com.obs.services.model.GroupGrantee;
import com.obs.services.model.HeaderResponse;
import com.obs.services.model.LifecycleConfiguration;
import com.obs.services.model.Permission;
import com.obs.services.model.ReplicationConfiguration;
import com.obs.services.model.RequestPaymentConfiguration;
import com.obs.services.model.SetBucketAclRequest;
import com.obs.services.model.SetBucketCorsRequest;
import com.obs.services.model.SetBucketCustomDomainRequest;
import com.obs.services.model.GetBucketCustomDomainRequest;
import com.obs.services.model.SetBucketDirectColdAccessRequest;
import com.obs.services.model.SetBucketEncryptionRequest;
import com.obs.services.model.SetBucketLifecycleRequest;
import com.obs.services.model.SetBucketLoggingRequest;
import com.obs.services.model.SetBucketNotificationRequest;
import com.obs.services.model.SetBucketQuotaRequest;
import com.obs.services.model.SetBucketReplicationRequest;
import com.obs.services.model.SetBucketRequestPaymentRequest;
import com.obs.services.model.SetBucketTaggingRequest;
import com.obs.services.model.SetBucketVersioningRequest;
import com.obs.services.model.SetBucketWebsiteRequest;
import com.obs.services.model.SpecialParamEnum;
import com.obs.services.model.WebsiteConfiguration;

import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class ObsBucketAdvanceService extends ObsBucketBaseService {
    private static final ILogger log = LoggerBuilder.getLogger(ObsBucketAdvanceService.class);
    
    protected HeaderResponse setBucketVersioningImpl(SetBucketVersioningRequest request) throws ServiceException {
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put(SpecialParamEnum.VERSIONING.getOriginalStringCode(), "");
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        String xml = this.getIConvertor().transVersioningConfiguration(request.getBucketName(),
                request.getStatus() != null ? request.getStatus().getCode() : null);

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParams,
                createRequestBody(Mimetypes.MIMETYPE_XML, xml), true);
        return this.build(response);
    }

    protected BucketVersioningConfiguration getBucketVersioningImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put(SpecialParamEnum.VERSIONING.getOriginalStringCode(), "");

        Response response = performRestGet(request.getBucketName(), null, requestParams,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(response);

        BucketVersioningConfiguration ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(response),
                XmlResponsesSaxParser.BucketVersioningHandler.class, false).getVersioningStatus();
        setHeadersAndStatus(ret, response);
        return ret;
    }
    
    protected HeaderResponse setBucketRequestPaymentImpl(SetBucketRequestPaymentRequest request)
            throws ServiceException {
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put(SpecialParamEnum.REQUEST_PAYMENT.getOriginalStringCode(), "");
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        String xml = this.getIConvertor().transRequestPaymentConfiguration(request.getBucketName(),
                request.getPayer() != null ? request.getPayer().getCode() : null);

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParams,
                createRequestBody(Mimetypes.MIMETYPE_XML, xml), true);
        return this.build(response);
    }

    protected RequestPaymentConfiguration getBucketRequestPaymentImpl(BaseBucketRequest request)
            throws ServiceException {
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put(SpecialParamEnum.REQUEST_PAYMENT.getOriginalStringCode(), "");

        Response response = performRestGet(request.getBucketName(), null, requestParams,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(response);

        RequestPaymentConfiguration ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(response),
                XmlResponsesSaxParser.RequestPaymentHandler.class, false).getRequestPaymentConfiguration();
        setHeadersAndStatus(ret, response);
        return ret;
    }
    
    protected HeaderResponse setBucketNotificationImpl(SetBucketNotificationRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.NOTIFICATION.getOriginalStringCode(), "");
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        String xml = this.getIConvertor()
                .transBucketNotificationConfiguration(request.getBucketNotificationConfiguration());

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, xml), true);

        return build(response);
    }
    
    protected BucketNotificationConfiguration getBucketNotificationConfigurationImpl(BaseBucketRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.NOTIFICATION.getOriginalStringCode(), "");
        Response httpResponse = performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        BucketNotificationConfiguration result = getXmlResponseSaxParser()
                .parse(new HttpMethodReleaseInputStream(httpResponse),
                        XmlResponsesSaxParser.BucketNotificationConfigurationHandler.class, false)
                .getBucketNotificationConfiguration();
        setHeadersAndStatus(result, httpResponse);
        return result;
    }

    protected HeaderResponse setBucketWebsiteConfigurationImpl(SetBucketWebsiteRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.WEBSITE.getOriginalStringCode(), "");

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        String xml = this.getIConvertor().transWebsiteConfiguration(request.getWebsiteConfig());

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, xml), true);
        return build(response);
    }
    
    protected WebsiteConfiguration getBucketWebsiteConfigurationImpl(BaseBucketRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.WEBSITE.getOriginalStringCode(), "");

        Response httpResponse = performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        WebsiteConfiguration ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                XmlResponsesSaxParser.BucketWebsiteConfigurationHandler.class, false).getWebsiteConfig();

        setHeadersAndStatus(ret, httpResponse);
        return ret;
    }

    protected HeaderResponse deleteBucketWebsiteConfigurationImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.WEBSITE.getOriginalStringCode(), "");
        Response response = performRestDelete(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));
        return build(response);
    }
    
    protected HeaderResponse setBucketLifecycleConfigurationImpl(SetBucketLifecycleRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.LIFECYCLE.getOriginalStringCode(), "");

        Map<String, String> metadata = new HashMap<String, String>();
        String xml = this.getIConvertor().transLifecycleConfiguration(request.getLifecycleConfig());
        metadata.put(CommonHeaders.CONTENT_MD5, ServiceUtils.computeMD5(xml));
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, xml), true);

        return build(response);
    }
    
    protected LifecycleConfiguration getBucketLifecycleConfigurationImpl(BaseBucketRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.LIFECYCLE.getOriginalStringCode(), "");

        Response response = performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(response);

        LifecycleConfiguration ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(response),
                XmlResponsesSaxParser.BucketLifecycleConfigurationHandler.class, false).getLifecycleConfig();
        setHeadersAndStatus(ret, response);
        return ret;
    }

    protected HeaderResponse deleteBucketLifecycleConfigurationImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.LIFECYCLE.getOriginalStringCode(), "");
        Response response = performRestDelete(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));
        return build(response);
    }
    
    protected HeaderResponse setBucketTaggingImpl(SetBucketTaggingRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.TAGGING.getOriginalStringCode(), "");
        Map<String, String> headers = new HashMap<String, String>();

        String requestXmlElement = this.getIConvertor().transBucketTagInfo(request.getBucketTagInfo());

        headers.put(CommonHeaders.CONTENT_MD5, ServiceUtils.computeMD5(requestXmlElement));
        headers.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);

        transRequestPaymentHeaders(request, headers, this.getIHeaders());

        Response response = this.performRestPut(request.getBucketName(), null, headers, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, requestXmlElement), true);
        return build(response);
    }

    protected BucketTagInfo getBucketTaggingImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.TAGGING.getOriginalStringCode(), "");
        Response httpResponse = this.performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        BucketTagInfo result = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                XmlResponsesSaxParser.BucketTagInfoHandler.class, false).getBucketTagInfo();
        setHeadersAndStatus(result, httpResponse);
        return result;
    }
    
    protected HeaderResponse deleteBucketTaggingImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.TAGGING.getOriginalStringCode(), "");
        Response response = performRestDelete(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));
        return build(response);
    }

    protected HeaderResponse setBucketEncryptionImpl(SetBucketEncryptionRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.ENCRYPTION.getOriginalStringCode(), "");
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);

        String encryptAsXml = request.getBucketEncryption() == null ? ""
                : this.getIConvertor().transBucketEcryption(request.getBucketEncryption());
        metadata.put(CommonHeaders.CONTENT_LENGTH, String.valueOf(encryptAsXml.length()));
        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, encryptAsXml), true);
        return build(response);
    }

    protected BucketEncryption getBucketEncryptionImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.ENCRYPTION.getOriginalStringCode(), "");

        Response httpResponse = performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        BucketEncryption ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                XmlResponsesSaxParser.BucketEncryptionHandler.class, false).getEncryption();

        setHeadersAndStatus(ret, httpResponse);
        return ret;
    }
    
    protected HeaderResponse deleteBucketEncryptionImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.ENCRYPTION.getOriginalStringCode(), "");
        Response response = performRestDelete(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));
        return build(response);
    }
    
    protected HeaderResponse setBucketReplicationConfigurationImpl(SetBucketReplicationRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.REPLICATION.getOriginalStringCode(), "");
        Map<String, String> headers = new HashMap<String, String>();

        String requestXmlElement = this.getIConvertor()
                .transReplicationConfiguration(request.getReplicationConfiguration());

        headers.put(CommonHeaders.CONTENT_MD5, ServiceUtils.computeMD5(requestXmlElement));
        headers.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        transRequestPaymentHeaders(request, headers, this.getIHeaders());

        Response response = this.performRestPut(request.getBucketName(), null, headers, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, requestXmlElement), true);
        return build(response);
    }
    
    protected ReplicationConfiguration getBucketReplicationConfigurationImpl(BaseBucketRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.REPLICATION.getOriginalStringCode(), "");
        Response httpResponse = this.performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        ReplicationConfiguration result = getXmlResponseSaxParser()
                .parse(new HttpMethodReleaseInputStream(httpResponse),
                        XmlResponsesSaxParser.BucketReplicationConfigurationHandler.class, false)
                .getReplicationConfiguration();

        setHeadersAndStatus(result, httpResponse);
        return result;
    }

    protected HeaderResponse deleteBucketReplicationConfigurationImpl(BaseBucketRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.REPLICATION.getOriginalStringCode(), "");
        Response response = performRestDelete(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));
        return build(response);
    }
    
    protected HeaderResponse setBucketCorsImpl(SetBucketCorsRequest request) throws ServiceException {
        String corsXML = request.getBucketCors() == null ? ""
                : this.getIConvertor().transBucketCors(request.getBucketCors());

        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.CORS.getOriginalStringCode(), "");

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        metadata.put(CommonHeaders.CONTENT_MD5, ServiceUtils.computeMD5(corsXML));

        metadata.put(CommonHeaders.CONTENT_LENGTH, String.valueOf(corsXML.length()));
        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, corsXML), true);
        return build(response);
    }
    
    protected BucketCors getBucketCorsImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.CORS.getOriginalStringCode(), "");
        Response httpResponse = performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);
        BucketCors ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                XmlResponsesSaxParser.BucketCorsHandler.class, false).getConfiguration();
        setHeadersAndStatus(ret, httpResponse);
        return ret;

    }
    
    protected HeaderResponse deleteBucketCorsImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.CORS.getOriginalStringCode(), "");

        Response response = performRestDelete(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));
        return build(response);
    }

    protected HeaderResponse setBucketQuotaImpl(SetBucketQuotaRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.QUOTA.getOriginalStringCode(), "");
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);

        String quotaAsXml = request.getBucketQuota() == null ? ""
                : this.getIConvertor().transBucketQuota(request.getBucketQuota());
        metadata.put(CommonHeaders.CONTENT_LENGTH, String.valueOf(quotaAsXml.length()));
        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, quotaAsXml), true);
        return build(response);
    }
    
    protected BucketQuota getBucketQuotaImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.QUOTA.getOriginalStringCode(), "");

        Response httpResponse = performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        BucketQuota ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                XmlResponsesSaxParser.BucketQuotaHandler.class, false).getQuota();
        setHeadersAndStatus(ret, httpResponse);
        return ret;
    }
    
    protected HeaderResponse setBucketAclImpl(SetBucketAclRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.ACL.getOriginalStringCode(), "");
        RequestBody entity = null;
        if (ServiceUtils.isValid(request.getCannedACL())) {
            request.setAcl(this.getIConvertor().transCannedAcl(request.getCannedACL().trim()));
        }
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        boolean isExtraAclPutRequired = !prepareRESTHeaderAcl(metadata, request.getAcl());
        if (isExtraAclPutRequired) {
            String aclAsXml = request.getAcl() == null ? ""
                    : this.getIConvertor().transAccessControlList(request.getAcl(), true);
            metadata.put(CommonHeaders.CONTENT_LENGTH, String.valueOf(aclAsXml.length()));
            entity = createRequestBody(Mimetypes.MIMETYPE_XML, aclAsXml);
        }

        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParameters, entity, true);
        return build(response);
    }
    
    protected AccessControlList getBucketAclImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.ACL.getOriginalStringCode(), "");

        Response httpResponse = performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        AccessControlList ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                XmlResponsesSaxParser.AccessControlListHandler.class, false).getAccessControlList();
        setHeadersAndStatus(ret, httpResponse);
        return ret;
    }
    
    protected HeaderResponse setBucketLoggingConfigurationImpl(SetBucketLoggingRequest request)
            throws ServiceException {
        if (request.getLoggingConfiguration().isLoggingEnabled() && request.isUpdateTargetACLifRequired()
                && this.getProviderCredentials().getAuthType() != AuthTypeEnum.OBS) {
            boolean isSetLoggingGroupWrite = false;
            boolean isSetLoggingGroupReadACP = false;
            String groupIdentifier = Constants.LOG_DELIVERY_URI;

            BaseBucketRequest getBucketAclRequest = new BaseBucketRequest(
                    request.getLoggingConfiguration().getTargetBucketName());
            getBucketAclRequest.setRequesterPays(request.isRequesterPays());
            AccessControlList logBucketACL = getBucketAclImpl(getBucketAclRequest);

            for (GrantAndPermission gap : logBucketACL.getGrantAndPermissions()) {
                if (gap.getGrantee() instanceof GroupGrantee) {
                    GroupGrantee grantee = (GroupGrantee) gap.getGrantee();
                    if (groupIdentifier.equals(this.getIConvertor().transGroupGrantee(grantee.getGroupGranteeType()))) {
                        if (Permission.PERMISSION_WRITE.equals(gap.getPermission())) {
                            isSetLoggingGroupWrite = true;
                        } else if (Permission.PERMISSION_READ_ACP.equals(gap.getPermission())) {
                            isSetLoggingGroupReadACP = true;
                        }
                    }
                }
            }

            if (!isSetLoggingGroupWrite || !isSetLoggingGroupReadACP) {
                if (log.isWarnEnabled()) {
                    log.warn("Target logging bucket '" + request.getLoggingConfiguration().getTargetBucketName()
                            + "' does not have the necessary ACL settings, updating ACL now");
                }
                if (logBucketACL.getOwner() != null) {
                    logBucketACL.getOwner().setDisplayName(null);
                }
                logBucketACL.grantPermission(GroupGrantee.LOG_DELIVERY, Permission.PERMISSION_WRITE);
                logBucketACL.grantPermission(GroupGrantee.LOG_DELIVERY, Permission.PERMISSION_READ_ACP);

                SetBucketAclRequest aclReqeust = new SetBucketAclRequest(request.getBucketName(), logBucketACL);
                aclReqeust.setRequesterPays(request.isRequesterPays());
                setBucketAclImpl(aclReqeust);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Target logging bucket '" + request.getLoggingConfiguration().getTargetBucketName()
                            + "' has the necessary ACL settings");
                }
            }
        }

        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.LOGGING.getOriginalStringCode(), "");

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        String statusAsXml = request.getLoggingConfiguration() == null ? ""
                : this.getIConvertor().transBucketLoggingConfiguration(request.getLoggingConfiguration());

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, statusAsXml), true);
        return build(response);
    }
    
    protected BucketLoggingConfiguration getBucketLoggingConfigurationImpl(BaseBucketRequest request)
            throws ServiceException {

        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.LOGGING.getOriginalStringCode(), "");

        Response httpResponse = performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        BucketLoggingConfiguration ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                XmlResponsesSaxParser.BucketLoggingHandler.class, false).getBucketLoggingStatus();

        setHeadersAndStatus(ret, httpResponse);
        return ret;
    }

    protected HeaderResponse setBucketDirectColdAccessImpl(SetBucketDirectColdAccessRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.DIRECTCOLDACCESS.getOriginalStringCode(), "");
        Map<String, String> headers = new HashMap<String, String>();

        String requestXmlElement = this.getIConvertor().transBucketDirectColdAccess(request.getAccess());

        headers.put(CommonHeaders.CONTENT_MD5, ServiceUtils.computeMD5(requestXmlElement));
        headers.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        transRequestPaymentHeaders(request, headers, this.getIHeaders());

        Response response = this.performRestPut(request.getBucketName(), null, headers, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, requestXmlElement), true);
        return build(response);
    }
    
    protected BucketDirectColdAccess getBucketDirectColdAccessImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.DIRECTCOLDACCESS.getOriginalStringCode(), "");
        Response httpResponse = this.performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        BucketDirectColdAccess result = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                XmlResponsesSaxParser.BucketDirectColdAccessHandler.class, false).getBucketDirectColdAccess();
        setHeadersAndStatus(result, httpResponse);
        return result;
    }
    
    protected HeaderResponse deleteBucketDirectColdAccessImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.DIRECTCOLDACCESS.getOriginalStringCode(), "");
        Response response = performRestDelete(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        return build(response);
    }
    
    protected BucketCustomDomainInfo getBucketCustomDomainImpl(GetBucketCustomDomainRequest request) 
            throws ServiceException {
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put(SpecialParamEnum.CUSTOMDOMAIN.getOriginalStringCode(), "");

        Response response = performRestGet(request.getBucketName(), null, requestParams,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(response);

        BucketCustomDomainInfo ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(response),
                XmlResponsesSaxParser.BucketCustomDomainHandler.class, true).getBucketTagInfo();
        setHeadersAndStatus(ret, response);
        return ret;
    }
    
    protected HeaderResponse setBucketCustomDomainImpl(SetBucketCustomDomainRequest request) throws ServiceException {
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put(SpecialParamEnum.CUSTOMDOMAIN.getOriginalStringCode(), request.getDomainName());
        Map<String, String> metadata = transRequestPaymentHeaders(request, null, this.getIHeaders());

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParams,
                null, true);
        return this.build(response);
    }
    
    protected HeaderResponse deleteBucketCustomDomainImpl(DeleteBucketCustomDomainRequest request) 
            throws ServiceException {
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put(SpecialParamEnum.CUSTOMDOMAIN.getOriginalStringCode(), request.getDomainName());

        Response response = performRestDelete(request.getBucketName(), null, requestParams,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));
        return this.build(response);
    }
}
