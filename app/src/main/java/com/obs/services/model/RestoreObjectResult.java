package com.obs.services.model;

/**
 * 取回归档对象的响应结果
 */
public class RestoreObjectResult extends HeaderResponse {

    private String bucketName;
    private String objectKey;
    private String versionId;


    public RestoreObjectResult(String bucketName, String objectKey, String versionId) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.versionId = versionId;
    }
    
    public String getBucketName() {
        return bucketName;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public String getVersionId() {
        return versionId;
    }    
}
