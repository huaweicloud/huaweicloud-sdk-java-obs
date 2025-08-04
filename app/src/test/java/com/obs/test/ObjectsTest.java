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

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.obs.services.model.GetObjectMetadataRequest;
import com.obs.services.model.ListVersionsRequest;
import com.obs.services.model.PutObjectResult;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.CopyObjectResult;
import com.obs.services.model.GetObjectRequest;
import com.obs.services.model.HttpMethodEnum;
import com.obs.services.model.InitiateMultipartUploadRequest;
import com.obs.services.model.InitiateMultipartUploadResult;
import com.obs.services.model.ListVersionsResult;
import com.obs.services.model.ObjectMetadata;
import com.obs.services.model.ObsObject;
import com.obs.services.model.PutObjectRequest;
import com.obs.services.model.SetObjectMetadataRequest;
import com.obs.services.model.TemporarySignatureRequest;
import com.obs.services.model.TemporarySignatureResponse;
import org.junit.rules.TestName;

public class ObjectsTest {
    @Rule
    public TestName testName = new TestName();
    @Rule
    public com.obs.test.tools.PrepareTestBucket prepareTestBucket = new com.obs.test.tools.PrepareTestBucket();

    @Test
    public void test_set_object_metadata() throws IOException
    {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);
        String objectKey = bucketName+"-obj";
        assert obsClient != null;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("Hello OBS!".getBytes(StandardCharsets.UTF_8));
        PutObjectRequest request = new PutObjectRequest(bucketName, objectKey);
        obsClient.putObject(request);
        byteArrayInputStream.close();

        SetObjectMetadataRequest setObjectMetadataRequest = new SetObjectMetadataRequest(bucketName, objectKey);
        setObjectMetadataRequest.getMetadata().put("property1", "property-value1");
        setObjectMetadataRequest.getMetadata().put("property2", "%#123");
        ObjectMetadata metadata = obsClient.setObjectMetadata(setObjectMetadataRequest);
        Assert.assertEquals(200, metadata.getStatusCode());

        GetObjectMetadataRequest getObjectMetadataRequest = new GetObjectMetadataRequest(bucketName,objectKey);
        getObjectMetadataRequest.setIsEncodeHeaders(false);
        ObjectMetadata metadataReturn = obsClient.getObjectMetadata(getObjectMetadataRequest);

