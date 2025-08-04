package com.obs.test;

import com.obs.services.ObsClient;
import com.obs.services.model.CompleteMultipartUploadRequest;
import com.obs.services.model.CompleteMultipartUploadResult;
import com.obs.services.model.InitiateMultipartUploadRequest;
import com.obs.services.model.InitiateMultipartUploadResult;
import com.obs.services.model.ObjectMetadata;
import com.obs.services.model.PartEtag;
import com.obs.services.model.UploadPartRequest;
import com.obs.services.model.UploadPartResult;
import com.obs.test.tools.PrepareTestBucket;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserHeaderTest {

    @Rule
    public PrepareTestBucket prepareTestBucket = new PrepareTestBucket();

    @Rule
    public TestName testName = new TestName();

    @Test
    public void test_add_user_headers() {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        assert obsClient != null;
        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);
        String objectKey = "test_add_user_headers";
        String metaDataPrefix = "x-obs-meta-";
        String metaDataKey = "test";
        String headerKey = metaDataPrefix+metaDataKey;
        String headerVal = "test-value-initiateMultipartUpload";


        // 测试覆盖所有 performRequest 和 trans 函数
        InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest();
        initiateMultipartUploadRequest.setBucketName(bucketName);
        initiateMultipartUploadRequest.setObjectKey(objectKey);
        initiateMultipartUploadRequest.addUserHeaders(headerKey, headerVal);
        InitiateMultipartUploadResult initResult = obsClient.initiateMultipartUpload(initiateMultipartUploadRequest);
        assertEquals(200, initResult.getStatusCode());

        UploadPartRequest uploadPartRequest = new UploadPartRequest();
        uploadPartRequest.setUploadId(initResult.getUploadId());
        uploadPartRequest.setPartNumber(1);
        uploadPartRequest.setBucketName(bucketName);
        uploadPartRequest.setObjectKey(objectKey);
        uploadPartRequest.setInput(new ByteArrayInputStream("testObject".getBytes(StandardCharsets.UTF_8)));
        UploadPartResult uploadResult = obsClient.uploadPart(uploadPartRequest);
        assertEquals(200, uploadResult.getStatusCode());

        CompleteMultipartUploadRequest completeRequest = new CompleteMultipartUploadRequest();
        completeRequest.setObjectKey(objectKey);
        completeRequest.setBucketName(bucketName);
        completeRequest.setUploadId(initResult.getUploadId());
        completeRequest.addUserHeaders(headerKey, "test-value-completeMultipartUpload");
        PartEtag partEtag = new PartEtag();
        partEtag.setPartNumber(uploadResult.getPartNumber());
        partEtag.setEtag(uploadResult.getEtag());
        completeRequest.getPartEtag().add(partEtag);
        CompleteMultipartUploadResult completeResult = obsClient.completeMultipartUpload(completeRequest);
        assertEquals(200, completeResult.getStatusCode());

        ObjectMetadata metadata = obsClient.getObjectMetadata(bucketName,objectKey);
        String testValue = (String)metadata.getUserMetadata(metaDataKey);
        assertEquals(headerVal,testValue);
    }

    @Test
    public void test_add_user_headers_need_auth_01() {
        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        String objectKey = "test_add_user_headers";
        String headerKey1 = TestTools.getAuthType().equals("v2") ? "x-amz-test-auth-header1"
                : "x-obs-test-auth-header1";
        String headerKey2 = TestTools.getAuthType().equals("v2") ? "x-amz-test-auth-header2"
                : "x-obs-test-auth-header2";

        // 初始化 log
        StringWriter writer = new StringWriter();

        TestTools.initLog(writer);

        // 测试覆盖所有 performRequest 和 trans 函数
        InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest();
        initiateMultipartUploadRequest.setBucketName(bucketName);
        initiateMultipartUploadRequest.setObjectKey(objectKey);
        initiateMultipartUploadRequest.addUserHeaders(headerKey1, "test-value-initiateMultipartUpload1");
        initiateMultipartUploadRequest.addUserHeaders(headerKey2, "test-value-initiateMultipartUpload2");
        InitiateMultipartUploadResult initResult = obsClient.initiateMultipartUpload(initiateMultipartUploadRequest);
        // 签名计算字段
        System.out.println("test_add_user_headers_need_auth_01 102:"+writer);
        assertTrue(writer.toString().contains("|" + headerKey1 + ":test-value-initiateMultipartUpload1|"));
        // 请求头域
        assertTrue(writer.toString().contains("|" + headerKey2 + ":test-value-initiateMultipartUpload2|"));
        writer.flush();
        assertEquals(200, initResult.getStatusCode());

        UploadPartRequest uploadPartRequest = new UploadPartRequest();
        uploadPartRequest.setUploadId(initResult.getUploadId());
        uploadPartRequest.setPartNumber(1);
        uploadPartRequest.setBucketName(bucketName);
        uploadPartRequest.setObjectKey(objectKey);
        uploadPartRequest.setInput(new ByteArrayInputStream("testObject".getBytes(StandardCharsets.UTF_8)));
        uploadPartRequest.addUserHeaders(headerKey1, "test-value-uploadPart1");
        uploadPartRequest.addUserHeaders(headerKey2, "test-value-uploadPart2");
        UploadPartResult uploadResult = obsClient.uploadPart(uploadPartRequest);
        // 签名计算字段
        assertTrue(writer.toString().contains("|" + headerKey1 + ":test-value-uploadPart1|"));
        // 请求头域
        assertTrue(writer.toString().contains("|" + headerKey2 + ":test-value-uploadPart2|"));
        writer.flush();
        assertEquals(200, uploadResult.getStatusCode());

        CompleteMultipartUploadRequest completeRequest = new CompleteMultipartUploadRequest();
        completeRequest.setObjectKey(objectKey);
        completeRequest.setBucketName(bucketName);
        completeRequest.setUploadId(initResult.getUploadId());
        completeRequest.addUserHeaders(headerKey1, "test-value-completeMultipartUpload1");
        completeRequest.addUserHeaders(headerKey2, "test-value-completeMultipartUpload2");
        PartEtag partEtag = new PartEtag();
        partEtag.setPartNumber(uploadResult.getPartNumber());
        partEtag.setEtag(uploadResult.getEtag());
        completeRequest.getPartEtag().add(partEtag);
        CompleteMultipartUploadResult completeResult = obsClient.completeMultipartUpload(completeRequest);
        // 签名计算字段
        assertTrue(writer.toString().contains("|" + headerKey1 + ":test-value-completeMultipartUpload1|"));
        // 请求头域
        assertTrue(writer.toString().contains("|" + headerKey2 + ":test-value-completeMultipartUpload2|"));
        writer.flush();
        assertEquals(200, completeResult.getStatusCode());
    }

    @Test
    public void test_add_empty_user_headers_01() {
        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        String objectKey = "test_add_empty_user_headers";
        String headerKey = "x-obs-test-add-empty-user-headers";
        String headerVal = "";

        // 初始化 log
        StringWriter writer = new StringWriter();

        TestTools.initLog(writer);

        // 测试覆盖所有 performRequest 和 trans 函数
        InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest();
        initiateMultipartUploadRequest.setBucketName(bucketName);
        initiateMultipartUploadRequest.setObjectKey(objectKey);
        initiateMultipartUploadRequest.addUserHeaders(headerKey, headerVal);
        InitiateMultipartUploadResult initResult = obsClient.initiateMultipartUpload(initiateMultipartUploadRequest);
        System.out.println("test_add_empty_user_headers_01 163:"+writer);
        assertTrue(writer.toString().contains("|" + headerKey + ":"+headerVal+"|"));
        writer.flush();
        assertEquals(200, initResult.getStatusCode());

        UploadPartRequest uploadPartRequest = new UploadPartRequest();
        uploadPartRequest.setUploadId(initResult.getUploadId());
        uploadPartRequest.setPartNumber(1);
        uploadPartRequest.setBucketName(bucketName);
        uploadPartRequest.setObjectKey(objectKey);
        uploadPartRequest.setInput(new ByteArrayInputStream("testObject".getBytes(StandardCharsets.UTF_8)));
        uploadPartRequest.addUserHeaders("test-user-headers", "");
        UploadPartResult uploadResult = obsClient.uploadPart(uploadPartRequest);
        assertTrue(writer.toString().contains("|" + headerKey + ":"+headerVal+"|"));
        writer.flush();
        assertEquals(200, uploadResult.getStatusCode());

        CompleteMultipartUploadRequest completeRequest = new CompleteMultipartUploadRequest();
        completeRequest.setObjectKey(objectKey);
        completeRequest.setBucketName(bucketName);
        completeRequest.setUploadId(initResult.getUploadId());
        completeRequest.addUserHeaders("test-user-headers", "");
        PartEtag partEtag = new PartEtag();
        partEtag.setPartNumber(uploadResult.getPartNumber());
        partEtag.setEtag(uploadResult.getEtag());
        completeRequest.getPartEtag().add(partEtag);
        CompleteMultipartUploadResult completeResult = obsClient.completeMultipartUpload(completeRequest);
        assertTrue(writer.toString().contains("|" + headerKey + ":"+headerVal+"|"));
        writer.flush();
        assertEquals(200, completeResult.getStatusCode());
    }

}
