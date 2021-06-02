/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.obs.services;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import com.obs.services.internal.ObsConstraint;
import com.obs.services.model.AuthTypeEnum;
import com.obs.services.model.HttpProtocolTypeEnum;

import okhttp3.Dispatcher;

/**
 * OBS客户端配置参数
 */
public class ObsConfiguration implements Cloneable {

    private int connectionTimeout;

    private int idleConnectionTime;

    private int maxIdleConnections;

    private int maxConnections;

    private int maxErrorRetry;

    private int socketTimeout;

    private String endPoint;

    private int endpointHttpPort;

    private int endpointHttpsPort;

    private boolean httpsOnly;

    private boolean pathStyle;

    private HttpProxyConfiguration httpProxy;

    private int uploadStreamRetryBufferSize;

    private boolean validateCertificate;

    private boolean verifyResponseContentType;

    private int readBufferSize;

    private int writeBufferSize;

    private KeyManagerFactory keyManagerFactory;

    private TrustManagerFactory trustManagerFactory;

    private boolean isStrictHostnameVerification;

    private AuthTypeEnum authType;

    private String signatString;
    private String defaultBucketLocation;
    private int bufferSize;
    private int socketWriteBufferSize;
    private int socketReadBufferSize;
    private boolean isNio;
    private boolean useReaper;
    private boolean keepAlive;
    private int connectionRequestTimeout;
    private boolean authTypeNegotiation;

    private boolean cname;

    private String delimiter;

    private String sslProvider;

    private HttpProtocolTypeEnum httpProtocolType;

    private Dispatcher httpDispatcher;

    /**
     * 构造函数
     */
    public ObsConfiguration() {
        this.connectionTimeout = ObsConstraint.HTTP_CONNECT_TIMEOUT_VALUE;
        this.maxConnections = ObsConstraint.HTTP_MAX_CONNECT_VALUE;
        this.maxErrorRetry = ObsConstraint.HTTP_RETRY_MAX_VALUE;
        this.socketTimeout = ObsConstraint.HTTP_SOCKET_TIMEOUT_VALUE;
        this.endpointHttpPort = ObsConstraint.HTTP_PORT_VALUE;
        this.endpointHttpsPort = ObsConstraint.HTTPS_PORT_VALUE;
        this.httpsOnly = true;
        this.endPoint = "";
        this.pathStyle = false;
        this.validateCertificate = false;
        this.verifyResponseContentType = true;
        this.isStrictHostnameVerification = false;
        this.uploadStreamRetryBufferSize = -1;
        this.socketWriteBufferSize = -1;
        this.socketReadBufferSize = -1;
        this.readBufferSize = -1;
        this.writeBufferSize = -1;
        this.idleConnectionTime = ObsConstraint.DEFAULT_IDLE_CONNECTION_TIME;
        this.maxIdleConnections = ObsConstraint.DEFAULT_MAX_IDLE_CONNECTIONS;
        this.authType = AuthTypeEnum.OBS;
        this.keepAlive = true;
        this.signatString = "";
        this.defaultBucketLocation = "";
        this.authTypeNegotiation = true;
        this.cname = false;
        this.delimiter = "/";
        this.httpProtocolType = HttpProtocolTypeEnum.HTTP1_1;
    }

    public String getDelimiter() {
        return delimiter;
    }

    /**
     * 设置文件夹分隔符
     *
     * @param delimiter
     *            文件夹分隔符
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    @Deprecated
    public String getSignatString() {
        return signatString;
    }

    @Deprecated
    public void setSignatString(String signatString) {
        this.signatString = signatString;
    }

    @Deprecated
    public String getDefaultBucketLocation() {
        return defaultBucketLocation;
    }

    @Deprecated
    public void setDefaultBucketLocation(String defaultBucketLocation) {
        this.defaultBucketLocation = defaultBucketLocation;
    }

    @Deprecated
    public int getBufferSize() {
        return bufferSize;
    }

    @Deprecated
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    /**
     * 判断是否使用路径访问方式访问OBS服务，true使用路径访问方式，false使用虚拟主机访问方式，默认值：false
     * 注意：如果设置了路径方式，无法使用OBS 3.0版本桶的新特性
     *
     * @return 是否使用路径访问方式访问OBS服务
     */
    public boolean isDisableDnsBucket() {
        return this.isPathStyle();
    }

