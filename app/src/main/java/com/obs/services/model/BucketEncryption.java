package com.obs.services.model;

/**
 * 桶加密信息, 目前仅支持 SSE-KMS 加密方式
 *
 */
public class BucketEncryption extends HeaderResponse {
    
    private SSEAlgorithmEnum sseAlgorithm;
    
    private String kmsKeyId;
    
    public BucketEncryption() {}

    /**
     * 构造方法
     * @param sseAlgorithm 桶加密算法
     */
    public BucketEncryption(SSEAlgorithmEnum sseAlgorithm) {
        this.sseAlgorithm = sseAlgorithm;
    }
    
    /**
     * 获取桶加密算法
     * @return 桶加密算法
     */
    public SSEAlgorithmEnum getSseAlgorithm() {
        return sseAlgorithm;
    }

    /**
     * 设置桶加密算法
     * @param sseAlgorithm 桶加密算法
     */
    public void setSseAlgorithm(SSEAlgorithmEnum sseAlgorithm) {
        this.sseAlgorithm = sseAlgorithm;
    }

    /**
     * 获取SSE-KMS方式下使用的主密钥，可为空，如果为空，那么默认的主密钥将会被使用
     * 
     * @return SSE-KMS方式下使用的主密钥
     */
    public String getKmsKeyId() {
        return kmsKeyId;
    }

    /**
     * 设置SSE-KMS方式下使用的主密钥，可为空，如果为空，那么默认的主密钥将会被使用
     * 
     * @param kmsKeyId SSE-KMS方式下使用的主密钥
     */
    public void setKmsKeyId(String kmsKeyId) {
        this.kmsKeyId = kmsKeyId;
    }

    @Override
    public String toString() {
        return "BucketEncryption [sseAlgorithm=" + sseAlgorithm + ", kmsKeyId=" + kmsKeyId + "]";
    }
    
}