        Assert.assertEquals(200, metadataReturn.getStatusCode());
        assertEquals("property-value1", metadataReturn.getAllMetadata().get("property1"));
        assertEquals("%#123", metadataReturn.getAllMetadata().get("property2"));
    }

    @Test
    public void test_get_object_metadata_1() {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);

        String objectKey = "test_get_object_metadata_1_obj";
        try
        {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(objectKey.getBytes(StandardCharsets.UTF_8));
            obsClient.putObject(bucketName,objectKey,inputStream);
            inputStream.close();
        }catch (IOException e){

        }
        ObjectMetadata metadata = obsClient.getObjectMetadata(bucketName, objectKey);

        System.out.println(metadata);

        assertNotNull(metadata);
    }
    
    @Test
    public void test_get_object_metadata_2() {
        ByteArrayInputStream byteArrayInputStream = null;
        try (ObsClient obsClient = com.obs.test.TestTools.getPipelineEnvironment()){
            assert obsClient != null;
            String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);
            String objectKey = "objectKey";

            byteArrayInputStream = new ByteArrayInputStream("Hello OBS!".getBytes(StandardCharsets.UTF_8));
            PutObjectResult result = obsClient.putObject(bucketName,objectKey,byteArrayInputStream);
            assertEquals(200,result.getStatusCode());

            ObjectMetadata metadata = obsClient.getObjectMetadata(bucketName, objectKey);
            assertNotNull(metadata);
            System.out.println(metadata);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(byteArrayInputStream != null){
                try
                {
                    byteArrayInputStream.close();
                }catch (IOException ignored){
                }
            }
        }
    }

    @Test
    public void test_download_object_metadata() {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);
        String objectKey = bucketName+"-obj";

        try
        {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bucketName.getBytes(StandardCharsets.UTF_8));
            PutObjectRequest request = new PutObjectRequest(bucketName, objectKey, byteArrayInputStream);
            obsClient.putObject(request);
            byteArrayInputStream.close();
            ObsObject metadata = obsClient.getObject(bucketName, objectKey);

            System.out.println(metadata);

            assertNotNull(metadata);
        }catch (IOException ignore){

        }
    }

    @Test
    public void test_put_object_and_set_acl() {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);
        String objectKey = bucketName+"-obj";

        try
        {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bucketName.getBytes(StandardCharsets.UTF_8));
            PutObjectRequest request = new PutObjectRequest(bucketName, objectKey, byteArrayInputStream);
            obsClient.putObject(request);
            byteArrayInputStream.close();
            ObsObject metadata = obsClient.getObject(bucketName, objectKey);

            System.out.println(metadata);

            assertNotNull(metadata);
        }catch (IOException ignore){

        }
    }

    @Test
    public void test_put_object_base() {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);
        String objectKey = bucketName+"-obj";

        try
        {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bucketName.getBytes(StandardCharsets.UTF_8));
            PutObjectRequest request = new PutObjectRequest(bucketName, objectKey, byteArrayInputStream);
            PutObjectResult result = obsClient.putObject(request);
            assertEquals(200, result.getStatusCode());
            byteArrayInputStream.close();
        }catch (IOException ignore){

        }
    }
    
    @Test
    public void test_put_object_metadata() {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);
        String objectKey = bucketName+"-obj";
        String objectKeyNoMeta = bucketName+"-obj-no-metadata";

        try
        {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bucketName.getBytes(StandardCharsets.UTF_8));
            PutObjectRequest request = new PutObjectRequest(bucketName, objectKey, byteArrayInputStream);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentDisposition("mytest,file=test");
            metadata.setCacheControl("no-cache-me");
            metadata.setContentEncoding("test-encoding");
            metadata.setContentType("test/type");
            metadata.setContentLanguage("test-language-zh-CN");
            metadata.setExpires("test-expires");
            request.setMetadata(metadata);

            obsClient.putObject(request);
            byteArrayInputStream.close();

            ObjectMetadata metadata2 = obsClient.getObjectMetadata(bucketName, objectKey);

            System.out.println(metadata2);
            assertEquals(metadata2.getContentEncoding(), "test-encoding");
            assertEquals(metadata2.getContentType(), "test/type");
            assertEquals(metadata2.getContentDisposition(), "mytest,file=test");
            assertEquals(metadata2.getCacheControl(), "no-cache-me");
            assertEquals(metadata2.getContentLanguage(), "test-language-zh-CN");
            assertEquals(metadata2.getExpires(), "test-expires");


            // Case-2
            byteArrayInputStream = new ByteArrayInputStream(bucketName.getBytes(StandardCharsets.UTF_8));
            request = new PutObjectRequest(bucketName, objectKeyNoMeta, byteArrayInputStream);

            metadata = new ObjectMetadata();
            request.setMetadata(metadata);

            obsClient.putObject(request);
            byteArrayInputStream.close();

            metadata2 = obsClient.getObjectMetadata(bucketName, objectKeyNoMeta);
            System.out.println(metadata2);
            assertEquals(metadata2.getContentEncoding(), null);
            assertEquals(metadata2.getContentDisposition(), null);
            assertEquals(metadata2.getCacheControl(), null);
            assertEquals(metadata2.getExpires(), null);
            assertEquals(metadata2.getContentLanguage(), null);
            assertEquals(metadata2.getContentType(), "application/octet-stream");
            PutObjectResult result = obsClient.putObject(request);
            assertEquals(200, result.getStatusCode());
        }catch (IOException ignore){

        }

    }

    @Test
    public void test_get_object_base() throws IOException {
        ObsClient obsClient = com.obs.test.TestTools.getPipelineEnvironment();
        assert obsClient != null;
        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);
        String objectKey = "objectKey";

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("Hello OBS!".getBytes(StandardCharsets.UTF_8));
        PutObjectResult result = obsClient.putObject(bucketName,objectKey,byteArrayInputStream);
        assertEquals(200,result.getStatusCode());
        byteArrayInputStream.close();

        ObsObject obsObject = obsClient.getObject(bucketName, objectKey);
        // 读取对象内容
        System.out.println("Object content:");
        InputStream input = obsObject.getObjectContent();

        int byteRead;
        ByteArrayOutputStream bos = null;
        try {
            byte[] buffer = new byte[1024];
            bos = new ByteArrayOutputStream();
            while ((byteRead=input.read(buffer)) != -1){
                bos.write(buffer, 0, byteRead);
            }

            System.out.println(bos);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(bos != null){
                bos.close();
            }
            if(input != null){
                input.close();
            }
        }
    }
    
    @Test
    public void test_get_object_by_range() throws IOException {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        assert obsClient != null;
        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);
        String objectKey = bucketName+"-obj";
        ByteArrayInputStream byteArrayInputStream;

        byteArrayInputStream = new ByteArrayInputStream("Hello OBS!".getBytes(StandardCharsets.UTF_8));
        PutObjectResult putObjectResult = obsClient.putObject(bucketName, objectKey,byteArrayInputStream);
        assertEquals(200 ,putObjectResult.getStatusCode());

        GetObjectRequest request = new GetObjectRequest(bucketName, objectKey);
        request.setRangeStart(0L);
        request.setRangeEnd(5L);
        ObsObject obsObject = obsClient.getObject(request);

        // 读取对象内容
        System.out.println("Object content:");
        InputStream input = obsObject.getObjectContent();

        int byteread = 0;

        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1204];
            int length;
            while ((byteread = input.read(buffer)) != -1) {
                outputStream.write(buffer, 0, byteread);
            }
            System.out.println(outputStream);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            input.close();
            outputStream.close();
            byteArrayInputStream.close();
        }
    }

    @Test
    public void test_copy_object() {
        ObsClient obsClient = TestTools.getPipelineEnvironment();

        // ObsObject metadata = obsClient.getObject(bucketName,
        // "00d451d484662462a0ea6d7e507dd48c-87-tcmeitulongmixwithobsfunc002-long-1-1");

        try {
            CopyObjectResult result = obsClient.copyObject("sourcebucketname", "sourceobjectname", "destbucketname",
                    "destobjectname");

            System.out.println("\t" + result.getStatusCode());
            System.out.println("\t" + result.getEtag());
        } catch (ObsException e) {
            System.out.println("HTTP Code: " + e.getResponseCode());
            System.out.println("Error Code:" + e.getErrorCode());
            System.out.println("Error Message: " + e.getErrorMessage());

            System.out.println("Request ID:" + e.getErrorRequestId());
            System.out.println("Host ID:" + e.getErrorHostId());
        }
    }

    @Test
    public void test_temporarySignatureRequest_for_image() {
        ObsClient obsClient = com.obs.test.TestTools.getPipelineEnvironment();
        assert obsClient != null;

        long expireSeconds = 3600L;

        TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.GET, expireSeconds);
        request.setBucketName("bucketname");
        request.setObjectKey("objectname.jpg");

        // 设置图片处理参数，对图片依次进行缩放、旋转
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("x-image-process", "image/resize,m_fixed,w_100,h_100/rotate,90");
        request.setQueryParams(queryParams);

        // 生成临时授权URL
        TemporarySignatureResponse response = obsClient.createTemporarySignature(request);
        System.out.println(response.getSignedUrl());
    }

    @Test
    public void initiateMultipartUpload() {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);

        String objectKey = "initiateMultipartUpload_test_";
        for (int i = 0; i < 50; i++) {
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName,
                    objectKey + "-" + i);
            InitiateMultipartUploadResult result = obsClient.initiateMultipartUpload(request);
            System.out.println(result.getObjectKey() + ";   " + result.getUploadId());
        }
    }
    
    @Test
    public void test_list_version_base()
    {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);

        ListVersionsResult result = obsClient.listVersions(bucketName);
        Assert.assertEquals(200, result.getStatusCode());
    }
}