    /**
     * 设置是否使用路径访问方式访问OBS服务，true使用路径访问方式，false使用虚拟主机访问方式，默认值：false
     * 注意：如果设置了路径方式，无法使用OBS 3.0版本桶的新特性
     *
     * @param disableDns
     *            是否使用路径访问方式访问OBS服务
     */
    public void setDisableDnsBucket(boolean disableDns) {
        this.setPathStyle(disableDns);
    }

    /**
     * 获取socket接收缓冲区大小，单位：字节，对应java.net.SocketOptions.SO_SNDBUF参数，默认值：-1，表示不设置
     *
     * @return socket接收缓冲区大小
     */
    public int getSocketReadBufferSize() {
        return socketReadBufferSize;
    }

    /**
     * 设置socket接收缓冲区大小，单位：字节，对应java.net.SocketOptions.SO_SNDBUF参数，默认值：-1，表示不设置
     *
     * @param socketReadBufferSize
     *            socket接收缓冲区大小
     */
    public void setSocketReadBufferSize(int socketReadBufferSize) {
        this.socketReadBufferSize = socketReadBufferSize;
    }

    /**
     * 获取socket发送缓冲区大小，单位：字节，对应java.net.SocketOptions.SO_RCVBUF参数，默认值：-1，表示不设置
     *
     * @return socket发送缓冲区大小
     */
    public int getSocketWriteBufferSize() {
        return socketWriteBufferSize;
    }

    /**
     * 设置socket发送缓冲区大小，单位：字节，对应java.net.SocketOptions.SO_RCVBUF参数，默认值：-1，表示不设置
     *
     * @param socketWriteBufferSize
     *            socket发送缓冲区大小
     */
    public void setSocketWriteBufferSize(int socketWriteBufferSize) {
        this.socketWriteBufferSize = socketWriteBufferSize;
    }

    @Deprecated
    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    @Deprecated
    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    @Deprecated
    public void disableNio() {
        this.isNio = false;
    }

    @Deprecated
    public void enableNio() {
        this.isNio = true;
    }

    @Deprecated
    public boolean isNio() {
        return this.isNio;
    }

    @Deprecated
    public boolean isUseReaper() {
        return useReaper;
    }

    @Deprecated
    public void setUseReaper(boolean useReaper) {
        this.useReaper = useReaper;
    }

    /**
     * 获取生成KeyManager数组的工厂
     *
     * @return 生成KeyManager数组的工厂
     */
    public KeyManagerFactory getKeyManagerFactory() {
        return keyManagerFactory;
    }

    /**
     * 设置生成KeyManager数组的工厂
     *
     * @param keyManagerFactory
     *            生成KeyManager数组的工厂
     */
    public void setKeyManagerFactory(KeyManagerFactory keyManagerFactory) {
        this.keyManagerFactory = keyManagerFactory;
    }

    /**
     * 获取生成TrustManager数组的工厂
     *
     * @return 生成TrustManager数组的工厂
     */
    public TrustManagerFactory getTrustManagerFactory() {
        return trustManagerFactory;
    }

    /**
     * 设置生成TrustManager数组的工厂
     *
     * @param trustManagerFactory
     *            生成TrustManager数组的工厂
     */
    public void setTrustManagerFactory(TrustManagerFactory trustManagerFactory) {
        this.trustManagerFactory = trustManagerFactory;
    }

    /**
     * 获取是否验证域名标识，默认值：false
     *
     * @return 是否验证域名标识
     */
    public boolean isStrictHostnameVerification() {
        return isStrictHostnameVerification;
    }

    /**
     * 设置是否验证域名标识
     *
     * @param isStrictHostnameVerification
     *            是否验证域名标识
     */
    public void setIsStrictHostnameVerification(boolean isStrictHostnameVerification) {
        this.isStrictHostnameVerification = isStrictHostnameVerification;
    }

