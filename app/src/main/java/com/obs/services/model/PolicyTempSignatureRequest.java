package com.obs.services.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.obs.services.internal.ObsConstraint;
import com.obs.services.internal.utils.ServiceUtils;

/**
 * 带策略的临时授权访问的请求参数
 *
 */
public class PolicyTempSignatureRequest extends AbstractTemporarySignatureRequest{
    
    private Date expiryDate;
    
    private long expires = ObsConstraint.DEFAULT_EXPIRE_SECONEDS;
    
    private List<PolicyConditionItem> conditions;
    
    public PolicyTempSignatureRequest() {
    }
    
    /**
     * 构造方法
     * @param method HTTP/HTTPS请求方法
     * @param bucketName 桶名
     * @param objectKey 对象名
     */
    public PolicyTempSignatureRequest(HttpMethodEnum method, String bucketName, String objectKey) {
        super(method, bucketName, objectKey);
    }

    /**
     * 构造方法
     * @param method HTTP/HTTPS请求方法
     * @param bucketName 桶名
     * @param objectKey 对象名
     * @param expiryDate 有效截止日期
     */
    public PolicyTempSignatureRequest(HttpMethodEnum method, String bucketName, String objectKey, Date expiryDate) {
        super(method, bucketName, objectKey);
        this.expiryDate = expiryDate;
    }
    
    /**
     * 构造方法
     * @param method HTTP/HTTPS请求方法
     * @param bucketName 桶名
     * @param objectKey 对象名
     * @param expires 有效时间
     */
    public PolicyTempSignatureRequest(HttpMethodEnum method, String bucketName, String objectKey, long expires) {
        super(method, bucketName, objectKey);
        this.expires = expires;
    }
    
    /**
     * 根据有效期和策略条件生成策略
     * @return
     */
    public String generatePolicy() {
        Date requestDate = new Date();
        SimpleDateFormat expirationDateFormat = ServiceUtils.getExpirationDateFormat();
        Date expiryDate = this.expiryDate;
        if (expiryDate == null) {
            expiryDate = new Date(requestDate.getTime() + (this.expires <=0 ? ObsConstraint.DEFAULT_EXPIRE_SECONEDS : this.expires) * 1000);
        }
        String expiration = expirationDateFormat.format(expiryDate);
        StringBuilder policy = new StringBuilder();
        policy.append("{\"expiration\":").append("\"").append(expiration).append("\",").append("\"conditions\":[");
        if (this.conditions != null && !this.conditions.isEmpty()) {
            policy.append(ServiceUtils.join(this.conditions, ","));
        }
        policy.append("]}");
        return policy.toString();
    }


    /**
     * 设置有效截止日期
     * @return 有效截止日期
     */
    public Date getExpiryDate()
    {
        return expiryDate;
    }
    
    /**
     * 获取有效截止日期
     * @param expiryDate 有效截止日期
     */
    public void setExpiryDate(Date expiryDate)
    {
        this.expiryDate = expiryDate;
    }
    
    /**
     * 获取有效时间，默认值为5分钟（300）
     * @return 有效时间
     */
    public long getExpires()
    {
        return expires;
    }
    
    /**
     * 设置有效时间，单位：秒
     * @param expires 有效时间
     */
    public void setExpires(long expires)
    {
        this.expires = expires;
    }

    /**
     * 获取策略的条件集合
     * @return 策略条件集合
     */
    public List<PolicyConditionItem> getConditions() {
        return conditions;
    }

    /**
     * 设置策略的条件集合
     * @param conditions 策略条件集合
     */
    public void setConditions(List<PolicyConditionItem> conditions) {
        this.conditions = conditions;
    }
}
