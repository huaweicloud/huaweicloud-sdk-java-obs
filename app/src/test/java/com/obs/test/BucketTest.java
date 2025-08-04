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

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.internal.Constants;
import com.obs.services.internal.ObsConstraint;
import com.obs.services.model.AvailableZoneEnum;
import com.obs.services.model.BucketCustomDomainInfo;
import com.obs.services.model.BucketTypeEnum;
import com.obs.services.model.BucketVersioningConfiguration;
import com.obs.services.model.CustomDomainCertificateConfig;
import com.obs.services.model.CreateBucketRequest;
import com.obs.services.model.HeaderResponse;
import com.obs.services.model.HistoricalObjectReplicationEnum;
import com.obs.services.model.ListBucketsResult;
import com.obs.services.model.ObjectListing;
import com.obs.services.model.ReplicationConfiguration;
import com.obs.services.model.ReplicationConfiguration.Destination;
import com.obs.services.model.ReplicationConfiguration.Rule;
import com.obs.services.model.RuleStatusEnum;
import com.obs.services.model.SetBucketCustomDomainRequest;
import com.obs.services.model.SetBucketReplicationRequest;
import com.obs.services.model.SetBucketVersioningRequest;
import com.obs.services.model.StorageClassEnum;
import com.obs.services.model.VersioningStatusEnum;
import com.obs.services.model.fs.NewBucketRequest;
import com.obs.services.model.fs.ObsFSBucket;
import com.obs.test.tools.PrepareTestBucket;
import com.obs.test.tools.PropertiesTools;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BucketTest {
    private static final File file = new File("./app/src/test/resource/test_data.properties");
    private static final ArrayList<String> createdBuckets = new ArrayList<>();

    @org.junit.Rule
    public TestName testName = new TestName();

    @org.junit.Rule
    public PrepareTestBucket prepareTestBucket = new PrepareTestBucket();
    @BeforeClass
    public static void create_demo_bucket() throws IOException {
        String beforeBucket = PropertiesTools.getInstance(file).getProperties("beforeBucket");
        String location = PropertiesTools.getInstance(file).getProperties("environment.location");
        CreateBucketRequest request = new CreateBucketRequest();
        request.setBucketName(beforeBucket);
        request.setBucketType(BucketTypeEnum.OBJECT);
        request.setLocation(location);
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        HeaderResponse response = obsClient.createBucket(request);
        assertEquals(200, response.getStatusCode());
        createdBuckets.add(beforeBucket);
    }

    @AfterClass
    public static void delete_created_buckets() {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        for (String bucket : createdBuckets) {
            obsClient.deleteBucket(bucket);
        }
    }

    @Test
    public void test_create_bucket_obs() throws IOException {
        String bucketName = PropertiesTools.getInstance(file).getProperties("bucketPrefix")
                + "creat-bucket001";
        String location = PropertiesTools.getInstance(file).getProperties("environment.location");
        ObsClient obsClient = TestTools.getPipelineEnvironment();

        CreateBucketRequest request = new CreateBucketRequest();
        request.setBucketName(bucketName);
        request.setBucketType(BucketTypeEnum.OBJECT);
        request.setLocation(location);
        HeaderResponse response = obsClient.createBucket(request);

        assertEquals(200, response.getStatusCode());
        createdBuckets.add(bucketName);
    }

    @Test
    public void test_create_bucket_3az() throws IOException {
        String bucketName = PropertiesTools.getInstance(file).getProperties("bucketPrefix")
                + "test-sdk-obs-3az";
        String location = PropertiesTools.getInstance(file).getProperties("location");
        ObsClient obsClient = TestTools.getPipelineEnvironment();

        CreateBucketRequest request = new CreateBucketRequest();
        request.setBucketName(bucketName);
        request.setBucketType(BucketTypeEnum.OBJECT);
        request.setAvailableZone(AvailableZoneEnum.MULTI_AZ);
        request.setLocation(location);
        HeaderResponse response = obsClient.createBucket(request);

        assertEquals(200, response.getStatusCode());
        createdBuckets.add(bucketName);
    }

    @Test
    public void test_create_bucket_pfs() throws IOException {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        String bucketName = PropertiesTools.getInstance(file).getProperties("bucketPrefix")
                + "test-sdk-pfs";
        String location = PropertiesTools.getInstance(file).getProperties("environment.location");

        CreateBucketRequest request = new CreateBucketRequest();
        request.setBucketName(bucketName);
        request.setBucketType(BucketTypeEnum.PFS);
        request.setLocation(location);
        HeaderResponse response = obsClient.createBucket(request);

        assertEquals(200, response.getStatusCode());
        createdBuckets.add(bucketName);
    }

    @Test
    public void test_create_bucket_new_bucket() throws IOException {
        String bucketName = PropertiesTools.getInstance(file).getProperties("bucketPrefix")
                + "test-sdk-pfs-2";
        String location = PropertiesTools.getInstance(file).getProperties("environment.location");
//        ObsClient obsClient = TestTools.getExternalEnvironment();
        ObsClient obsClient = TestTools.getPipelineEnvironment();

//        CreateBucketRequest request = new CreateBucketRequest();
//        request.setBucketName("test-sdk-pfs-0000003");
//        request.setBucketType(BucketTypeEnum.PFS);
//        request.setLocation("cn-north-4");

        NewBucketRequest request = new NewBucketRequest(bucketName, location);
        ObsFSBucket response = obsClient.newBucket(request);

        assertEquals(bucketName, response.getBucketName());
        createdBuckets.add(bucketName);
    }

    @Test
    public void test_head_bucket() throws IOException {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        String beforeBucket = PropertiesTools.getInstance(file).getProperties("beforeBucket");
        boolean result = obsClient.headBucket(beforeBucket);

        assertTrue(result);
    }

    @Test
    public void test_list_bucket() {
        ObsClient obsClient = TestTools.getPipelineEnvironment();

        ListBucketsResult result = obsClient.listBucketsV2(null);

        assertEquals(result.getStatusCode(), 200);
    }

    @Test
    public void test_list_objects_in_bucket() throws IOException {

        ObsClient obsClient = TestTools.getPipelineEnvironment();
        assert obsClient != null;
        String beforeBucket = PropertiesTools.getInstance(file).getProperties("beforeBucket");
        int objectSize = 50;
        String objectKey = "objectKey";

        for(int i =0;i<objectSize;i++){
            obsClient.putObject(beforeBucket,objectKey+i,new ByteArrayInputStream("Hello OBS".getBytes(StandardCharsets.UTF_8)));
        }
        ObjectListing result = obsClient.listObjects(beforeBucket);

        assertEquals(result.getBucketName(), beforeBucket);

        assertEquals(result.getObjects().size(), objectSize);

        for(int i =0;i<objectSize;i++){
            obsClient.deleteObject(beforeBucket,objectKey+i);
        }
    }

    @Test
    public void test_set_bucket_version() {
        ObsClient obsClient = TestTools.getPipelineEnvironment();

        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);

        SetBucketVersioningRequest request = new SetBucketVersioningRequest(bucketName, VersioningStatusEnum.ENABLED);
        HeaderResponse result = obsClient.setBucketVersioning(request);
        assertEquals(result.getStatusCode(), 200);
        BucketVersioningConfiguration config = obsClient.getBucketVersioning(bucketName);
        assertEquals(config.getVersioningStatus().getCode(), "Enabled");

        result = obsClient.setBucketVersioning(bucketName, new BucketVersioningConfiguration(VersioningStatusEnum.SUSPENDED));
        assertEquals(result.getStatusCode(), 200);
        config = obsClient.getBucketVersioning(bucketName);
        assertEquals(config.getVersioningStatus().getCode(), "Suspended");
    }

    @Test(expected = ObsException.class)
    public void test_set_bucket_version_exception() {
        ObsClient obsClient = TestTools.getPipelineEnvironment();

        String bucketName = "putobject-bucket-0603051314";

        HeaderResponse result = obsClient.setBucketVersioning(bucketName, new BucketVersioningConfiguration(VersioningStatusEnum.getValueFromCode("ERROR")));
    }

    @Test
    public void test_set_bucket_replication() {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        assert obsClient != null;
        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);

        ReplicationConfiguration replicationConfiguration = new ReplicationConfiguration();
        Rule rule = new Rule();
        rule.setId("test_rule11"); // id为一个自定义的唯一字符串
        rule.setPrefix("test");    // 前缀
        rule.setStatus(RuleStatusEnum.ENABLED);
        Destination dest = new Destination();
        dest.setBucket("test-2023-9-11-111"); // 目标桶名称
        rule.setDestination(dest);
        replicationConfiguration.getRules().add(rule);
        replicationConfiguration.setAgency("sdk-test"); // IAM委托名称

        SetBucketReplicationRequest request = new SetBucketReplicationRequest(bucketName, replicationConfiguration);


        // 需要不同region的桶，不能在乌兰三跑