    /**
     * 判断是否使用路径访问方式访问OBS服务，true使用路径访问方式，false使用虚拟主机访问方式，默认值：false
     * 注意：如果设置了路径方式，无法使用OBS 3.0版本桶的新特性
     *
     * @return 是否使用路径访问方式访问OBS服务
     */
    public boolean isPathStyle() {
        return pathStyle;
    }

    /**
     * 设置是否使用路径访问方式访问OBS服务，true使用路径访问方式，false使用虚拟主机访问方式，默认值：false
     * 注意：如果设置了路径方式，无法使用OBS 3.0版本桶的新特性
     *
     * @param pathStyle
     *            是否使用路径访问方式访问OBS服务
     */
    public void setPathStyle(boolean pathStyle) {
        this.pathStyle = pathStyle;
    }

    /**
     * 获取建立HTTP/HTTPS连接的超时时间，单位：毫秒，默认值：60000
     *
     * @return 建立HTTP/HTTPS连接的超时时间
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * 设置建立HTTP/HTTPS连接的超时时间，单位：毫秒，默认值：60000
     *
     * @param connectionTimeout
     *            建立HTTP/HTTPS连接的超时时间
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * 获取最大允许的HTTP并发请求数，默认值：1000
     *
     * @return 最大允许的HTTP并发请求数
     */
    public int getMaxConnections() {
        return maxConnections;
    }

    /**
     * 设置最大允许的HTTP并发请求数，默认值：1000
     *
     * @param maxConnections
     *            最大允许的HTTP并发请求数
     */
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    /**
     * 获取请求失败(请求异常、服务端报500或503错误)后最大的重试次数，默认值：3
     *
     * @return 请求失败后最大的重试次数
     */
    public int getMaxErrorRetry() {
        return maxErrorRetry;
    }

    /**
     * 设置请求失败(请求异常、服务端报500或503错误)后最大的重试次数，默认值：3
     *
     * @param maxErrorRetry
     *            请求失败后最大的重试次数
     */
    public void setMaxErrorRetry(int maxErrorRetry) {
        this.maxErrorRetry = maxErrorRetry;
    }

    /**
     * 获取socket层传输数据的超时时间，单位：毫秒，默认值：60000
     *
     * @return socket层传输数据的超时时间
     */
    public int getSocketTimeout() {
        return socketTimeout;
    }

    /**
     * 设置socket层传输数据的超时时间，单位：毫秒，默认值：60000
     *
     * @param socketTimeout
     *            socket层传输数据的超时时间
     */
    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    /**
     * 获取连接OBS的服务地址。
     *
     * @return 连接OBS的服务地址
     */
    public String getEndPoint() {
        if (endPoint == null || endPoint.trim().equals("")) {
            throw new IllegalArgumentException("EndPoint is not set");
        }
        return endPoint.trim();
    }

    /**
     * 设置连接OBS的服务地址。
     *
     * @param endPoint
     *            连接OBS的服务地址
     */
    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    /**
     * 获取HTTP请求的端口号，默认值：80
     *
     * @return HTTP请求的端口号
     */
    public int getEndpointHttpPort() {
        return endpointHttpPort;
    }

    /**
     * 设置HTTP请求的端口号，默认值：80
     *
     * @param endpointHttpPort
     *            HTTP请求的端口号
     */
    public void setEndpointHttpPort(int endpointHttpPort) {
        this.endpointHttpPort = endpointHttpPort;
    }

    /**
     * 获取HTTPS请求的端口号，默认值：443
     *
     * @return HTTPS请求的端口号
     */
    public int getEndpointHttpsPort() {
        return endpointHttpsPort;
    }

    /**
     * 设置HTTPS请求的端口号，默认值：443
     *
     * @param endpointHttpsPort
     *            HTTPS请求的端口号
     */
    public void setEndpointHttpsPort(int endpointHttpsPort) {
        this.endpointHttpsPort = endpointHttpsPort;
    }

    /**
     * 设置是否使用HTTPS连接OBS服务，默认值：true
     *
     * @param httpsOnly
     *            是否使用HTTPS连接OBS服务标识
     */
    public void setHttpsOnly(boolean httpsOnly) {
        this.httpsOnly = httpsOnly;
    }

