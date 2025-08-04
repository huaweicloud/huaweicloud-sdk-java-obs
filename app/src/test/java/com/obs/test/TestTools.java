package com.obs.test;

import com.obs.services.Log4j2Configurator;
import com.obs.services.ObsClient;
import com.obs.services.ObsClientAsync;
import com.obs.services.ObsConfiguration;
import com.obs.services.crypto.CryptoObsClient;
import com.obs.services.crypto.CtrRSACipherGenerator;
import com.obs.services.exception.ObsException;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.AbortMultipartUploadRequest;
import com.obs.services.model.AuthTypeEnum;
import com.obs.services.model.BucketMetadataInfoRequest;
import com.obs.services.model.BucketTypeEnum;
import com.obs.services.model.CreateBucketRequest;
import com.obs.services.model.DownloadFileRequest;
import com.obs.services.model.DownloadFileResult;
import com.obs.services.model.HeaderResponse;
import com.obs.services.model.ListMultipartUploadsRequest;
import com.obs.services.model.ListVersionsRequest;
import com.obs.services.model.ListVersionsResult;
import com.obs.services.model.MultipartUpload;
import com.obs.services.model.MultipartUploadListing;
import com.obs.services.model.ProgressListener;
import com.obs.services.model.VersionOrDeleteMarker;
import com.obs.services.model.bpa.BucketPublicAccessBlock;
import com.obs.services.model.bpa.PutBucketPublicAccessBlockRequest;
import com.obs.test.tools.PropertiesTools;
import com.obs.test.tools.TokenGetter;
import okhttp3.Dns;
import okhttp3.EventListener;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.rules.TemporaryFolder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;

public class TestTools {
    private static final File file = new File("app/src/test/resource/test_data.properties");

    public static File getPropertiesFile() {
        return file;
    }

    static {
        // 获取当前路径
        String currentPath = System.getProperty("user.dir");
        // 打印当前路径
        System.out.println("user.dir,Current Path: " + currentPath);
        String log4j2XmlPath1 = currentPath + "/app/src/test/resource/log4j2.xml";
        String log4j2XmlPath2 = currentPath + "/log4j2.xml";
        String log4j2XmlPath3 = currentPath + "/app/src/main/resource/log4j2.xml";
        String log4j2XmlPath = null;
        if ((new File(log4j2XmlPath1)).exists()){
            log4j2XmlPath = log4j2XmlPath1;
        } else if ((new File(log4j2XmlPath2)).exists()){
            log4j2XmlPath = log4j2XmlPath2;
        } else {
            log4j2XmlPath = log4j2XmlPath3;
        }
        // 打印当前log4j2XmlPath
        System.out.println("log4j2XmlPath: " + log4j2XmlPath);
        Log4j2Configurator.setLogConfig(log4j2XmlPath);
    }

