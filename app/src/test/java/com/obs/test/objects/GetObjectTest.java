package com.obs.test.objects;

import com.obs.services.ObsClient;
import com.obs.services.model.GetObjectMetadataRequest;
import com.obs.services.model.GetObjectRequest;
import com.obs.services.model.ObjectMetadata;
import com.obs.services.model.ObsObject;
import com.obs.services.model.PutObjectResult;
import com.obs.test.tools.PrepareTestBucket;
import com.obs.test.TestTools;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GetObjectTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public PrepareTestBucket prepareTestBucket = new PrepareTestBucket();

    @Rule
    public TestName testName = new TestName();


    @Test
    public void test_getObject_with_url_encode_001() {
        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        String objectKey = "test_getObject_with_url_encode_001";
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("test-url-encode", "%^&");
        metadata.setContentDisposition("%^&");
        PutObjectResult putResult = obsClient.putObject(bucketName, objectKey,
                new ByteArrayInputStream("testObject".getBytes(StandardCharsets.UTF_8)), metadata);

        assertEquals(200, putResult.getStatusCode());

        GetObjectRequest request = new GetObjectRequest();
        request.setObjectKey(objectKey);
        request.setBucketName(bucketName);
        try {
            obsClient.getObject(request);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("URLDecoder: Illegal hex characters in escape (%) pattern"));
        }

        GetObjectMetadataRequest getObjectMetadataRequest = new GetObjectMetadataRequest(bucketName,objectKey);
        getObjectMetadataRequest.setIsEncodeHeaders(false);
        ObjectMetadata metadata1 = obsClient.getObjectMetadata(getObjectMetadataRequest);
        assertEquals("%^&", metadata1.getUserMetadata("test-url-encode"));
        assertEquals("%^&", metadata1.getResponseHeaders().get("content-disposition"));
    }

    @Test
    public void test_getObject_with_url_encode_002() {
        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        String objectKey = "test_getObject_with_url_encode_002";
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("test-url-encode", "%E6%B5%8B%E8%AF%95%E4%B8%AD%E6%96%87");
        metadata.setContentDisposition("%E6%B5%8B%E8%AF%95%E4%B8%AD%E6%96%87");
        PutObjectResult putResult = obsClient.putObject(bucketName, objectKey,
                new ByteArrayInputStream("testObject".getBytes(StandardCharsets.UTF_8)),metadata);

        assertEquals(200, putResult.getStatusCode());


        GetObjectMetadataRequest getObjectMetadataRequest = new GetObjectMetadataRequest(bucketName,objectKey);
        ObjectMetadata metadata1 = obsClient.getObjectMetadata(getObjectMetadataRequest);
        assertEquals("测试中文", metadata1.getUserMetadata("test-url-encode"));
        assertEquals("测试中文", metadata1.getResponseHeaders().get("content-disposition"));
    }


    @Test
    public void test_getObject_with_progress() throws IOException
    {
        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        String objectKey = "test_getObject_with_progress";
        ObjectMetadata metadata = new ObjectMetadata();
        PutObjectResult putResult = obsClient.putObject(bucketName, objectKey,
                new ByteArrayInputStream("testObject".getBytes(StandardCharsets.UTF_8)),metadata);

        assertEquals(200, putResult.getStatusCode());

        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, objectKey);
        getObjectRequest.setProgressListener(status -> {
            // 获取上传平均速率
            System.out.println("AverageSpeed:" + status.getAverageSpeed());
            // 获取上传进度百分比
            System.out.println("TransferPercentage:" + status.getTransferPercentage());
        });
        ObsObject obsObject = obsClient.getObject(getObjectRequest);
        assertEquals(200, obsObject.getMetadata().getStatusCode());
        obsObject.getObjectContent().close();

    }


    @Test
    public void test_getObject_with_read_bufferSize() throws IOException
    {
        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);
        ObsClient obsClient = TestTools.getPipelineEnvironmentWithReadBufferSize(10240);
        String objectKey = "test_getObject_with_progress";
        ObjectMetadata metadata = new ObjectMetadata();
        PutObjectResult putResult = obsClient.putObject(bucketName, objectKey,
                new ByteArrayInputStream("testObject".getBytes(StandardCharsets.UTF_8)),metadata);

        assertEquals(200, putResult.getStatusCode());

        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, objectKey);
        getObjectRequest.setProgressListener(status -> {
            // 获取上传平均速率
            System.out.println("AverageSpeed:" + status.getAverageSpeed());
            // 获取上传进度百分比
            System.out.println("TransferPercentage:" + status.getTransferPercentage());
        });
        ObsObject obsObject = obsClient.getObject(getObjectRequest);
        assertEquals(200, obsObject.getMetadata().getStatusCode());
        obsObject.getObjectContent().close();

    }
}