    /**
     * 获取是否使用HTTPS连接OBS服务，默认值：true
     *
     * @return 是否使用HTTPS连接OBS服务标识
     */
    public boolean isHttpsOnly() {
        return httpsOnly;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * 获取代理配置信息
     *
     * @return 代理配置信息
     */
    public HttpProxyConfiguration getHttpProxy() {
        return httpProxy;
    }

    /**
     * 设置代理配置信息
     *
     * @param httpProxy
     *            代理配置信息
     */
    public void setHttpProxy(HttpProxyConfiguration httpProxy) {
        this.httpProxy = httpProxy;
    }

    /**
     * 设置代理服务器配置信息
     *
     * @param proxyAddr
     *            代理服务器地址
     * @param proxyPort
     *            代理服务器端口
     * @param userName
     *            代理用户名
     * @param password
     *            代理密码
     * @param domain
     *            代理域
     */
    @Deprecated
    public void setHttpProxy(String proxyAddr, int proxyPort, String userName, String password, String domain) {
        this.httpProxy = new HttpProxyConfiguration(proxyAddr, proxyPort, userName, password, domain);
    }

    /**
     * 设置代理服务器配置信息
     *
     * @param proxyAddr
     *            代理服务器地址
     * @param proxyPort
     *            代理服务器端口
     * @param userName
     *            代理用户名
     * @param password
     *            代理密码
     */
    public void setHttpProxy(String proxyAddr, int proxyPort, String userName, String password) {
        this.httpProxy = new HttpProxyConfiguration(proxyAddr, proxyPort, userName, password, null);
    }

    /**
     * 设置上传流对象时使用的缓存大小，单位：字节，默认值：512KB
     *
     * @param uploadStreamRetryBufferSize
     *            上传流对象时使用的缓存大小
     */
    @Deprecated
    public void setUploadStreamRetryBufferSize(int uploadStreamRetryBufferSize) {
        this.uploadStreamRetryBufferSize = uploadStreamRetryBufferSize;
    }

    /**
     * 获取上传流对象时使用的缓存大小，单位：字节，默认值：512KB
     *
     * @return 上传流对象时使用的缓存大小
     */
    @Deprecated
    public int getUploadStreamRetryBufferSize() {
        return this.uploadStreamRetryBufferSize;
    }

    /**
     * 获取是否验证服务端证书标识，默认值：false
     *
     * @return 是否验证服务端证书标识
     */
    public boolean isValidateCertificate() {
        return validateCertificate;
    }

    /**
     * 设置是否验证服务端证书标识，默认值：false
     *
     * @param validateCertificate
     *            是否验证服务端证书标识
     */
    public void setValidateCertificate(boolean validateCertificate) {
        this.validateCertificate = validateCertificate;
    }

    /**
     * 获取是否验证响应头信息的ContentType，默认值：true
     *
     * @return 是否验证响应头信息的ContentType标识
     */
    public boolean isVerifyResponseContentType() {
        return verifyResponseContentType;
    }

    /**
     * 设置是否验证响应头信息的ContentType，默认值：true
     *
     * @param verifyResponseContentType
     *            是否验证响应头信息的ContentType标识
     */
    public void setVerifyResponseContentType(boolean verifyResponseContentType) {
        this.verifyResponseContentType = verifyResponseContentType;
    }

    /**
     * 获取从Socket流下载对象的缓存大小，-1表示不设置缓存，单位：字节，默认值：-1
     *
     * @return 从Socket流下载对象的缓存大小
     */
    public int getReadBufferSize() {
        return readBufferSize;
    }

    /**
     * 设置从Socket流下载对象的缓存大小，-1表示不设置缓存，单位：字节，默认值：-1
     *
     * @param readBufferSize
     *            从Socket流下载对象的缓存大小
     */
    public void setReadBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
    }

    /**
     * 获取上传对象到Socket流时的缓存大小，-1表示不设置缓存，单位：字节，默认为-1
     *
     * @return 上传对象到Socket流时的缓存大小
     */
    public int getWriteBufferSize() {
        return writeBufferSize;
    }

