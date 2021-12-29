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

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.internal.Constants;
import com.obs.services.internal.Constants.CommonHeaders;
import com.obs.services.internal.ServiceException;
import com.obs.services.internal.handler.XmlResponsesSaxParser;
import com.obs.services.internal.io.HttpMethodReleaseInputStream;
import com.obs.services.internal.trans.NewTransResult;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.AccessControlList;
import com.obs.services.model.SpecialParamEnum;
import com.obs.services.model.StorageClassEnum;
import com.obs.services.model.fs.ListContentSummaryRequest;
import com.obs.services.model.fs.ListContentSummaryResult;
import com.obs.services.model.fs.ObsFSFile;
import com.obs.services.model.fs.RenameRequest;
import com.obs.services.model.fs.RenameResult;
import com.obs.services.model.fs.TruncateFileRequest;
import com.obs.services.model.fs.TruncateFileResult;
import com.obs.services.model.fs.WriteFileRequest;
import okhttp3.Response;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

public abstract class ObsFileService extends ObsObjectService {
    private static final ILogger log = LoggerBuilder.getLogger(ObsFileService.class);

    protected TruncateFileResult truncateFileImpl(TruncateFileRequest request) throws ServiceException {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(SpecialParamEnum.TRUNCATE.getOriginalStringCode(), "");
        requestParams.put(Constants.ObsRequestParams.LENGTH, String.valueOf(request.getNewLength()));
        Map<String, String> headers = transRequestPaymentHeaders(request, null,
                this.getIHeaders(request.getBucketName()));
        NewTransResult transResult = transObjectRequest(request);
        transResult.setParams(requestParams);
        transResult.setHeaders(headers);
        Response response = performRequest(transResult);
        TruncateFileResult result = new TruncateFileResult();
        setHeadersAndStatus(result, response);
        return result;
    }

    protected RenameResult renameFileImpl(RenameRequest request) throws ServiceException {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(SpecialParamEnum.RENAME.getOriginalStringCode(), "");
        requestParams.put(Constants.ObsRequestParams.NAME, request.getNewObjectKey());

        Map<String, String> headers = transRequestPaymentHeaders(request, null,
                this.getIHeaders(request.getBucketName()));

        NewTransResult transResult = transObjectRequest(request);
        transResult.setParams(requestParams);
        transResult.setHeaders(headers);
        Response response = performRequest(transResult);

        RenameResult result = new RenameResult();
        setHeadersAndStatus(result, response);
        return result;
    }

    protected ObsFSFile writeFileImpl(WriteFileRequest request) throws ServiceException {
        TransResult result = null;
        Response response;
        boolean isExtraAclPutRequired;
        AccessControlList acl = request.getAcl();
        try {
            result = this.transWriteFileRequest(request);

            isExtraAclPutRequired = !prepareRESTHeaderAcl(request.getBucketName(), result.getHeaders(), acl);
            NewTransResult newTransResult = transObjectRequestWithResult(result, request);
            response = performRequest(newTransResult);
        } finally {
            if (result != null && result.getBody() != null && request.isAutoClose()) {
                if (result.getBody() instanceof Closeable) {
                    ServiceUtils.closeStream((Closeable) result.getBody());
                }
            }
        }

        ObsFSFile ret = new ObsFSFile(request.getBucketName(), request.getObjectKey(),
                response.header(CommonHeaders.ETAG),
                response.header(this.getIHeaders(request.getBucketName()).versionIdHeader()),
                StorageClassEnum.getValueFromCode(response.header(this.getIHeaders(request.getBucketName())
                        .storageClassHeader())),
                this.getObjectUrl(request.getBucketName(), request.getObjectKey()));

        setHeadersAndStatus(ret, response);
        if (isExtraAclPutRequired && acl != null) {
            try {
                putAclImpl(request.getBucketName(), request.getObjectKey(), acl, null, request.isRequesterPays());
            } catch (Exception e) {
                if (log.isWarnEnabled()) {
                    log.warn("Try to set object acl error", e);
                }
            }
        }
        return ret;
    }

    protected ListContentSummaryResult listContentSummaryImpl(ListContentSummaryRequest listContentSummaryRequest)
            throws ServiceException {

        TransResult result = this.transListContentSummaryRequest(listContentSummaryRequest);

        Response httpResponse = performRestGet(listContentSummaryRequest.getBucketName(), null,
                result.getParams(), null, listContentSummaryRequest.getUserHeaders());

        this.verifyResponseContentType(httpResponse);

        XmlResponsesSaxParser.ListContentSummaryHandler listContentSummaryHandler = getXmlResponseSaxParser().parse(
                new HttpMethodReleaseInputStream(httpResponse), XmlResponsesSaxParser.ListContentSummaryHandler.class,
                true);

        ListContentSummaryResult contentSummaryResult = new ListContentSummaryResult.Builder()
                .folderContentSummaries(listContentSummaryHandler.getFolderContentSummaries())
                .bucketName(listContentSummaryHandler.getBucketName() == null
                        ? listContentSummaryRequest.getBucketName() : listContentSummaryHandler.getBucketName())
                .truncated(listContentSummaryHandler.isListingTruncated())
                .prefix(listContentSummaryHandler.getRequestPrefix() == null ? listContentSummaryRequest.getPrefix()
                        : listContentSummaryHandler.getRequestPrefix())
                .marker(listContentSummaryHandler.getRequestMarker() == null ? listContentSummaryRequest.getMarker()
                        : listContentSummaryHandler.getRequestMarker())
                .maxKeys(listContentSummaryHandler.getRequestMaxKeys())
                .delimiter(listContentSummaryHandler.getRequestDelimiter() == null
                        ? listContentSummaryRequest.getDelimiter() : listContentSummaryHandler.getRequestDelimiter())
                .nextMarker(listContentSummaryHandler.getMarkerForNextListing())
                .location(httpResponse
                        .header(this.getIHeaders(listContentSummaryRequest.getBucketName()).bucketRegionHeader()))
                .builder();

        setHeadersAndStatus(contentSummaryResult, httpResponse);
        return contentSummaryResult;
    }
}
