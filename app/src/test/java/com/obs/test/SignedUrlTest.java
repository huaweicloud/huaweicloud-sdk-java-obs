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

package com.obs.test;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.obs.services.ObsClient;
import com.obs.services.internal.handler.XmlResponsesSaxParser;
import com.obs.services.internal.handler.XmlResponsesSaxParser.InitiateMultipartUploadHandler;
import com.obs.services.internal.handler.XmlResponsesSaxParser.ListPartsHandler;
import com.obs.services.model.BucketTypeEnum;
import com.obs.services.model.HttpMethodEnum;
import com.obs.services.model.InitiateMultipartUploadResult;
import com.obs.services.model.ListPartsResult;
import com.obs.services.model.Multipart;
import com.obs.services.model.SpecialParamEnum;
import com.obs.services.model.StorageClassEnum;
import com.obs.services.model.TemporarySignatureRequest;
import com.obs.services.model.TemporarySignatureResponse;
import com.obs.test.tools.BucketTools;
import com.obs.test.tools.PropertiesTools;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SignedUrlTest {
    private static final Logger logger = LogManager.getLogger(SignedUrlTest.class);

    private static String endpint_ip = null;
    private static String endpint_dns = null;
    private static String ak = null;
    private static String sk = null;

    private static String bucketName;

    private static String objectKey;

    public static X509TrustManager trustAllManager = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // 客户端证书验证
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
        {
            // 服务端证书验证
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };

    static ObsClient obsClient = null;

    @BeforeClass
    public static void init() throws FileNotFoundException, IllegalArgumentException, IOException {
        endpint_ip = PropertiesTools.getInstance(TestTools.getPropertiesFile()).getProperties("environment.1.endpoint");
        endpint_dns = PropertiesTools.getInstance(TestTools.getPropertiesFile()).getProperties("environment.1.endpoint.dns");
        ak = PropertiesTools.getInstance(TestTools.getPropertiesFile()).getProperties("environment.1.ak");;
        sk = PropertiesTools.getInstance(TestTools.getPropertiesFile()).getProperties("environment.1.sk");;
        
        bucketName = SignedUrlTest.class.getName().replaceAll("\\.", "-").toLowerCase() + "-obs";

        objectKey = "mulitpart_test_001";

        obsClient = TestTools.getPipelineEnvironment();

        // 强制删除一个桶
        // BucketTools.deleteBucket(obsClient, bucketName, true);

        // 创建桶
        BucketTools.createBucket(obsClient, bucketName, BucketTypeEnum.OBJECT);
    }

    @Test
    public void test_get_url() throws IllegalArgumentException
    {
        ObsClient obsClient = TestTools.getPipelineEnvironment();

        // URL有效期，3600秒
        long expireSeconds = 3600L;

        TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.GET, expireSeconds);
        request.setBucketName("com-obs-test-requestpaymenttest-pfs");
        request.setObjectKey("test_rename_object_before");

        TemporarySignatureResponse response = obsClient.createTemporarySignature(request);

        System.out.println("Getting object using temporary signature url:");
        System.out.println("\t" + response.getSignedUrl());
    }
    
    @Test
    public void test_create_bucket_url()
            throws IllegalArgumentException, IOException, NoSuchAlgorithmException, KeyManagementException
    {
        ObsClient obsClient = TestTools.getPipelineEnvironment();

        // URL有效期，3600秒
        long expireSeconds = 3600L;
        String bucketName = "test-create-bucket-001";
        TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.PUT, expireSeconds);
        request.setBucketName(bucketName);

        TemporarySignatureResponse response = obsClient.createTemporarySignature(request);

        System.out.println("Creating bucket using temporary signature url:");
        System.out.println("\t" + response.getSignedUrl());
        
        Request.Builder builder = new Request.Builder();
        for (Map.Entry<String, String> entry : response.getActualSignedRequestHeaders().entrySet()) {
               builder.header(entry.getKey(), entry.getValue());
        }
        // 使用PUT请求创建桶
        String location = "cn-north-7";
        String createXml = "<CreateBucketConfiguration><Location>" + location + "</Location></CreateBucketConfiguration>";
        System.out.println("Create Body: " + createXml);
        
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        
        Request httpRequest = builder.url(response.getSignedUrl()).put(RequestBody.create(null, createXml.getBytes())).build();

        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, new TrustManager[]{trustAllManager}, new SecureRandom());
        OkHttpClient httpClient = clientBuilder.followRedirects(false).retryOnConnectionFailure(false)
                      .cache(null).sslSocketFactory(sslContext.getSocketFactory(), trustAllManager).build();

        Call c = httpClient.newCall(httpRequest);
        Response res = c.execute();
        System.out.println("\tStatus:" + res.code());
        if (res.body() != null) {
               System.out.println("\tContent:" + res.body().string() + "\n");
        }
        res.close();
        obsClient.deleteBucket(bucketName);
    }

    @Test
    public void test_temporary_signature_for_init_mulitpartupload() throws IOException {
        init_mulitpartupload(bucketName, objectKey);
    }

    @Test
    public void test_temporary_signature_for_upload_part() throws IOException {
        InitiateMultipartUploadResult initResult = init_mulitpartupload(bucketName, objectKey);

        uploadPart(bucketName, objectKey, initResult.getUploadId(), 1);
        uploadPart(bucketName, objectKey, initResult.getUploadId(), 2);
    }

    @Test
    public void test_temporary_signature_for_list_parts() throws IOException {
        InitiateMultipartUploadResult initResult = init_mulitpartupload(bucketName, objectKey);

        uploadPart(bucketName, objectKey, initResult.getUploadId(), 1);
        uploadPart(bucketName, objectKey, initResult.getUploadId(), 2);
        
        ListPartsResult result = listParts(bucketName, objectKey, initResult.getUploadId());
        
        assertEquals(2, result.getMultipartList().size()); 
    }

    @Test
    public void test_temporary_signature_for_complete_multipartupload()
            throws IOException, NoSuchAlgorithmException, KeyManagementException
    {
        InitiateMultipartUploadResult initResult = init_mulitpartupload(bucketName, objectKey);

        uploadPart(bucketName, objectKey, initResult.getUploadId(), 1);
        uploadPart(bucketName, objectKey, initResult.getUploadId(), 2);
        
        ListPartsResult result = listParts(bucketName, objectKey, initResult.getUploadId());
        
        assertEquals(2, result.getMultipartList().size()); 
        
        completeMultipartupload(bucketName, objectKey, initResult.getUploadId(), result);
    }

    private InitiateMultipartUploadResult init_mulitpartupload(String bucketName, String objectKey) throws IOException {
        ObsClient client = new ObsClient(ak, sk, endpint_ip);
        long expireSeconds = 3600L;

        TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.POST, expireSeconds);
        request.setBucketName(bucketName);
        request.setObjectKey(objectKey);
        request.setSpecialParam(SpecialParamEnum.UPLOADS);
        TemporarySignatureResponse response = client.createTemporarySignature(request);

        logger.info("\t" + response.getSignedUrl());

        Request.Builder builder = new Request.Builder();
        for (Map.Entry<String, String> entry : response.getActualSignedRequestHeaders().entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }
        Response res = null;
        try {
            Request httpRequest = builder.url(response.getSignedUrl()).post(RequestBody.create(null, "")).build();
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, new TrustManager[]{trustAllManager}, new SecureRandom());
            OkHttpClient httpClient = new OkHttpClient.Builder().followRedirects(false).retryOnConnectionFailure(false)
                    .cache(null).sslSocketFactory(sslContext.getSocketFactory(), trustAllManager).build();

            Call c = httpClient.newCall(httpRequest);
            res = c.execute();
            int responseCode = res.code();
            logger.info("\tStatus:" + res.code());
            assertEquals(responseCode, 200);

            InitiateMultipartUploadResult multipartUpload = new XmlResponsesSaxParser()
                    .parse(res.body().byteStream(), InitiateMultipartUploadHandler.class, true)
                    .getInitiateMultipartUploadResult();
            logger.info("\tContent:" + multipartUpload + "\n");
            return multipartUpload;
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
        catch (KeyManagementException e)
        {
            throw new RuntimeException(e);
        }
        finally {
            if (null != res) {
                res.close();
            }
        }
    }

    private void uploadPart(String bucketName, String objectKey, String uploadId, int partNumber) throws IOException {
        ObsClient client = new ObsClient(ak, sk, endpint_ip);
        long expireSeconds = 3600L;

        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("partNumber", partNumber);
        queryParams.put("uploadId", uploadId);

        TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.PUT, expireSeconds);
        request.setBucketName(bucketName);
        request.setObjectKey(objectKey);

        request.setQueryParams(queryParams);

        TemporarySignatureResponse response = client.createTemporarySignature(request);

        logger.info("\t" + response.getSignedUrl());
        Request.Builder builder = new Request.Builder();
        for (Map.Entry<String, String> entry : response.getActualSignedRequestHeaders().entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }

        Response res = null;
        try {
            // 使用PUT请求上传段
            Request httpRequest = builder.url(response.getSignedUrl())
                    .put(RequestBody.create(null, new byte[6 * 1024 * 1024])).build();
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, new TrustManager[]{trustAllManager}, new SecureRandom());
            OkHttpClient httpClient = new OkHttpClient.Builder().followRedirects(false).retryOnConnectionFailure(false)
                    .cache(null).sslSocketFactory(sslContext.getSocketFactory(), trustAllManager).build();

            Call c = httpClient.newCall(httpRequest);
            res = c.execute();
            System.out.println("\tStatus:" + res.code());
            if (res.body() != null) {
                System.out.println("\tContent:" + res.body().string() + "\n");
            }
            res.close();
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
        catch (KeyManagementException e)
        {
            throw new RuntimeException(e);
        }
        finally {
            if (null != res) {
                res.close();
            }
        }
    }

    private ListPartsResult listParts(String bucketName, String objectKey, String uploadId) throws IOException {
        // String endPoint = "http://your-endpoint";
        // String ak = "*** Provide your Access Key ***";
        // String sk = "*** Provide your Secret Key ***";

        // 创建ObsClient实例
        ObsClient obsClient = new ObsClient(ak, sk, endpint_ip);
        // URL有效期，3600秒
        long expireSeconds = 3600L;

        TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.GET, expireSeconds);
        request.setBucketName(bucketName);
        request.setObjectKey(objectKey);

        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("uploadId", uploadId);
        request.setQueryParams(queryParams);

        TemporarySignatureResponse response = obsClient.createTemporarySignature(request);

        logger.info("list parts using temporary signature url:");
        logger.info("\t" + response.getSignedUrl());

        Response res = null;
        try {
            Request.Builder builder = new Request.Builder();
            for (Map.Entry<String, String> entry : response.getActualSignedRequestHeaders().entrySet()) {
                builder.header(entry.getKey(), entry.getValue());
            }
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, new TrustManager[]{trustAllManager}, new SecureRandom());

            Request httpRequest = builder.url(response.getSignedUrl()).get().build();
            OkHttpClient httpClient = new OkHttpClient.Builder().followRedirects(false).retryOnConnectionFailure(false)
                    .cache(null).sslSocketFactory(sslContext.getSocketFactory(), trustAllManager).build();

            Call c = httpClient.newCall(httpRequest);
            res = c.execute();
            int responseCode = res.code();
            logger.info("\tStatus:" + res.code());
            assertEquals(responseCode, 200);
            
            ListPartsHandler handler = new XmlResponsesSaxParser().parse(res.body().byteStream(),
                    ListPartsHandler.class, true);

            ListPartsResult result = new ListPartsResult(handler.getBucketName(), handler.getObjectKey(),
                    handler.getUploadId(), handler.getInitiator(), handler.getOwner(),
                    StorageClassEnum.getValueFromCode(handler.getStorageClass()), handler.getMultiPartList(),
                    handler.getMaxParts(), handler.isTruncated(),
                    handler.getPartNumberMarker(),
                    handler.getNextPartNumberMarker());

            logger.info("\tContent:" + result + "\n");
            return result;
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
        catch (KeyManagementException e)
        {
            throw new RuntimeException(e);
        }
        finally {
            if(null != res) {
                res.close();
            }
        }
    }
    
    private void completeMultipartupload(String bucketName, String objectKey, String uploadId, ListPartsResult result)
            throws IOException, NoSuchAlgorithmException, KeyManagementException
    {
        // 创建ObsClient实例
        ObsClient obsClient = new ObsClient(ak, sk, endpint_ip);
        // URL有效期，3600秒
        long expireSeconds = 3600L;

        TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.POST, expireSeconds);
        request.setBucketName(bucketName);
        request.setObjectKey(objectKey);

        Map<String, String> headers = new HashMap<String, String>();
        String contentType = "application/xml";
        headers.put("Content-Type", contentType);
        request.setHeaders(headers);

        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("uploadId", uploadId);
        request.setQueryParams(queryParams);

        TemporarySignatureResponse response = obsClient.createTemporarySignature(request);

        System.out.println("complete multipart upload using temporary signature url:");
        System.out.println("\t" + response.getSignedUrl());

        Request.Builder builder = new Request.Builder();
        for (Map.Entry<String, String> entry : response.getActualSignedRequestHeaders().entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }

        StringBuilder sb = new StringBuilder("<CompleteMultipartUpload>");
        for(Multipart part : result.getMultipartList()) {
            sb.append("<Part>");
            sb.append("<PartNumber>").append(part.getPartNumber()).append("</PartNumber>");
            sb.append("<ETag>").append(part.getEtag()).append("</ETag>");
            sb.append("</Part>");
        }
        sb.append("</CompleteMultipartUpload>");

        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, new TrustManager[]{trustAllManager}, new SecureRandom());

        Request httpRequest = builder.url(response.getSignedUrl())
                .post(RequestBody.create(MediaType.parse(contentType), sb.toString().getBytes("UTF-8"))).build();
        OkHttpClient httpClient = new OkHttpClient.Builder().followRedirects(false).retryOnConnectionFailure(false)
                .cache(null).sslSocketFactory(sslContext.getSocketFactory(), trustAllManager).build();

        Call c = httpClient.newCall(httpRequest);
        Response res = c.execute();
        System.out.println("\tStatus:" + res.code());
        if (res.body() != null) {
            System.out.println("\tContent:" + res.body().string() + "\n");
        }
        res.close();
    }
}
