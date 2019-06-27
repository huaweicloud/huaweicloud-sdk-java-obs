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
**/
package com.obs.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * 接口日志类，主要提供日志的格式化
 * 
 */
public class InterfaceLogBean
{
    private static final String DATE_FMT_YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 唯一标识接口消息所属事务，不存在时为空
     */
    private String transactionId;
   
    /**
     * 填写接口所属的产品，如UC的接口填写UC。包括UC、IVS、TP、FusionSphere、Storage等
     */
    private String product;
    
    /**
     * 接口类型，值为1和2：其中1标识为北向接口；2标识为南向接口
     */
    private String interfaceType;
    
    /**
     * 协议类型，值为SOAP（细分ParlayX）、Rest、COM、Native、HTTP+XML，SMPP
     */
    private String protocolType;
    
    /**
     * 接口名称
     */
    private String name;
    
    /**
     * 源端设备，客户端API类为空，参数不对外体现
     */
    private String sourceAddr;
    
    /**
     * 宿端设备，客户端API类为空，参数不对外体现
     */
    private String targetAddr;
    
    /**
     * 北向接口收到请求的时间，南向接口发起请求的时间
     */
    private Date reqTime;
    
    /**
     * 格式为yyyy-MM-dd HH:mm:ss
     */
    private String reqTimeAsString;
    
    /**
     * 北向接口应答的时间，南向接口收到应答的时间
     */
    private Date respTime;
    
    /**
     * 格式为yyyy-MM-dd HH:mm:ss
     */
    private String respTimeAsString;
    
    /**
     * 请求参数，关键字需要用*替换
     */
    private String reqParams;
    
    /**
     * 接口返回结果码
     */
    private String resultCode;
    
    /**
     * 应答参数，关键字需要用*替换
     */
    private String respParams;

    public InterfaceLogBean(){}
    
    /**
     * 默认
     * InterfaceType 1,
     * Product Storage,
     * ProtocolType HTTP+XML,
     * ReqTime 构造时间,
     * sourceAddr 本地ip,
     * transactionId 请求编号，可以是当前生成的UUID
     * 响应信息需要在响应的时候设置
     * @param name 接口名称
     * @param targetAddr 目标主机IP
     * @param reqParams 请求参数
     */
    public InterfaceLogBean(String name,String targetAddr, String reqParams)
    {
        this.transactionId = "";
        this.interfaceType = "1";
        this.product = "Storage";
        this.protocolType = "HTTP+XML";
        this.reqTime = new Date();
        this.name = name;
        this.sourceAddr = "";
        this.targetAddr = "";
        this.reqParams = reqParams;
    }
    
    /**
     * 设置响应信息<br/>
     * 响应时间默认设置为使用该方法的时间，如需另外设置，可以使用setRespTime方法设置
     * @param respParams 响应参数
     * @param resultCode 结果码
     * @return
     */
    public void setResponseInfo(String respParams,String resultCode)
    {
        this.respParams = respParams;
        this.resultCode = resultCode;
    }
    
    public String getTransactionId()
    {
        return transactionId;
    }

    public void setTransactionId(String transactionId)
    {
        this.transactionId = transactionId;
    }

    public String getProduct()
    {
        return product;
    }

    public void setProduct(String product)
    {
        this.product = product;
    }

    public String getInterfaceType()
    {
        return interfaceType;
    }

    public void setInterfaceType(String interfaceType)
    {
        this.interfaceType = interfaceType;
    }

    public String getProtocolType()
    {
        return protocolType;
    }

    public void setProtocolType(String protocolType)
    {
        this.protocolType = protocolType;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSourceAddr()
    {
        return sourceAddr;
    }

    public void setSourceAddr(String sourceAddr)
    {
        this.sourceAddr = sourceAddr;
    }

    public String getTargetAddr()
    {
        return targetAddr;
    }

    public void setTargetAddr(String targetAddr)
    {
        this.targetAddr = targetAddr;
    }

    public Date getReqTime()
    {
        return reqTime;
    }

    public void setReqTime(Date reqTime)
    {
        this.reqTime = reqTime;
    }

    public String getReqTimeAsString()
    {
        //DATE_FMT_YYYYMMDDHHMMSS
        if (null == reqTimeAsString && null != reqTime)
        {
            DateFormat df = new SimpleDateFormat(DATE_FMT_YYYYMMDDHHMMSS);
            return df.format(reqTime);
        }
        return reqTimeAsString;
    }

    public void setReqTimeAsString(String reqTimeAsString)
    {
        this.reqTimeAsString = reqTimeAsString;
    }

    public Date getRespTime()
    {
        return respTime;
    }

    public void setRespTime(Date respTime)
    {
        this.respTime = respTime;
    }

    public String getRespTimeAsString()
    {
        if (null == respTimeAsString && null != respTime)
        {
            DateFormat df = new SimpleDateFormat(DATE_FMT_YYYYMMDDHHMMSS);
            return df.format(respTime);
        }
        return respTimeAsString;
    }

    public void setRespTimeAsString(String respTimeAsString)
    {
        this.respTimeAsString = respTimeAsString;
    }

    public String getReqParams()
    {
        return reqParams;
    }

    public void setReqParams(String reqParams)
    {
        this.reqParams = reqParams;
    }

    public String getResultCode()
    {
        return resultCode;
    }

    public void setResultCode(String resultCode)
    {
        this.resultCode = resultCode;
    }

    public String getRespParams()
    {
        return respParams;
    }

    public void setRespParams(String respParams)
    {
        this.respParams = respParams;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getProduct()).append("|");
        sb.append(this.getInterfaceType()).append("|");
        sb.append(this.getProtocolType()).append("|");
        sb.append(this.getName()).append("|");
        sb.append(this.getSourceAddr()).append("|");
        sb.append(this.getTargetAddr()).append("|");
        sb.append(this.getTransactionId() == null ? "" : this.getTransactionId()).append("|");
        sb.append(this.getReqTimeAsString()).append("|");
        sb.append(this.getRespTimeAsString()).append("|");
        sb.append(this.getReqParams() == null ? "" : this.getReqParams()).append("|");
        sb.append(this.getRespParams() == null ? "" : this.getRespParams()).append("|");
        sb.append(this.getResultCode()).append("|");
        return sb.toString();
    }
}
