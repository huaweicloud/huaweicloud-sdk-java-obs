package com.obs.test;

import static org.junit.Assert.assertTrue;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.crypto.CryptoObsClient;
import com.obs.services.crypto.CtrRSACipherGenerator;
import com.obs.services.model.AbortMultipartUploadRequest;
import com.obs.services.model.AuthTypeEnum;
import com.obs.services.model.BucketTypeEnum;
import com.obs.services.model.CreateBucketRequest;
import com.obs.services.model.HeaderResponse;
import com.obs.services.model.ListMultipartUploadsRequest;
import com.obs.services.model.ListVersionsRequest;
import com.obs.services.model.ListVersionsResult;
import com.obs.services.model.MultipartUpload;
import com.obs.services.model.MultipartUploadListing;
import com.obs.services.model.VersionOrDeleteMarker;
import com.obs.test.tools.PropertiesTools;

import okhttp3.Dns;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestTools {
    private static final File file = new File("app/src/test/resource/test_data.properties");

    public static File getPropertiesFile() {
        return file;
    }

    /**
     * 获取自定义内部环境
     */
    public static ObsClient getCustomPipelineEnvironment() {
        try {
            String endPoint = PropertiesTools.getInstance(file).getProperties("environment.endpoint");
            String ak = PropertiesTools.getInstance(file).getProperties("environment.ak");
            String sk = PropertiesTools.getInstance(file).getProperties("environment.sk");
            String authType = PropertiesTools.getInstance(file).getProperties("environment.authType");
            ObsConfiguration config = new ObsConfiguration();
            config.setSocketTimeout(30000);
            config.setConnectionTimeout(10000);
            config.setEndPoint(endPoint);
            config.setLocalAuthTypeCacheCapacity(3);
            if (authType.equals("v2")) {
                config.setAuthType(AuthTypeEnum.V2);
            } else {
                config.setAuthType(AuthTypeEnum.OBS);
            }
            return new ObsClient(ak, sk, config);

        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取内部环境
     */
    public static ObsClient getPipelineEnvironment() {
        try {
            String endPoint = PropertiesTools.getInstance(file).getProperties("environment.endpoint");
            String ak = PropertiesTools.getInstance(file).getProperties("environment.ak");
            String sk = PropertiesTools.getInstance(file).getProperties("environment.sk");
            String authType = PropertiesTools.getInstance(file).getProperties("environment.authType");
            ObsConfiguration config = new ObsConfiguration();
            config.setSocketTimeout(30000);
            config.setConnectionTimeout(10000);
            config.setEndPoint(endPoint);
            config.setAuthTypeNegotiation(false);
            if (authType.equals("v2")) {
                config.setAuthType(AuthTypeEnum.V2);
            } else {
                config.setAuthType(AuthTypeEnum.OBS);
            }
            return new ObsClient(ak, sk, config);

        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    public static ObsClient getPipelineEnvironmentWithCustomisedDns(Dns customizedDns) {
        try {
            String endPoint = PropertiesTools.getInstance(file).getProperties("environment.endpoint");
            String ak = PropertiesTools.getInstance(file).getProperties("environment.ak");
            String sk = PropertiesTools.getInstance(file).getProperties("environment.sk");
            String authType = PropertiesTools.getInstance(file).getProperties("environment.authType");
            ObsConfiguration config = new ObsConfiguration();
            config.setSocketTimeout(30000);
            config.setConnectionTimeout(10000);
            config.setEndPoint(endPoint);
            config.setAuthTypeNegotiation(false);
            config.setCustomizedDnsImpl(customizedDns);
            if (authType.equals("v2")) {
                config.setAuthType(AuthTypeEnum.V2);
            } else {
                config.setAuthType(AuthTypeEnum.OBS);
            }
            return new ObsClient(ak, sk, config);

        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static CryptoObsClient getPipelineCryptoEnvironment() {
        try {
            String endPoint = PropertiesTools.getInstance(file).getProperties("environment.endpoint");
            String ak = PropertiesTools.getInstance(file).getProperties("environment.ak");
            String sk = PropertiesTools.getInstance(file).getProperties("environment.sk");
            String authType = PropertiesTools.getInstance(file).getProperties("environment.authType");
            String privateKeyPath = PropertiesTools.getInstance(file).getProperties("rsa.privateKeyPath");
            String publicKeyPath = PropertiesTools.getInstance(file).getProperties("rsa.publicKeyPath");
            ObsConfiguration config = new ObsConfiguration();
            config.setSocketTimeout(30000);
            config.setConnectionTimeout(10000);
            config.setEndPoint(endPoint);
            config.setAuthTypeNegotiation(false);
            if (authType.equals("v2")) {
                config.setAuthType(AuthTypeEnum.V2);
            } else {
                config.setAuthType(AuthTypeEnum.OBS);
            }
            PrivateKey privateKeyObj = CtrRSACipherGenerator.importPKCS8PrivateKey(privateKeyPath);
            PublicKey publicKeyObj = CtrRSACipherGenerator.importPublicKey(publicKeyPath);
            CtrRSACipherGenerator ctrRSACipherGenerator =
                    new CtrRSACipherGenerator(
                            "test_master_key", true, config.getSecureRandom(), privateKeyObj, publicKeyObj);
            return new CryptoObsClient(ak, sk, config, ctrRSACipherGenerator);

        } catch (IllegalArgumentException | IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getKMSID() {
        try {
            return PropertiesTools.getInstance(file).getProperties("kmsID");
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getAuthType() {
        try {
            return PropertiesTools.getInstance(file).getProperties("environment.authType");
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取请求者付费的测试环境
     */
    public static ObsClient getRequestPaymentEnvironment_User1() {
        try {
            String endPoint = PropertiesTools.getInstance(file).getProperties("environment.1.endpoint");
            String ak = PropertiesTools.getInstance(file).getProperties("environment.1.ak");
            String sk = PropertiesTools.getInstance(file).getProperties("environment.1.sk");

            ObsConfiguration config = new ObsConfiguration();
            config.setSocketTimeout(30000);
            config.setConnectionTimeout(10000);
            config.setEndPoint(endPoint);

            return new ObsClient(ak, sk, config);

        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取请求者付费的测试环境
     */
    public static ObsClient getRequestPaymentEnvironment_User2() {
        try {
            String endPoint = PropertiesTools.getInstance(file).getProperties("environment.2.endpoint");
            String ak = PropertiesTools.getInstance(file).getProperties("environment.2.ak");
            String sk = PropertiesTools.getInstance(file).getProperties("environment.2.sk");

            ObsConfiguration config = new ObsConfiguration();
            config.setSocketTimeout(30000);
            config.setConnectionTimeout(10000);
            config.setEndPoint(endPoint);

            return new ObsClient(ak, sk, config);

        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取请求者付费的测试环境
     */
    public static ObsClient getEnvironment_User3() {
        try {
            String endPoint = PropertiesTools.getInstance(file).getProperties("environment.3.endpoint");
            String ak = PropertiesTools.getInstance(file).getProperties("environment.3.ak");
            String sk = PropertiesTools.getInstance(file).getProperties("environment.3.sk");

            ObsConfiguration config = new ObsConfiguration();
            config.setSocketTimeout(30000);
            config.setConnectionTimeout(10000);
            config.setEndPoint(endPoint);

            return new ObsClient(ak, sk, config);

        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取外部环境
     */
    public static ObsClient getExternalEnvironment() {
        try {
            String endPoint = PropertiesTools.getInstance(file).getProperties("environment.me.endpoint");
            String ak = PropertiesTools.getInstance(file).getProperties("environment.me.ak");
            String sk = PropertiesTools.getInstance(file).getProperties("environment.me.sk");

            ObsConfiguration config = new ObsConfiguration();
            config.setSocketTimeout(30000);
            config.setConnectionTimeout(10000);
            config.setEndPoint(endPoint);
            config.setHttpProxy(
                    PropertiesTools.getInstance(file).getProperties("environment.me.proxyaddr"),
                    Integer.parseInt(PropertiesTools.getInstance(file).getProperties("environment.me.proxyport")),
                    PropertiesTools.getInstance(file).getProperties("environment.me.username"),
                    PropertiesTools.getInstance(file).getProperties("environment.me.password"));

            return new ObsClient(ak, sk, config);

        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取请求者付费的测试环境
     */
    public static ObsClient getInnerTempEnvironment() {
        try {
            String endPoint = PropertiesTools.getInstance(file).getProperties("environment.4.endpoint");
            String ak = PropertiesTools.getInstance(file).getProperties("environment.4.ak");
            String sk = PropertiesTools.getInstance(file).getProperties("environment.4.sk");

            ObsConfiguration config = new ObsConfiguration();
            config.setSocketTimeout(30000);
            config.setConnectionTimeout(10000);
            config.setEndPoint(endPoint);

            return new ObsClient(ak, sk, config);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void deleteObjects(ObsClient obsClient, String bucketName) {
        ListVersionsRequest request = new ListVersionsRequest(bucketName);
        request.setEncodingType("url");
        request.setMaxKeys(100);
        ListVersionsResult result;
        ListMultipartUploadsRequest request2 = new ListMultipartUploadsRequest();
        request2.setEncodingType("url");
        request2.setBucketName(bucketName);
        MultipartUploadListing listing = obsClient.listMultipartUploads(request2);
        AbortMultipartUploadRequest abortRequest = new AbortMultipartUploadRequest();
        abortRequest.setBucketName(bucketName);
        for (MultipartUpload upload : listing.getMultipartTaskList()) {
            abortRequest.setUploadId(upload.getUploadId());
            abortRequest.setObjectKey(upload.getObjectKey());
            obsClient.abortMultipartUpload(abortRequest);
        }
        do {
            result = obsClient.listVersions(request);
            if (result.getVersions().length == 0) {
                break;
            }
            // 文件桶使用批删会有问题，所以此处改成遍历单个删除，兼容对象桶和文件桶（但是一次列举最大为一千个，超过一千个在文件桶来说也会有问题）
            List<VersionOrDeleteMarker> versionOrDeleteMarkerLists = Arrays.asList(result.getVersions());
            Collections.reverse(versionOrDeleteMarkerLists);
            for (VersionOrDeleteMarker v : versionOrDeleteMarkerLists) {
                obsClient.deleteObject(bucketName, v.getKey());
            }

            request.setKeyMarker(result.getNextKeyMarker());
            request.setVersionIdMarker(result.getNextVersionIdMarker());
        } while (result.isTruncated());
    }

    public static void delete_buckets(ObsClient obsClient, List<String> bucketList) {
        for (String bucket : bucketList) {
            delete_bucket(obsClient, bucket);
        }
    }

    public static HeaderResponse delete_bucket(ObsClient obsClient, String bucketName) {
        System.out.println("Deleting " + bucketName);
        deleteObjects(obsClient, bucketName);
        return obsClient.deleteBucket(bucketName);
    }

    public static HeaderResponse createBucket(
            ObsClient obsClient, String bucketName, String location, boolean isPosix) {
        CreateBucketRequest request = new CreateBucketRequest();
        request.setBucketName(bucketName);
        request.setBucketType(BucketTypeEnum.OBJECT);
        request.setLocation(location);
        if (isPosix) {
            request.setBucketType(BucketTypeEnum.PFS);
        }
        return obsClient.createBucket(request);
    }

    public static void genTestFile(String filePath, int fileSizeKb) throws IOException {
        File testFile = new File(filePath);
        if (!testFile.exists()) {
            assertTrue(testFile.createNewFile());
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 128; i++) {
            stringBuilder.append("TestOBS!");
        }
        FileOutputStream outputStream = new FileOutputStream(filePath);
        for (int i = 0; i < fileSizeKb; i++) {
            outputStream.write(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
        }
        outputStream.close();
    }

    public static void initLog(StringWriter writer) {
        LoggerContext context = LoggerContext.getContext(false);
        Configuration config = context.getConfiguration();
        LoggerConfig LoggerConfig = config.getLoggerConfig("com.obs.services.internal.RestStorageService");
        if (!LoggerConfig.getName().equals("com.obs.services.internal.RestStorageService")) {
            LoggerConfig = new LoggerConfig("com.obs.services.internal.RestStorageService", Level.DEBUG, true);
        }
        config.addLogger("com.obs.services.internal.RestStorageService", LoggerConfig);

        PatternLayout layout = PatternLayout.createDefaultLayout(config);
        Appender appender = WriterAppender.createAppender(layout, null, writer, "StringWriter", false, true);
        config.addAppender(appender);
        LoggerConfig.addAppender(appender, Level.DEBUG, null);
        appender.start();
        context.updateLoggers();
        Configurator.setLevel("com.obs.services.internal.RestStorageService", Level.DEBUG);
    }
}
