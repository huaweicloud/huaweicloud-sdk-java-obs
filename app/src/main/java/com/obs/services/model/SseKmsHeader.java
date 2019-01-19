package com.obs.services.model;

/**
 * SSE-KMS加密头域信息
 */
public class SseKmsHeader
{
    private ServerEncryption encryption;
    
    private String kmsKeyId;
    
    private String context;
    
    /**
     * 获取加密算法类型，目前仅支持kms
     * 
     * @return 加密算法类型
     */
    public ServerEncryption getEncryption()
    {
        return encryption;
    }
    
    /**
     * 设置加密算法类型，目前仅支持kms
     * 
     * @param encryption 加密算法类型
     */
    public void setEncryption(ServerEncryption encryption)
    {
        this.encryption = encryption;
    }
    
    /**
     * 获取SSE-KMS方式下使用的主密钥，可为空，如果为空，那么默认的主密钥将会被使用
     * 
     * @return SSE-KMS方式下使用的主密钥
     */
    public String getKmsKeyId()
    {
        return kmsKeyId;
    }
    
    /**
     * 设置SSE-KMS方式下使用的主密钥，可为空，如果为空，那么默认的主密钥将会被使用
     * 
     * @param kmsKeyId SSE-KMS方式下使用的主密钥
     */
    public void setKmsKeyId(String kmsKeyId)
    {
        this.kmsKeyId = kmsKeyId;
    }
    
    @Deprecated
    public String getContext()
    {
        return context;
    }
    
    @Deprecated
    public void setContext(String context)
    {
        this.context = context;
    }

    @Override
    public String toString()
    {
        return "SseKmsHeader [encryption=" + encryption + ", kmsKeyId=" + kmsKeyId + ", context=" + context + "]";
    }
    
}