    public static String getEndpointWithNoPrefix() {
        try
        {
            String endPoint = PropertiesTools.getInstance(file).getProperties("environment.endpoint");
            endPoint = endPoint.replace("https://", "");
            endPoint = endPoint.replace("http://", "");
            return endPoint;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
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
     * 获取软连接内部环境
     */
    public static ObsClient getPipelineForSymEnvironment() {
        try {
            String endPoint = PropertiesTools.getInstance(file).getProperties("environment.sym.endpoint");
            String ak = PropertiesTools.getInstance(file).getProperties("environment.sym.ak");
            String sk = PropertiesTools.getInstance(file).getProperties("environment.sym.sk");
            String authType = PropertiesTools.getInstance(file).getProperties("environment.sym.authType");
            ObsConfiguration config = new ObsConfiguration();
            config.setSocketTimeout(30000);
            config.setConnectionTimeout(10000);
            config.setEndPoint(endPoint);
            config.setAuthTypeNegotiation(false);
            config.setCustomizedDnsImpl(getCustomPipelineDnsResolver());
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
            config.setCustomizedDnsImpl(getCustomPipelineDnsResolver());
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
    public static ObsClient getPipelineCRREnvironment() {
        try {
            String endPoint = PropertiesTools.getInstance(file).getProperties("environment.crr.endpoint");
            String ak = PropertiesTools.getInstance(file).getProperties("environment.crr.ak");
            String sk = PropertiesTools.getInstance(file).getProperties("environment.crr.sk");
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

    public static ObsClient getPipelineEnvironmentInSecure() {
        try {
            String endPoint = PropertiesTools.getInstance(file).getProperties("environment.endpoint");
            String ak = PropertiesTools.getInstance(file).getProperties("environment.ak");
            String sk = PropertiesTools.getInstance(file).getProperties("environment.sk");
            String authType = PropertiesTools.getInstance(file).getProperties("environment.authType");
            ObsConfiguration config = new ObsConfiguration();
            config.setHttpsOnly(false);
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

    /**
     * 获取内部环境, 固定为obs协议
     */
    public static ObsClient getPipelineEnvironment_OBS() {
        try {
            String endPoint = PropertiesTools.getInstance(file).getProperties("environment.endpoint");
            String ak = PropertiesTools.getInstance(file).getProperties("environment.ak");
            String sk = PropertiesTools.getInstance(file).getProperties("environment.sk");
            ObsConfiguration config = new ObsConfiguration();
            config.setSocketTimeout(30000);
            config.setConnectionTimeout(10000);
            config.setEndPoint(endPoint);
            config.setAuthTypeNegotiation(false);
            config.setAuthType(AuthTypeEnum.OBS);
            config.setCustomizedDnsImpl(getCustomPipelineDnsResolver());
            return new ObsClient(ak, sk, config);
        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取内部环境, 固定为v2协议
     */
    public static ObsClient getPipelineEnvironment_V2() {
        try {
            String endPoint = PropertiesTools.getInstance(file).getProperties("environment.endpoint");
            String ak = PropertiesTools.getInstance(file).getProperties("environment.ak");
            String sk = PropertiesTools.getInstance(file).getProperties("environment.sk");
            ObsConfiguration config = new ObsConfiguration();
            config.setSocketTimeout(30000);
            config.setConnectionTimeout(10000);
            config.setEndPoint(endPoint);
            config.setAuthTypeNegotiation(false);
            config.setAuthType(AuthTypeEnum.V2);
            config.setCustomizedDnsImpl(getCustomPipelineDnsResolver());
            return new ObsClient(ak, sk, config);
        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取内部环境, 固定为v4协议
     */
    public static ObsClient getPipelineEnvironment_V4() {
        try {
            String endPoint = PropertiesTools.getInstance(file).getProperties("environment.endpoint");
            String ak = PropertiesTools.getInstance(file).getProperties("environment.ak");
            String sk = PropertiesTools.getInstance(file).getProperties("environment.sk");
            ObsConfiguration config = new ObsConfiguration();
            config.setSocketTimeout(30000);
            config.setConnectionTimeout(10000);
            config.setEndPoint(endPoint);
            config.setAuthTypeNegotiation(false);
            config.setAuthType(AuthTypeEnum.V4);
            config.setCustomizedDnsImpl(getCustomPipelineDnsResolver());
            return new ObsClient(ak, sk, config);
        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取内部环境
     */
    public static ObsClientAsync getPipelineEnvironmentForAsyncClient() {
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
            config.setCustomizedDnsImpl(getCustomPipelineDnsResolver());
            if (authType.equals("v2")) {
                config.setAuthType(AuthTypeEnum.V2);
            } else {
                config.setAuthType(AuthTypeEnum.OBS);
            }
            return new ObsClientAsync(ak, sk, config);

        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取环境,可配置
     */
    public static ObsClient getPipelineEnvironmentClientWithConfig(ObsConfiguration config) {
        try {
            String endPoint = PropertiesTools.getInstance(file).getProperties("environment.endpoint");
            String ak = PropertiesTools.getInstance(file).getProperties("environment.ak");
            String sk = PropertiesTools.getInstance(file).getProperties("environment.sk");
            String authType = PropertiesTools.getInstance(file).getProperties("environment.authType");
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
    /**
     * 获取内部环境
     */
    public static class TestPipelineAkSk {
        public String endPoint;
        public String ak;
        public String sk;
        public String securityToken;
        public String authType;
    }
    public static TestPipelineAkSk getPipelineAkSk() {
        try {
            TestPipelineAkSk testPipelineAkSk = new TestPipelineAkSk();
            testPipelineAkSk.endPoint = PropertiesTools.getInstance(file).getProperties("environment.endpoint");
            testPipelineAkSk.ak = PropertiesTools.getInstance(file).getProperties("environment.ak");
            testPipelineAkSk.sk = PropertiesTools.getInstance(file).getProperties("environment.sk");
            testPipelineAkSk.authType = PropertiesTools.getInstance(file).getProperties("environment.authType");
            return testPipelineAkSk;
        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    public static TestPipelineAkSk getPipelineAkSkToken() {
        try {
            String iamEndPoint = PropertiesTools.getInstance(file).getProperties("iamEndPoint_CN");
            String iamUserName = PropertiesTools.getInstance(file).getProperties("iamUserName_CN");
            String iamUserPassWord = PropertiesTools.getInstance(file).getProperties("iamUserPassWord_CN");
            String HuaweiAccountName = PropertiesTools.getInstance(file).getProperties("HuaweiAccountName_CN");
            String TokenDurationSecondString =
                PropertiesTools.getInstance(file).getProperties("TokenDurationSecondString_CN");
            long TokenDurationSeconds =
                TokenDurationSecondString == null ? 900 : Integer.parseInt(TokenDurationSecondString);
            TokenGetter.initCredentials(iamEndPoint,iamUserName,iamUserPassWord,HuaweiAccountName,TokenDurationSeconds);
            TokenGetter.getToken();
            String tokenContent = TokenGetter.getSecurityToken();
            TestPipelineAkSk testPipelineAkSk = new TestPipelineAkSk();
            testPipelineAkSk.endPoint = PropertiesTools.getInstance(file).getProperties("environment.endpoint");
            testPipelineAkSk.ak = TokenGetter.getAK(tokenContent);
            testPipelineAkSk.sk = TokenGetter.getSK(tokenContent);
            testPipelineAkSk.securityToken = TokenGetter.getStsToken(tokenContent);
            testPipelineAkSk.authType = PropertiesTools.getInstance(file).getProperties("environment.authType");
            return testPipelineAkSk;
        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ObsClient getWrongPipelineEnvironment() {
        try {
            String endPoint = PropertiesTools.getInstance(file).getProperties("environment.endpoint");
            String ak = PropertiesTools.getInstance(file).getProperties("environment.ak");
            String sk = "randomWrongSk"; // randomWrongSk
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

    public static ObsClient getPipelineEnvironmentWithHostnameVerifier(HostnameVerifier hostnameVerifier) {
        try {
            String endPoint = PropertiesTools.getInstance(file).getProperties("environment.endpoint");
            String ak = PropertiesTools.getInstance(file).getProperties("environment.ak");
            String sk = PropertiesTools.getInstance(file).getProperties("environment.sk");
            String authType = PropertiesTools.getInstance(file).getProperties("environment.authType");
            ObsConfiguration config = new ObsConfiguration();
            config.setSocketTimeout(30000);
            config.setConnectionTimeout(10000);
            config.setEndPoint(endPoint);
            config.setIsStrictHostnameVerification(true);
            config.setHostnameVerifier(hostnameVerifier);
            config.setCustomizedDnsImpl(getCustomPipelineDnsResolver());
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

    public static ObsClient getPipelineEnvironmentWithEventListenerFactory(EventListener.Factory eventListenerFactory) {
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
            config.setEventListenerFactory(eventListenerFactory);
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

    public static ObsClient getPipelineEnvironmentWithCustomisedSSLContext(SSLContext sslContext) {
        return getPipelineEnvironmentWithCustomisedSSLContext(sslContext, null);
    }

    public static ObsClient getPipelineEnvironmentWithCustomisedSSLContext(SSLContext sslContext, String sslProvider) {
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
            config.setSslContext(sslContext);
            if (sslProvider != null) {
                config.setSslProvider(sslProvider);
            }
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

    public static ObsClient getPipelineEnvironmentWithReadBufferSize(int bufferSize) {
        try {
            String endPoint = PropertiesTools.getInstance(file).getProperties("environment.endpoint");
            String ak = PropertiesTools.getInstance(file).getProperties("environment.ak");
            String sk = PropertiesTools.getInstance(file).getProperties("environment.sk");
            String authType = PropertiesTools.getInstance(file).getProperties("environment.authType");
            ObsConfiguration config = new ObsConfiguration();
            config.setSocketTimeout(30000);
            config.setConnectionTimeout(10000);
            config.setEndPoint(endPoint);
            config.setReadBufferSize(bufferSize);
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
     * 获取s3协议的client
     */
    public static ObsClient getS3Environment() {
        try {
            String endPoint = PropertiesTools.getInstance(file).getProperties("environment.1.endpoint");
            String ak = PropertiesTools.getInstance(file).getProperties("environment.1.ak");
            String sk = PropertiesTools.getInstance(file).getProperties("environment.1.sk");

            ObsConfiguration config = new ObsConfiguration();
            config.setSocketTimeout(30000);
            config.setConnectionTimeout(10000);
            config.setEndPoint(endPoint);
            config.setAuthTypeNegotiation(false);
            config.setAuthType(AuthTypeEnum.V2);

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
            String proxyAddress = PropertiesTools.getInstance(file).getProperties("environment.me.proxyaddr");
            String proxyPort = PropertiesTools.getInstance(file).getProperties("environment.me.proxyport");
            String username = PropertiesTools.getInstance(file).getProperties("environment.me.username");
            String password = PropertiesTools.getInstance(file).getProperties("environment.me.password");

            if(proxyAddress!=null&&!proxyAddress.equals("")){
                config.setHttpProxy(
                        proxyAddress,
                        Integer.parseInt(proxyPort),
                        username,
                        password);
            }

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
                obsClient.deleteObject(bucketName, v.getKey(),
                    BucketTypeEnum.PFS.equals(
                        obsClient.getBucketMetadata(new BucketMetadataInfoRequest(bucketName))
                            .getBucketType()) ? null : v.getVersionId());
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

    public static void removeTestFile(String filePath){
        new File(filePath).delete();
    }

    public static File genTestFile(TemporaryFolder temporaryFolder, String testFileName, long fileSizeInBytes) throws IOException {
        File testFile = temporaryFolder.newFile(testFileName);
        SecureRandom secureRandom = getPipeLineTestSecureRandom();
        long bufferSize = 65536;
        long sizeToWrite;
        byte [] buffer = new byte[(int)bufferSize];
        AtomicBoolean needRandomBytes = new AtomicBoolean(true);
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(testFile, "rw")) {
            Thread getRandomBytesThread = new Thread(() -> {
                while (needRandomBytes.get()) {
                    secureRandom.nextBytes(buffer);
                }
            });
            getRandomBytesThread.start();
            while (0 < fileSizeInBytes) {
                sizeToWrite = Long.min(fileSizeInBytes, bufferSize);
                randomAccessFile.write(buffer, 0, (int)sizeToWrite);
                fileSizeInBytes -= sizeToWrite;
            }
            needRandomBytes.set(false);
        }
        return testFile;
    }

    public static void initLog(StringWriter writer) {
        LoggerContext context = LoggerContext.getContext(false);
        Configuration config = context.getConfiguration();
        String loggerNameObs1 = "com.obs.services.internal.RestStorageService";
        LoggerConfig LoggerConfig = config.getLoggerConfig(loggerNameObs1);
        LoggerConfig LoggerConfig_OBS = null;
        String loggerNameObs2 = "com.obs.services.AbstractClient";
        LoggerConfig LoggerConfig2 = config.getLoggerConfig(loggerNameObs2);
        LoggerConfig LoggerConfig_OBS2 = null;
        if (!LoggerConfig.getName().equals(loggerNameObs1)) {
            LoggerConfig.setLevel(Level.DEBUG);
            LoggerConfig_OBS = new LoggerConfig(loggerNameObs1, Level.DEBUG, true);
            config.addLogger(loggerNameObs1, LoggerConfig_OBS);
        }
        if (!LoggerConfig2.getName().equals(loggerNameObs2)) {
            LoggerConfig2.setLevel(Level.DEBUG);
            LoggerConfig_OBS2 = new LoggerConfig(loggerNameObs2, Level.DEBUG, true);
            config.addLogger(loggerNameObs2, LoggerConfig_OBS2);
        }

        PatternLayout layout = PatternLayout.createDefaultLayout(config);
        String testAppenderName = "StringWriter";
        Appender appender = WriterAppender.createAppender(layout, null, writer, testAppenderName, false, true);
        config.addAppender(appender);
        LoggerConfig.removeAppender(testAppenderName);
        LoggerConfig.addAppender(appender, Level.DEBUG, null);
        LoggerConfig2.removeAppender(testAppenderName);
        LoggerConfig2.addAppender(appender, Level.DEBUG, null);
        if (LoggerConfig_OBS != null) {
            LoggerConfig_OBS.removeAppender(testAppenderName);
            LoggerConfig_OBS.addAppender(appender, Level.DEBUG, null);
        }
        if (LoggerConfig_OBS2 != null) {
            LoggerConfig_OBS2.removeAppender(testAppenderName);
            LoggerConfig_OBS2.addAppender(appender, Level.DEBUG, null);
        }
        appender.start();
        context.updateLoggers();
        Configurator.setLevel(loggerNameObs1, Level.DEBUG);
        Configurator.setLevel(loggerNameObs2, Level.DEBUG);
    }
    public static void printObsException(ObsException e) {
        System.out.println("HTTP Code: " + e.getResponseCode());
        System.out.println("Error Code:" + e.getErrorCode());
        System.out.println("Error Message: " + e.getErrorMessage());
        System.out.println("Request ID:" + e.getErrorRequestId());
        System.out.println("Host ID:" + e.getErrorHostId());
        Map<String, String> headers = e.getResponseHeaders();// 遍历Map的entry,打印所有报错相关头域
        if(headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                System.out.println(header.getKey()+":"+header.getValue());
            }
        }
        printException(e);
    }
    public static void printException(Throwable e) {
        try (StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter)) {
            e.printStackTrace(printWriter);
            System.out.println(stringWriter);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    static ObsConfiguration obsConfiguration = new ObsConfiguration();
    public static SecureRandom getPipeLineTestSecureRandom() {
        return obsConfiguration.getSecureRandom();
    }
    public static int getTestRandomIntInRange(int start ,int end) {
        if (start>= end) {
            throw new IllegalArgumentException("start is not less than end");
        } else {
            return getPipeLineTestSecureRandom().nextInt(end - start) + start;
        }
    }

    public static DownloadFileResult downloadFileWithRetry(ObsClient obsClient, String bucketName, String objectKey,
            File localFile, long partSize, boolean enabledCheckpoint, boolean needCRC64,
            ProgressListener progressListener, long progressInterval) throws ObsException {
        DownloadFileRequest downloadFileRequest = new DownloadFileRequest(
                bucketName,
                objectKey,
                localFile.getPath(),
                partSize,
                32,
                enabledCheckpoint);
        downloadFileRequest.setNeedCalculateCRC64(needCRC64);
        downloadFileRequest.setProgressListener(progressListener);
        downloadFileRequest.setProgressInterval(progressInterval);
        int retry = 5;
        DownloadFileResult downloadFileResult = null;
        ObsException lastException = null;
        while (retry-- > 0) {
            try {
                lastException = null;
                downloadFileResult = obsClient.downloadFile(downloadFileRequest);
                break;
            } catch (ObsException ignore) {
                lastException = ignore;
                System.out.println("downloadFileResult remain retry time:" + retry);
            }
        }
        if (lastException != null) {
            throw lastException;
        }
        return downloadFileResult;

    }
    public static DownloadFileResult downloadFileWithRetry(ObsClient obsClient, String bucketName, String objectKey,
            File localFile, long partSize, boolean enabledCheckpoint) throws ObsException {
        return downloadFileWithRetry(
                obsClient,
                bucketName,
                objectKey,
                localFile,
                partSize,
                enabledCheckpoint,
                false, null, 0L);
    }
    public static DownloadFileResult downloadFileWithRetry(ObsClient obsClient, String bucketName, String objectKey,
            File localFile, long partSize) throws ObsException {
        return downloadFileWithRetry(
                obsClient,
                bucketName,
                objectKey,
                localFile,
                partSize,
                true,
                false, null, 0L);
    }

    public static void setBucketPublicAccess(ObsClient obsClient, String bucketName) {
        BucketPublicAccessBlock bucketPublicAccessBlock = new BucketPublicAccessBlock();
        bucketPublicAccessBlock.setBlockPublicACLs(false);
        bucketPublicAccessBlock.setBlockPublicPolicy(false);
        PutBucketPublicAccessBlockRequest putBucketPublicAccessBlockRequest =
            new PutBucketPublicAccessBlockRequest(bucketName, bucketPublicAccessBlock);
        obsClient.putBucketPublicAccessBlock(putBucketPublicAccessBlockRequest);
    }

    public static String computeMd5Etag(File file) throws IOException, NoSuchAlgorithmException {
        String ETag;
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            ETag = "\"" + ServiceUtils.toHex(ServiceUtils.computeMD5Hash(fileInputStream)) + "\"";
        }
        return ETag;
    }

    public static String computeMd5Base64(File file) throws IOException, NoSuchAlgorithmException {
        String Md5Base64;
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            Md5Base64 = ServiceUtils.toBase64(ServiceUtils.computeMD5Hash(fileInputStream));
        }
        return Md5Base64;
    }

    public static boolean areByteArraysEqual(byte[] array1, byte[] array2) {
        if (array1.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array1.length; i++) {
            if (array1[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取自定义流水线dns解析记录
     */
    public static Dns getCustomPipelineDnsResolver() {
        try {

            String endPoint = PropertiesTools.getInstance(file).getProperties("environment.endpoint");
            String ip = PropertiesTools.getInstance(file).getProperties("environment.endpoint.ip");
            if (!ServiceUtils.isValid2(ip)) {
                ip = System.getenv("environment_endpoint_ip");
            }
            return new CustomizedPipelineDns(endPoint, ip);

        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static class CustomizedPipelineDns implements Dns {

        private String endPoint;
        private static final String HTTPS_PREFIX = "https://";
        private static final String HTTP_PREFIX = "http://";
        private final List<InetAddress> inetAddresses;
        private void removeEndPointPrefix(){
            if (Objects.nonNull(endPoint)) {
                if (endPoint.startsWith(HTTPS_PREFIX)) {
                    endPoint = endPoint.substring(HTTPS_PREFIX.length());
                } else if (endPoint.startsWith(HTTP_PREFIX)) {
                    endPoint = endPoint.substring(HTTP_PREFIX.length());
                }
            }
        }
        public CustomizedPipelineDns(String endPoint, String ip) {
            this.endPoint = endPoint;
            removeEndPointPrefix();
            this.inetAddresses = new ArrayList<>(1);
            if (ServiceUtils.isValid2(ip)) {
                try {
                    // 将endpoint和ip转换为InetAddress
                    inetAddresses.add(InetAddress.getByName(ip));
                } catch (UnknownHostException e) {
                    System.out.println("CustomizedPipelineDns parse ip (" + ip + ") failed");
                    // 处理解析失败的情况
                    e.printStackTrace();
                }
            }
        }
        /**
         * @param hostname
         * @return
         * @throws UnknownHostException
         */
        @Override
        public List<InetAddress> lookup(String hostname) throws UnknownHostException {
            if (hostname.contains(endPoint)) {
                if (!inetAddresses.isEmpty()) {
                    return inetAddresses;
                }
            }
            return Dns.SYSTEM.lookup(hostname);
        }

    }
}
