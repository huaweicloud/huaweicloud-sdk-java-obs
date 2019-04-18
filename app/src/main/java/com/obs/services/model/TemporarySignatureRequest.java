package com.obs.services.model;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.obs.services.internal.ObsConstraint;

/**
 * 临时授权访问的请求参数
 *
 */
public class TemporarySignatureRequest extends AbstractTemporarySignatureRequest {

    private long expires = ObsConstraint.DEFAULT_EXPIRE_SECONEDS;

    private Date requestDate;

    public TemporarySignatureRequest() {
    }

    /**
     * 构造函数
     * 
     * @param method  HTTP/HTTPS请求方法
     * @param expires 有效时间，单位：秒
     */
    public TemporarySignatureRequest(HttpMethodEnum method, long expires) {
        this(method, null, null, null, expires);
    }

    /**
     * 构造函数
     * 
     * @param method       HTTP/HTTPS请求方法
     * @param bucketName   桶名
     * @param objectKey    对象名
     * @param specialParam 特殊操作符
     * @param expires      有效时间，单位：秒
     */
    public TemporarySignatureRequest(HttpMethodEnum method, String bucketName, String objectKey,
            SpecialParamEnum specialParam, long expires) {
        this(method, bucketName, objectKey, specialParam, expires, null);
    }

    /**
     * 构造函数
     * 
     * @param method       HTTP/HTTPS请求方法
     * @param bucketName   桶名
     * @param objectKey    对象名
     * @param specialParam 特殊操作符
     * @param expires      有效时间，单位：秒
     * @param requestDate  请求日期
     */
    public TemporarySignatureRequest(HttpMethodEnum method, String bucketName, String objectKey,
            SpecialParamEnum specialParam, long expires, Date requestDate) {
        this.method = method;
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.specialParam = specialParam;
        this.expires = expires;
        this.requestDate = requestDate;
    }

    /**
     * 获取临时授权访问有效时间，单位：秒。默认值为5分钟（300），最大取值为7天（604800）
     * 
     * @return 有效时间
     */
    public long getExpires() {
        return expires;
    }

    /**
     * 设置临时授权访问有效时间，单位：秒。默认值为5分钟（300），最大取值为7天（604800）
     * 
     * @param expires 有效时间
     */
    public void setExpires(long expires) {
        this.expires = expires;
    }

    /**
     * 获取请求时间
     * 
     * @return 请求时间
     */
    public Date getRequestDate() {
        return requestDate;
    }

    /**
     * 设置请求时间
     * 
     * @param requestDate 请求时间
     */
    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    @Override
    public String toString() {
        return "TemporarySignatureRequest [method=" + method + ", bucketName=" + bucketName + ", objectKey=" + objectKey
                + ", specialParam=" + specialParam + ", expires=" + expires + ", requestDate=" + requestDate
                + ", headers=" + getHeaders() + ", queryParams=" + getQueryParams() + "]";
    }

}
