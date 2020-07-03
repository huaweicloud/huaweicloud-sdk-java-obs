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

package com.obs.services.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.obs.services.internal.ObsConstraint;
import com.obs.services.internal.utils.ServiceUtils;

/**
 * 基于浏览器表单授权访问请求参数
 *
 */
public class PostSignatureRequest {

    private Date requestDate;

    private Date expiryDate;

    private String bucketName;

    private String objectKey;

    private long expires = ObsConstraint.DEFAULT_EXPIRE_SECONEDS;

    private Map<String, Object> formParams;

    private List<String> conditions;

    public PostSignatureRequest() {

    }

    /**
     * 构造函数
     * 
     * @param expires
     *            有效时间，单位：秒
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     */
    public PostSignatureRequest(long expires, String bucketName, String objectKey) {
        this.expires = expires;
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    /**
     * 构造函数
     * 
     * @param expiryDate
     *            有效截止日期
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     */
    public PostSignatureRequest(Date expiryDate, String bucketName, String objectKey) {
        this.expiryDate = ServiceUtils.cloneDateIgnoreNull(expiryDate);
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    /**
     * 
     * @param expires
     *            有效时间，单位：秒
     * @param requestDate
     *            请求时间
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     */
    public PostSignatureRequest(long expires, Date requestDate, String bucketName, String objectKey) {
        this.expires = expires;
        this.requestDate = ServiceUtils.cloneDateIgnoreNull(requestDate);
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    /**
     * 
     * @param expiryDate
     *            有效截止日期
     * @param requestDate
     *            请求时间
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     */
    public PostSignatureRequest(Date expiryDate, Date requestDate, String bucketName, String objectKey) {
        this.expiryDate = ServiceUtils.cloneDateIgnoreNull(expiryDate);
        this.requestDate = ServiceUtils.cloneDateIgnoreNull(requestDate);
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    /**
     * 设置请求时间
     * 
     * @return 请求时间
     */
    public Date getRequestDate() {
        return ServiceUtils.cloneDateIgnoreNull(this.requestDate);
    }

    /**
     * 获取请求时间
     * 
     * @param requestDate
     *            请求时间
     */
    public void setRequestDate(Date requestDate) {
        this.requestDate = ServiceUtils.cloneDateIgnoreNull(requestDate);
    }

    /**
     * 设置有效截止日期
     * 
     * @return 有效截止日期
     */
    public Date getExpiryDate() {
        return ServiceUtils.cloneDateIgnoreNull(this.expiryDate);
    }

    /**
     * 获取有效截止日期
     * 
     * @param expiryDate
     *            有效截止日期
     */
    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = ServiceUtils.cloneDateIgnoreNull(expiryDate);
    }

    /**
     * 获取有效时间，默认值为5分钟（300）
     * 
     * @return 有效时间
     */
    public long getExpires() {
        return expires;
    }

    /**
     * 设置有效时间，单位：秒
     * 
     * @param expires
     *            有效时间
     */
    public void setExpires(long expires) {
        this.expires = expires;
    }

    /**
     * 获取请求的表单参数
     * 
     * @return 请求的表单参数
     */
    public Map<String, Object> getFormParams() {
        if (formParams == null) {
            formParams = new HashMap<String, Object>();
        }
        return formParams;
    }

    /**
     * 设置请求的表单参数
     * 
     * @param formParams
     *            请求的表单参数
     */
    public void setFormParams(Map<String, Object> formParams) {
        this.formParams = formParams;
    }

    /**
     * 获取桶名
     * 
     * @return 桶名
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * 设置桶名
     * 
     * @param bucketName
     *            桶名
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 获取对象名
     * 
     * @return 对象名
     */
    public String getObjectKey() {
        return objectKey;
    }

    /**
     * 设置对象名
     * 
     * @param objectKey
     *            对象名
     */
    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    /**
     * 获取表单限制条件，如果设置了该值，将直接使用该值计算policy而忽略请求的表单参数中的设置
     * 
     * @return 表单限制条件
     */
    public List<String> getConditions() {
        if (this.conditions == null) {
            this.conditions = new ArrayList<String>();
        }
        return conditions;
    }

    /**
     * 设置表单限制条件，如果设置了该值，将直接使用该值计算policy而忽略请求的表单参数中的设置
     * 
     * @param conditions
     *            表单限制条件
     */
    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }

    @Override
    public String toString() {
        return "PostSignatureRequest [requestDate=" + requestDate + ", expiryDate=" + expiryDate + ", bucketName="
                + bucketName + ", objectKey=" + objectKey + ", expires=" + expires + ", formParams=" + formParams
                + ", conditions=" + conditions + "]";
    }

}