    /**
     * 设置上传对象到Socket流时的缓存大小，-1表示不设置缓存，单位：字节，默认为-1
     *
     * @param writeBufferSize
     *            上传对象到Socket流时的缓存大小
     */
    public void setWriteBufferSize(int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
    }

    /**
     * 获取连接池中连接的最大空闲时间，单位：毫秒，默认值：30000
     *
     * @return 连接池中连接的最大空闲时间
     */
    public int getIdleConnectionTime() {
        return idleConnectionTime;
    }

    /**
     * 设置连接池中连接的最大空闲时间，单位：毫秒，默认值：30000
     *
     * @param idleConnectionTime
     *            连接池中连接的最大空闲时间
     */
    public void setIdleConnectionTime(int idleConnectionTime) {
        this.idleConnectionTime = idleConnectionTime;
    }

    /**
     * 获取连接池中最大空闲连接数，默认值：1000
     *
     * @return 连接池中最大空闲连接数
     */
    public int getMaxIdleConnections() {
        return maxIdleConnections;
    }

    /**
     * 设置连接池中最大空闲连接数，默认值：1000
     *
     * @param maxIdleConnections
     *            连接池中最大空闲连接数
     */
    public void setMaxIdleConnections(int maxIdleConnections) {
        this.maxIdleConnections = maxIdleConnections;
    }

    /**
     * 获取鉴权类型
     *
     * @return 鉴权类型
     */
    public AuthTypeEnum getAuthType() {
        return authType;
    }

    /**
     * 设置鉴权类型
     *
     * @param authType
     *            鉴权类型
     */
    public void setAuthType(AuthTypeEnum authType) {
        this.authType = authType;
    }

    /**
     * 是否使用长连接
     *
     * @return 是否使用长连接标识
     */
    public boolean isKeepAlive() {
        return keepAlive;
    }

    /**
     * 设置是否使用长连接
     *
     * @param keepAlive
     *            是否使用长连接标识
     */
    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    /**
     * 是否进行协议协商
     *
     * @return 协议协商标识
     */
    public boolean isAuthTypeNegotiation() {
        return authTypeNegotiation;
    }

    /**
     * 设置协议协商标识
     *
     * @param authTypeNegotiation
     *            协议协商标识
     */
    public void setAuthTypeNegotiation(boolean authTypeNegotiation) {
        this.authTypeNegotiation = authTypeNegotiation;
    }

    /**
     * 是否是自定义域名
     *
     * @return 自定义域名标识
     */
    public boolean isCname() {
        return cname;
    }

    /**
     * 设置自定义域名标识
     *
     * @param cname
     *            自定义域名标识
     */
    public void setCname(boolean cname) {
        this.cname = cname;
    }

    /**
     * 设置SSLContext的Provider
     *
     * @return SSLContext的Provider
     */
    public String getSslProvider() {
        return sslProvider;
    }

    /**
     * 获取SSLContext的Provider
     *
     * @param sslProvider
     *            SSLContext的Provider
     */
    public void setSslProvider(String sslProvider) {
        this.sslProvider = sslProvider;
    }

    /**
     * 设置访问OBS服务端时使用的HTTP协议类型
     *
     * @return HTTP协议类型
     */
    public HttpProtocolTypeEnum getHttpProtocolType() {
        return httpProtocolType;
    }

    /**
     * 获取访问OBS服务端时使用的HTTP协议类型
     *
     * @param httpProtocolType
     *            HTTP协议类型
     */
    public void setHttpProtocolType(HttpProtocolTypeEnum httpProtocolType) {
        this.httpProtocolType = httpProtocolType;
    }

    /**
     * 设置自定义分发器
     *
     * @return 自定义分发器
     */
    public Dispatcher getHttpDispatcher() {
        return httpDispatcher;
    }

    /**
     * 获取自定义分发器
     *
     * @param httpDispatcher
     *            自定义分发器
     */
    public void setHttpDispatcher(Dispatcher httpDispatcher) {
        this.httpDispatcher = httpDispatcher;
    }

}
