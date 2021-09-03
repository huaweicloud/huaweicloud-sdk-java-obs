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

package com.obs.services;

/**
 * HTTP代理配置信息
 */
public class HttpProxyConfiguration {

    private String proxyAddr;

    private int proxyPort;

    private String proxyUname;

    private String userPasswd;

    private String domain;

    private String workstation;

    public HttpProxyConfiguration() {
    }

    /**
     * 
     * @param proxyAddr
     *            代理地址
     * @param proxyPort
     *            代理端口
     * @param proxyUname
     *            代理用户名
     * @param userPasswd
     *            代理密码
     * @param domain
     *            代理域
     */
    public HttpProxyConfiguration(String proxyAddr, int proxyPort, String proxyUname, String userPasswd,
            String domain) {
        this.proxyAddr = proxyAddr;
        this.proxyPort = proxyPort;
        this.proxyUname = proxyUname;
        this.userPasswd = userPasswd;
        this.domain = domain;
        this.workstation = this.proxyAddr;
    }

    /**
     * 带参构造函数
     * 
     * @param proxyAddr
     *            代理地址
     * @param proxyPort
     *            代理端口
     * @param proxyUname
     *            代理用户名
     * @param userPasswd
     *            代理密码
     * @param domain
     *            代理域
     * @param workstation
     *            代理所在工作区
     */
    public HttpProxyConfiguration(String proxyAddr, int proxyPort, String proxyUname, String userPasswd, String domain,
            String workstation) {
        this(proxyAddr, proxyPort, proxyUname, userPasswd, domain);
        this.workstation = this.proxyAddr;
    }

    /**
     * 获取代理地址
     * 
     * @return 代理地址
     */
    public String getProxyAddr() {
        return proxyAddr;
    }

    /**
     * 设置代理地址
     * 
     * @param proxyAddr
     *            代理地址
     */
    public void setProxyAddr(String proxyAddr) {
        this.proxyAddr = proxyAddr;
    }

    /**
     * 获取代理端口
     * 
     * @return 代理端口
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * 设置代理端口
     * 
     * @param proxyPort
     *            代理端口
     */
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    /**
     * 获取用户名
     * 
     * @return 用户名
     */
    public String getProxyUName() {
        return proxyUname;
    }

    /**
     * 设置用户名
     * 
     * @param proxyUName
     *            用户名
     */
    public void setProxyUName(String proxyUName) {
        this.proxyUname = proxyUName;
    }

    /**
     * 获取代理用户密码
     * 
     * @return 代理用户密码
     */
    public String getUserPasswd() {
        return userPasswd;
    }

    @Deprecated
    public String getUserPaaswd() {
        return getUserPasswd();
    }

    /**
     * 设置代理用户密码
     * 
     * @param userPasswd
     *            代理用户密码
     */
    public void setUserPasswd(String userPasswd) {
        this.userPasswd = userPasswd;
    }

    @Deprecated
    public void setUserPaaswd(String userPasswd) {
        setUserPasswd(userPasswd);
    }

    /**
     * 获取代理域
     * 
     * @return 代理域
     */
    public String getDomain() {
        return domain;
    }

    /**
     * 设置代理域
     * 
     * @param domain
     *            代理域
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * 获取代理工作区
     * 
     * @return 代理工作区
     */
    public String getWorkstation() {
        return workstation;
    }

    /**
     * 设置代理工作区
     * 
     * @param workstation
     *            代理工作区
     */
    public void setWorkstation(String workstation) {
        this.workstation = workstation;
    }

}
