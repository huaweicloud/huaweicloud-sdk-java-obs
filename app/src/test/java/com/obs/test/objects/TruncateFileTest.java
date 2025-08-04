package com.obs.test.objects;

import com.obs.services.ObsClient;
import com.obs.services.model.BucketTypeEnum;
import com.obs.services.model.CreateBucketRequest;
import com.obs.services.model.HeaderResponse;
import com.obs.services.model.ObjectMetadata;
import com.obs.services.model.PutObjectRequest;
import com.obs.services.model.PutObjectResult;
import com.obs.services.model.fs.TruncateFileRequest;
import com.obs.services.model.fs.TruncateFileResult;
import com.obs.test.TestTools;
import com.obs.test.tools.PrepareTestBucket;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class TruncateFileTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public PrepareTestBucket prepareTestBucket = new PrepareTestBucket();

    @Rule
    public TestName testName = new TestName();

    @Test
    public void truncate_file_test_002() {
        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT)+"-pfs";
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        assert obsClient != null;
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
        createBucketRequest.setBucketType(BucketTypeEnum.PFS);
        HeaderResponse response = obsClient.createBucket(createBucketRequest);
        assertEquals(200, response.getStatusCode());
        String objectKey = "truncate_file_test_001";

        PutObjectRequest putRequest = new PutObjectRequest();
        putRequest.setObjectKey(objectKey);
        putRequest.setBucketName(bucketName);
        putRequest.setInput(new ByteArrayInputStream("testObject".getBytes(StandardCharsets.UTF_8)));

        PutObjectResult putResult = obsClient.putObject(putRequest);
        assertEquals(200, putResult.getStatusCode());

        TruncateFileRequest request = new TruncateFileRequest();
        request.setBucketName(bucketName);
        request.setObjectKey(objectKey);
        request.setNewLength(4);

        TruncateFileResult result = obsClient.truncateFile(request);
        assertEquals(204, result.getStatusCode());

        ObjectMetadata metadata = obsClient.getObjectMetadata(bucketName, objectKey);
        assertEquals(Long.valueOf(4), metadata.getContentLength());
        obsClient.deleteObject(bucketName,objectKey);
        obsClient.deleteBucket(bucketName);
    }
}