//        obsClient.setBucketReplication(request);
    }

    @Test
    public void test_get_bucket_replication() {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        assert obsClient != null;
        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);

        //obsClient.createBucket("dest-replication");
        ReplicationConfiguration replicationConfiguration = new ReplicationConfiguration();
        ArrayList<Rule> rules = new ArrayList<>();
        Rule rule = new Rule();
        rule.setId("replicationID");
        rule.setHistoricalObjectReplication(HistoricalObjectReplicationEnum.DISABLED);
        rule.setStatus(RuleStatusEnum.DISABLED);
        rule.setPrefix("prefix");
        Destination destination = new Destination();
        destination.setBucket("dest-replication");
        destination.setObjectStorageClass(StorageClassEnum.STANDARD);
        rule.setDestination(destination);
        rules.add(rule);
        replicationConfiguration.setRules(rules);
        replicationConfiguration.setAgency("sdk-test");
        // 需要不同region的桶，不能在乌兰三跑
//        obsClient.setBucketReplication(bucketName,replicationConfiguration);
//        ReplicationConfiguration replicationConfiguration2 = obsClient.getBucketReplication(bucketName);
//        System.out.println(replicationConfiguration2.toString());
    }
/**
     * 测试获取自定义域名
     */
    @Test
    public void test_get_bucket_custom_domain() {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        assert obsClient != null;
        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);
        HeaderResponse response = obsClient.setBucketCustomDomain(bucketName, "test.huawei.com");
        Assert.assertEquals(200,response.getStatusCode());
        BucketCustomDomainInfo bucketCustomDomainInfo = obsClient.getBucketCustomDomain(bucketName);
        System.out.println(bucketCustomDomainInfo.toString());
    }

    /**
     * 测试设置自定义域名
     */
    @Test
    public void test_set_bucket_custom_domain() {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        assert obsClient != null;
        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);
        HeaderResponse response = obsClient.setBucketCustomDomain(bucketName, "test.huawei.com");
        Assert.assertEquals(200,response.getStatusCode());
        System.out.println(response.toString());
    }

    @Test
    public void test_set_bucket_custom_domain_check_certificate_name() throws IOException, ObsException {
        // Initialize client and load properties
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        assertNotNull(obsClient);
        String bucketName = PropertiesTools.getInstance(file).getProperties("customCertificateBucketName");
        String domainName = PropertiesTools.getInstance(file).getProperties("customCertificateDomainName");
        String customCertificateId = PropertiesTools.getInstance(file).getProperties("customCertificateId");
        String customCertificate = PropertiesTools.getInstance(file).getProperties("customCertificate");
        String customCertificateChain = PropertiesTools.getInstance(file).getProperties("customCertificateChain");
        String customCertificatePrivateKey = PropertiesTools.getInstance(file).getProperties("customCertificatePrivateKey");

        // Prepare certificate configuration without certificate name
        CustomDomainCertificateConfig config = new CustomDomainCertificateConfig();
        config.setCertificate(customCertificate);
        config.setPrivateKey(customCertificatePrivateKey);
        if (Objects.nonNull(customCertificateChain) && !customCertificateChain.isEmpty()) {
            config.setCertificateChain(customCertificateChain);
        }
        if (Objects.nonNull(customCertificateId) && !customCertificateId.isEmpty()) {
            config.setCertificateId(customCertificateId);
        }

        // Verify that missing certificate name throws error
        try {
            config.setName("");
            SetBucketCustomDomainRequest req = new SetBucketCustomDomainRequest(bucketName, domainName, config);
            obsClient.setBucketCustomDomain(req);
            fail("Expected IllegalArgumentException due to missing certificate name");
        } catch (IllegalArgumentException e) {
            // Adjust expected message to match actual output
            assertEquals("Certificate name cannot be null", e.getMessage());
        }

        // Verify that a certificate name that is too short throws error
        try {
            config.setName("aa");
            SetBucketCustomDomainRequest req = new SetBucketCustomDomainRequest(bucketName, domainName, config);
            obsClient.setBucketCustomDomain(req);
            fail("Expected IllegalArgumentException due to short certificate name");
        } catch (IllegalArgumentException e) {
            assertEquals("Name length should be between "
                    + ObsConstraint.CUSTOM_DOMAIN_NAME_MIN_LENGTH + " and "
                    + ObsConstraint.CUSTOM_DOMAIN_NAME_MAX_LENGTH + " characters.", e.getMessage());
        }

        // Verify that a valid certificate name succeeds
        config.setName("aaa");
        SetBucketCustomDomainRequest req = new SetBucketCustomDomainRequest(bucketName, domainName, config);
        HeaderResponse response = obsClient.setBucketCustomDomain(req);
        Assert.assertEquals(200, response.getStatusCode());

        // Verify that a 64-digit certificate name throws error
        try {
            SecureRandom random = new SecureRandom();
            StringBuilder sb = new StringBuilder(64);
            for (int i = 0; i < 64; i++) {
                sb.append(random.nextInt(10));
            }
            config.setName(sb.toString());
            req.setCustomDomainCertificateConfig(config);
            obsClient.setBucketCustomDomain(req);
            fail("Expected IllegalArgumentException due to invalid certificate name length");
        } catch (IllegalArgumentException e) {
            assertEquals("Name length should be between "
                    + ObsConstraint.CUSTOM_DOMAIN_NAME_MIN_LENGTH + " and "
                    + ObsConstraint.CUSTOM_DOMAIN_NAME_MAX_LENGTH + " characters.", e.getMessage());
        }

        // Verify that a certificate name with Chinese characters succeeds
        config.setName("演示证书名称");
        req.setCustomDomainCertificateConfig(config);
        response = obsClient.setBucketCustomDomain(req);
        Assert.assertEquals(200, response.getStatusCode());
        obsClient.deleteBucketCustomDomain(bucketName, domainName);
    }

    @Test
    public void test_set_bucket_custom_domain_check_certificate_id() throws IOException, ObsException {
        // Initialize client and load properties
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        assertNotNull(obsClient);
        String bucketName = PropertiesTools.getInstance(file).getProperties("customCertificateBucketName");
        String domainName = PropertiesTools.getInstance(file).getProperties("customCertificateDomainName");
        String customCertificateName = PropertiesTools.getInstance(file).getProperties("customCertificateName");
        String customCertificateId = PropertiesTools.getInstance(file).getProperties("customCertificateId");
        String customCertificate = PropertiesTools.getInstance(file).getProperties("customCertificate");
        String customCertificateChain = PropertiesTools.getInstance(file).getProperties("customCertificateChain");
        String customCertificatePrivateKey = PropertiesTools.getInstance(file).getProperties("customCertificatePrivateKey");

        // Prepare certificate configuration without CertificateId
        CustomDomainCertificateConfig config = new CustomDomainCertificateConfig();
        config.setName(customCertificateName);
        config.setCertificate(customCertificate);
        config.setPrivateKey(customCertificatePrivateKey);
        if (Objects.nonNull(customCertificateChain) && !customCertificateChain.isEmpty()) {
            config.setCertificateChain(customCertificateChain);
        }
        SetBucketCustomDomainRequest req = new SetBucketCustomDomainRequest(bucketName, domainName, config);

        // Verify that configuration without CertificateId succeeds
        HeaderResponse response = obsClient.setBucketCustomDomain(req);
        Assert.assertEquals(200, response.getStatusCode());

        // Verify that configuration with a valid CertificateId succeeds
        config.setCertificateId(customCertificateId);
        req.setCustomDomainCertificateConfig(config);
        response = obsClient.setBucketCustomDomain(req);
        Assert.assertEquals(200, response.getStatusCode());

        // Verify that a CertificateId of invalid length throws error
        try {
            config.setCertificateId(customCertificateId.substring(0, 15));
            req.setCustomDomainCertificateConfig(config);
            obsClient.setBucketCustomDomain(req);
            fail("Expected IllegalArgumentException due to invalid CertificateId length");
        } catch (IllegalArgumentException e) {
            assertEquals("CertificateId length should be exactly "
                    + ObsConstraint.CUSTOM_DOMAIN_CERTIFICATE_ID_MIN_LENGTH + " characters.", e.getMessage());
        }
        obsClient.deleteBucketCustomDomain(bucketName, domainName);
    }

    @Test
    public void test_set_bucket_custom_domain_check_certificate() throws IOException, ObsException {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        assertNotNull(obsClient);
        HeaderResponse response;

        String bucketName = PropertiesTools.getInstance(file).getProperties("customCertificateBucketName");
        String domainName = PropertiesTools.getInstance(file).getProperties("customCertificateDomainName");
        String customCertificateName = PropertiesTools.getInstance(file).getProperties("customCertificateName");
        String customCertificateId = PropertiesTools.getInstance(file).getProperties("customCertificateId");
        String customCertificate = PropertiesTools.getInstance(file).getProperties("customCertificate");
        String customCertificateChain = PropertiesTools.getInstance(file).getProperties("customCertificateChain");
        String customCertificatePrivateKey = PropertiesTools.getInstance(file).getProperties("customCertificatePrivateKey");

        CustomDomainCertificateConfig config = new CustomDomainCertificateConfig();
        config.setName(customCertificateName);
        config.setPrivateKey(customCertificatePrivateKey);
        if (Objects.nonNull(customCertificateChain) && !customCertificateChain.isEmpty()) {
            config.setCertificateChain(customCertificateChain);
        }
        if (Objects.nonNull(customCertificateId) && !customCertificateId.isEmpty()) {
            config.setCertificateId(customCertificateId);
        }

        SetBucketCustomDomainRequest customDomainRequest = new SetBucketCustomDomainRequest(bucketName, domainName, config);

        // Case: The certificate configuration does not contain the certificate.
        try {
            obsClient.setBucketCustomDomain(customDomainRequest);
        } catch (IllegalArgumentException e) {
            assertEquals("Certificate cannot be null", e.getMessage());
        }

        // Case: The certificate contains a correct certificate
        config.setCertificate(customCertificate);
        customDomainRequest.setCustomDomainCertificateConfig(config);
        response = obsClient.setBucketCustomDomain(customDomainRequest);
        Assert.assertEquals(200, response.getStatusCode());
        obsClient.deleteBucketCustomDomain(bucketName, domainName);
    }

    @Test
    public void test_set_bucket_custom_domain_check_certificate_chain() throws IOException, ObsException {
        ObsClient obsClient = TestTools.getPipelineEnvironment();;
        assertNotNull(obsClient);
        HeaderResponse response;

        String bucketName = PropertiesTools.getInstance(file).getProperties("customCertificateBucketName");
        String domainName = PropertiesTools.getInstance(file).getProperties("customCertificateDomainName");

        String customCertificateName = PropertiesTools.getInstance(file).getProperties("customCertificateName");
        String customCertificateId = PropertiesTools.getInstance(file).getProperties("customCertificateId");
        String customCertificate = PropertiesTools.getInstance(file).getProperties("customCertificate");
        String customCertificateChain = PropertiesTools.getInstance(file).getProperties("customCertificateChain");
        String customCertificatePrivateKey = PropertiesTools.getInstance(file).getProperties("customCertificatePrivateKey");

        try {
            CustomDomainCertificateConfig config = new CustomDomainCertificateConfig();
            config.setName(customCertificateName);
            config.setCertificate(customCertificate);
            config.setPrivateKey(customCertificatePrivateKey);
            if (Objects.nonNull(customCertificateId) && !customCertificateId.isEmpty()) {
                config.setCertificateId(customCertificateId);
            }

            SetBucketCustomDomainRequest customDomainRequest = new SetBucketCustomDomainRequest(bucketName, domainName, config);

            // Case: The certificate configuration does not contain CertificateChain.
            response = obsClient.setBucketCustomDomain(customDomainRequest);
            Assert.assertEquals(200, response.getStatusCode());

            // Case: The certificate contains the correct CertificateChain
            config.setCertificateChain(customCertificateChain);
            customDomainRequest.setCustomDomainCertificateConfig(config);
            response = obsClient.setBucketCustomDomain(customDomainRequest);
            Assert.assertEquals(200, response.getStatusCode());
        } finally {
            obsClient.deleteBucketCustomDomain(bucketName, domainName);
        }
    }

    @Test
    public void test_set_bucket_custom_domain_check_certificate_private_key() throws IOException, ObsException {
        // Initialize client and load properties
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        assertNotNull(obsClient);
        HeaderResponse response;
        String bucketName = PropertiesTools.getInstance(file).getProperties("customCertificateBucketName");
        String domainName = PropertiesTools.getInstance(file).getProperties("customCertificateDomainName");
        String customCertificateName = PropertiesTools.getInstance(file).getProperties("customCertificateName");
        String customCertificateId = PropertiesTools.getInstance(file).getProperties("customCertificateId");
        String customCertificate = PropertiesTools.getInstance(file).getProperties("customCertificate");
        String customCertificateChain = PropertiesTools.getInstance(file).getProperties("customCertificateChain");
        String customCertificatePrivateKey = PropertiesTools.getInstance(file).getProperties("customCertificatePrivateKey");

        try {
            // Prepare configuration without private key
            CustomDomainCertificateConfig config = new CustomDomainCertificateConfig();
            config.setName(customCertificateName);
            config.setCertificate(customCertificate);
            if (Objects.nonNull(customCertificateChain) && !customCertificateChain.isEmpty()) {
                config.setCertificateChain(customCertificateChain);
            }
            if (Objects.nonNull(customCertificateId) && !customCertificateId.isEmpty()) {
                config.setCertificateId(customCertificateId);
            }
            SetBucketCustomDomainRequest req = new SetBucketCustomDomainRequest(bucketName, domainName, config);

            // Verify that missing private key throws error
            try {
                obsClient.setBucketCustomDomain(req);
                fail("Expected error: Private key cannot be null");
            } catch (IllegalArgumentException e) {
                assertEquals("Private key cannot be null", e.getMessage());
            }

            // Verify that configuration with correct private key succeeds
            config.setPrivateKey(customCertificatePrivateKey);
            req.setCustomDomainCertificateConfig(config);
            response = obsClient.setBucketCustomDomain(req);
            Assert.assertEquals(200, response.getStatusCode());
        } finally {
            try {
                obsClient.deleteBucketCustomDomain(bucketName, domainName);
            } catch (ObsException ex) {
                // Ignore deletion errors if domain not found
            }
        }
    }

    @Test
    public void test_set_bucket_custom_domain_check_certificate_safety() throws IOException, ObsException {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        ObsClient obsClientInSecure = TestTools.getPipelineEnvironmentInSecure();
        assertNotNull(obsClient);
        assertNotNull(obsClientInSecure);
        HeaderResponse response;
        String bucketName = PropertiesTools.getInstance(file).getProperties("customCertificateBucketName");
        String domainName = PropertiesTools.getInstance(file).getProperties("customCertificateDomainName");
        String customCertificateName = PropertiesTools.getInstance(file).getProperties("customCertificateName");
        String customCertificateId = PropertiesTools.getInstance(file).getProperties("customCertificateId");
        String customCertificate = PropertiesTools.getInstance(file).getProperties("customCertificate");
        String customCertificateChain = PropertiesTools.getInstance(file).getProperties("customCertificateChain");
        String customCertificatePrivateKey = PropertiesTools.getInstance(file).getProperties("customCertificatePrivateKey");

        // HTTP access without certificate should succeed
        try {
            response = obsClientInSecure.setBucketCustomDomain(bucketName, domainName);
            Assert.assertEquals(200, response.getStatusCode());
        } finally {
            try {
                obsClient.deleteBucketCustomDomain(bucketName, domainName);
            } catch (ObsException ex) {
                // Ignore deletion error if domain does not exist
            }
        }

        // HTTPS access without certificate should succeed
        try {
            response = obsClient.setBucketCustomDomain(bucketName, domainName);
            Assert.assertEquals(200, response.getStatusCode());
        } finally {
            try {
                obsClient.deleteBucketCustomDomain(bucketName, domainName);
            } catch (ObsException ex) {
                // Ignore deletion error if domain does not exist
            }
        }

        // Configure certificate details for HTTPS access with certificate
        CustomDomainCertificateConfig config = new CustomDomainCertificateConfig();
        config.setName(customCertificateName);
        config.setCertificate(customCertificate);
        config.setPrivateKey(customCertificatePrivateKey);
        if (Objects.nonNull(customCertificateChain) && !customCertificateChain.isEmpty()) {
            config.setCertificateChain(customCertificateChain);
        }
        if (Objects.nonNull(customCertificateId) && !customCertificateId.isEmpty()) {
            config.setCertificateId(customCertificateId);
        }
        SetBucketCustomDomainRequest customDomainRequest = new SetBucketCustomDomainRequest(bucketName, domainName, config);

        // HTTP access with certificate should fail for insecure transmission
        try {
            obsClientInSecure.setBucketCustomDomain(customDomainRequest);
            fail("Only '" + Constants.HTTPS_PREFIX +
                    "' URLs are allowed for sending certificate details to ensure secure transmission.");
        } catch (IllegalArgumentException e) {
            assertEquals("Only '" + Constants.HTTPS_PREFIX +
                    "' URLs are allowed for sending certificate details to ensure secure transmission.", e.getMessage());
        }

        // HTTPS access with certificate should succeed
        try {
            response = obsClient.setBucketCustomDomain(customDomainRequest);
            Assert.assertEquals(200, response.getStatusCode());
        } finally {
            try {
                obsClient.deleteBucketCustomDomain(bucketName, domainName);
            } catch (ObsException ex) {
                // Ignore deletion error if domain does not exist
            }
        }
    }

    @Test
    public void test_put_get_custom_domain_certificate() throws IOException, ObsException {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        assertNotNull(obsClient);
        HeaderResponse response;
        BucketCustomDomainInfo bucketCustomDomainInfo;
        boolean domainFound;

        String bucketName = PropertiesTools.getInstance(file).getProperties("customCertificateBucketName");
        String domainName = PropertiesTools.getInstance(file).getProperties("customCertificateDomainName");
        String customCertificateName = PropertiesTools.getInstance(file).getProperties("customCertificateName");
        String customCertificateId = PropertiesTools.getInstance(file).getProperties("customCertificateId");
        String customCertificate = PropertiesTools.getInstance(file).getProperties("customCertificate");
        String customCertificateChain = PropertiesTools.getInstance(file).getProperties("customCertificateChain");
        String customCertificatePrivateKey = PropertiesTools.getInstance(file).getProperties("customCertificatePrivateKey");

        // Before: Delete domains if exists
        try {
            obsClient.deleteBucketCustomDomain(bucketName, domainName);
        } catch (Exception e) {
            System.err.println("An error occurred while deleting the custom domain: " + e.getMessage());
        }
        try {
            obsClient.deleteBucketCustomDomain(bucketName, "a" + domainName);
        } catch (Exception e) {
            System.err.println("An error occurred while deleting the custom domain: " + e.getMessage());
        }
        try {
            obsClient.deleteBucketCustomDomain(bucketName, "b" + domainName);
        } catch (Exception e) {
            System.err.println("An error occurred while deleting the custom domain: " + e.getMessage());
        }
        try {
            obsClient.deleteBucketCustomDomain(bucketName, "c" + domainName);
        } catch (Exception e) {
            System.err.println("An error occurred while deleting the custom domain: " + e.getMessage());
        }

        // Case 1: Configure a domain name without a certificate.
        response = obsClient.setBucketCustomDomain(bucketName, domainName);
        Assert.assertEquals(200, response.getStatusCode());
        bucketCustomDomainInfo = obsClient.getBucketCustomDomain(bucketName);
        domainFound = false;
        for (BucketCustomDomainInfo.Domains domain : bucketCustomDomainInfo.getDomains()) {
            if (domain.getDomainName().equals(domainName)) {
                domainFound = true;
                Assert.assertNotNull(domain.getCertificateId());
                break;
            }
        }
        Assert.assertTrue("Domain " + domainName + " was found with a certificateId: " + customCertificateId, domainFound);

        // Case 2: Configure domain name b and have a certificate.
        CustomDomainCertificateConfig config;

        config = new CustomDomainCertificateConfig();
        config.setName(customCertificateName);
        config.setCertificate(customCertificate);
        config.setPrivateKey(customCertificatePrivateKey);
        if (Objects.nonNull(customCertificateChain) && !customCertificateChain.isEmpty()) {
            config.setCertificateChain(customCertificateChain);
        }
        if (Objects.nonNull(customCertificateId) && !customCertificateId.isEmpty()) {
            config.setCertificateId(customCertificateId);
        }
        SetBucketCustomDomainRequest customDomainRequest = new SetBucketCustomDomainRequest(bucketName, "b" + domainName, config);
        response = obsClient.setBucketCustomDomain(customDomainRequest);
        Assert.assertEquals(200, response.getStatusCode());
        bucketCustomDomainInfo = obsClient.getBucketCustomDomain(bucketName);
        domainFound = false;
        for (BucketCustomDomainInfo.Domains domain : bucketCustomDomainInfo.getDomains()) {
            if (domain.getDomainName().equals("b" + domainName)) {
                Assert.assertNotNull(domain.getCertificateId());
                domainFound = true;
                break;
            }
        }
        Assert.assertTrue("Domain " + "b" + domainName + " was not found with certificateId: " + customCertificateId, domainFound);

        // Case 3: Configure domain name c and have a certificate.
        config.setCertificateId(null);

        customDomainRequest.setDomainName("c" + domainName);
        customDomainRequest.setCustomDomainCertificateConfig(config);
        response = obsClient.setBucketCustomDomain(customDomainRequest);
        Assert.assertEquals(200, response.getStatusCode());
        bucketCustomDomainInfo = obsClient.getBucketCustomDomain(bucketName);
        domainFound = false;
        String certificateIdC = "";
        for (BucketCustomDomainInfo.Domains domain : bucketCustomDomainInfo.getDomains()) {
            if (domain.getDomainName().equals("c" + domainName)) {
                certificateIdC = domain.getCertificateId();
                Assert.assertNotNull(certificateIdC);
                domainFound = true;
                break;
            }
        }
        Assert.assertTrue("Domain " + "c" + domainName + " was not found with certificateId: " + customCertificateId, domainFound);

        // Case 4: Set the user-defined domain name to aaa. The domain name contains a certificate.
        config.setName("aaa");
        config.setCertificateId(customCertificateId);
        customDomainRequest.setCustomDomainCertificateConfig(config);
        customDomainRequest.setDomainName(domainName);
        response = obsClient.setBucketCustomDomain(customDomainRequest);
        Assert.assertEquals(200, response.getStatusCode());
        bucketCustomDomainInfo = obsClient.getBucketCustomDomain(bucketName);
        domainFound = false;
        for (BucketCustomDomainInfo.Domains domain : bucketCustomDomainInfo.getDomains()) {
            if (domain.getDomainName().equals(domainName)) {
                Assert.assertNotNull(domain.getCertificateId());
                domainFound = true;
                break;
            }
        }
        Assert.assertTrue("Domain " + domainName + " was not found with certificateId: " + customCertificateId, domainFound);

        try {
            // Case 5: Set the user-defined domain name to domain name b with a new certificate. Obtain the certificate ID
            // corresponding to domain name b and compare it with the certificate ID set in step 3.
            config.setCertificate(customCertificate);
            config.setCertificateId(null);
            customDomainRequest.setCustomDomainCertificateConfig(config);
            customDomainRequest.setDomainName("b" + domainName);
            response = obsClient.setBucketCustomDomain(customDomainRequest);
            Assert.assertEquals(200, response.getStatusCode());
            bucketCustomDomainInfo = obsClient.getBucketCustomDomain(bucketName);
            domainFound = false;
            String certificateIdB = "";
            for (BucketCustomDomainInfo.Domains domain : bucketCustomDomainInfo.getDomains()) {
                if (domain.getDomainName().equals("b" + domainName)) {
                    certificateIdB = domain.getCertificateId();
                    Assert.assertNotNull(certificateIdB);
                    domainFound = true;
                    break;
                }
            }
            Assert.assertFalse("Should be different", certificateIdB.equals(certificateIdC));
            Assert.assertTrue("Domain " + "b" + domainName + " was not found with certificateId: " + customCertificateId, domainFound);
        } finally {
            obsClient.deleteBucketCustomDomain(bucketName, "b" + domainName);
        }

        try {
            // Case: Set a user-defined domain name, configure the C domain name, and obtain the certificate ID corresponding to the C domain name.
            customDomainRequest.setDomainName("c" + domainName);
            customDomainRequest.setCustomDomainCertificateConfig(config);
            response = obsClient.setBucketCustomDomain(customDomainRequest);
            Assert.assertEquals(200, response.getStatusCode());
            bucketCustomDomainInfo = obsClient.getBucketCustomDomain(bucketName);
            domainFound = false;
            for (BucketCustomDomainInfo.Domains domain : bucketCustomDomainInfo.getDomains()) {
                if (domain.getDomainName().equals("c" + domainName)) {
                    Assert.assertNotNull(domain.getCertificateId());
                    domainFound = true;
                    break;
                }
            }
            Assert.assertTrue("Domain " + "c" + domainName + " was not found with certificateId: " + customCertificateId, domainFound);
        } finally {
            obsClient.deleteBucketCustomDomain(bucketName, "c" + domainName);
        }

        // Case: Set the user-defined domain name to domain name a, set the new certificate to bbb, and obtain the certificate ID corresponding to domain name a.
        customDomainRequest.setDomainName("a" + domainName);
        customDomainRequest.setCustomDomainCertificateConfig(config);
        response = obsClient.setBucketCustomDomain(customDomainRequest);
        Assert.assertEquals(200, response.getStatusCode());
        bucketCustomDomainInfo = obsClient.getBucketCustomDomain(bucketName);
        domainFound = false;
        for (BucketCustomDomainInfo.Domains domain : bucketCustomDomainInfo.getDomains()) {
            if (domain.getDomainName().equals("a" + domainName)) {
                Assert.assertNotNull(domain.getCertificateId());
                domainFound = true;
                break;
            }
        }
        Assert.assertTrue("Domain " + "a" + domainName + " was not found with certificateId: " + customCertificateId, domainFound);

        // Case: Set the user-defined domain name b with an invalid certificate ID.
        try {
            config.setCertificateId("invalid");
            customDomainRequest.setCustomDomainCertificateConfig(config);
            customDomainRequest.setDomainName("b" + domainName);
            obsClient.setBucketCustomDomain(customDomainRequest);
        } catch (IllegalArgumentException e) {
            assertEquals("CertificateId length should be exactly " +
                            ObsConstraint.CUSTOM_DOMAIN_CERTIFICATE_ID_MIN_LENGTH + " characters.",
                    e.getMessage());
        } finally {
            obsClient.deleteBucketCustomDomain(bucketName, domainName);
        }
    }

    @Test
    public void test_put_get_delete_custom_domain_certificate() throws IOException, ObsException {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        assertNotNull(obsClient);
        HeaderResponse response;
        BucketCustomDomainInfo bucketCustomDomainInfo;
        String bucketName = PropertiesTools.getInstance(file).getProperties("customCertificateBucketName");
        String domainName = PropertiesTools.getInstance(file).getProperties("customCertificateDomainName");
        String customCertificateName = PropertiesTools.getInstance(file).getProperties("customCertificateName");
        String customCertificate = PropertiesTools.getInstance(file).getProperties("customCertificate");
        String customCertificateChain = PropertiesTools.getInstance(file).getProperties("customCertificateChain");
        String customCertificatePrivateKey = PropertiesTools.getInstance(file).getProperties("customCertificatePrivateKey");

        // Configure a domain without a certificate
        try {
            obsClient.deleteBucketCustomDomain(bucketName, "a" + domainName);
        } catch (Throwable t) {
            // deleteBucketCustomDomain before setBucketCustomDomain
        }
        response = obsClient.setBucketCustomDomain(bucketName, "a" + domainName);
        Assert.assertEquals(200, response.getStatusCode());

        CustomDomainCertificateConfig config = new CustomDomainCertificateConfig();
        config.setName(customCertificateName);
        config.setCertificate(customCertificate);
        config.setPrivateKey(customCertificatePrivateKey);
        if (Objects.nonNull(customCertificateChain) && !customCertificateChain.isEmpty()) {
            config.setCertificateChain(customCertificateChain);
        }
        boolean domainAFound;
        boolean domainBFound;
        String domainACertificateId;
        String domainBCertificateId;
        try {
            // Configure domain "b" with a certificate
            SetBucketCustomDomainRequest customDomainRequest = new SetBucketCustomDomainRequest(bucketName, "b" + domainName, config);
            response = obsClient.setBucketCustomDomain(customDomainRequest);
            Assert.assertEquals(200, response.getStatusCode());
            bucketCustomDomainInfo = obsClient.getBucketCustomDomain(bucketName);
            domainAFound = false;
            domainBFound = false;
            domainACertificateId = "";
            domainBCertificateId = "";
            for (BucketCustomDomainInfo.Domains domain : bucketCustomDomainInfo.getDomains()) {
                if (domain.getDomainName().equals("a" + domainName)) {
                    domainACertificateId = domain.getCertificateId();
                    Assert.assertNotNull(domainACertificateId);
                    domainAFound = true;
                }
                if (domain.getDomainName().equals("b" + domainName)) {
                    domainBCertificateId = domain.getCertificateId();
                    Assert.assertNotNull(domainBCertificateId);
                    domainBFound = true;
                }
            }
            Assert.assertTrue("Domain a" + domainName + " was not found with certificateId: " + domainACertificateId, domainAFound);
            Assert.assertTrue("Domain b" + domainName + " was not found with certificateId: " + domainBCertificateId, domainBFound);
        } finally {
            try {
                response = obsClient.deleteBucketCustomDomain(bucketName, "a" + domainName);
                Assert.assertEquals(204, response.getStatusCode());
            } catch (ObsException ex) {
                // Ignore deletion error if domain does not exist
            }
        }

        try {
            bucketCustomDomainInfo = obsClient.getBucketCustomDomain(bucketName);
            domainAFound = false;
            domainBFound = false;
            String domainACertId = "";
            String domainBCertId = "";
            for (BucketCustomDomainInfo.Domains domain : bucketCustomDomainInfo.getDomains()) {
                if (domain.getDomainName().equals("a" + domainName)) {
                    domainACertId = domain.getCertificateId();
                    Assert.assertNotNull(domainACertId);
                    domainAFound = true;
                }
                if (domain.getDomainName().equals("b" + domainName)) {
                    domainBCertId = domain.getCertificateId();
                    Assert.assertNotNull(domainBCertId);
                    domainBFound = true;
                }
            }
            Assert.assertFalse("Domain a" + domainName + " should not exist after deletion", domainAFound);
            Assert.assertTrue("Domain b" + domainName + " was not found with certificateId: " + domainBCertId, domainBFound);
        } finally {
            try {
                obsClient.deleteBucketCustomDomain(bucketName, "b" + domainName);
            } catch (ObsException ex) {
                // Ignore deletion error if domain does not exist
            }
        }
    }

    /**
     * 测试删除桶自定义域名
     */
    @Test
    public void test_delete_bucket_custom_domain() {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        assert obsClient != null;
        String bucketName = testName.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);
        HeaderResponse response1 = obsClient.setBucketCustomDomain(bucketName, "test.huawei.com");
        Assert.assertEquals(200,response1.getStatusCode());
        HeaderResponse response2 = obsClient.deleteBucketCustomDomain(bucketName, "test.huawei.com");
        Assert.assertEquals(204,response2.getStatusCode());
        System.out.println(response2.toString());
    }

    @Test
    public void test_delete_custom_domain_not_exists() throws IOException {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        assertNotNull(obsClient);
        String bucketName = PropertiesTools.getInstance(file).getProperties("customCertificateBucketName");
        String domainName = "test.non-existed-domain.com";

        try {
            HeaderResponse response = obsClient.deleteBucketCustomDomain(bucketName, domainName);
            fail("Expected ObsException to be thrown");
        } catch (ObsException e) {
            assertEquals(400, e.getResponseCode());
        }
    }

    @Test
    public void test_delete_custom_domain_bucket_not_exists() throws IOException {
        ObsClient obsClient = TestTools.getPipelineEnvironment();
        assertNotNull(obsClient);
        String bucketName = "non-existed-bucket-name";
        String domainName = PropertiesTools.getInstance(file).getProperties("customCertificateDomainName");

        try {
            HeaderResponse response = obsClient.deleteBucketCustomDomain(bucketName, domainName);
            fail("Expected 404 http status in response");
        } catch (ObsException e) {
            assertEquals(404, e.getResponseCode());
        }
    }


    @Test
    public void test_close_obsclient() {
        ObsClient obsClient = TestTools.getPipelineEnvironment();


        String bucketName = "putobject-bucket-0603051314";

        assert obsClient != null;
        obsClient.headBucket(bucketName);

        try {
            obsClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            obsClient.headBucket(bucketName);
        } catch (Exception ignore) {
            System.out.println("use obsClient after close");
        }
    }
}
