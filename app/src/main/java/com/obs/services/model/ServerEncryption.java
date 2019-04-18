package com.obs.services.model;

/**
 * SSE-KMS的加密方式
 */
@Deprecated
public final class ServerEncryption
{
    /**
     * SSE-KMS目前仅支持kms加密方式
     */
    public static final ServerEncryption OBS_KMS = new ServerEncryption("kms");
    
    private String serverEncryption = "";
    
    private ServerEncryption(String serverEncryption)
    {
        this.serverEncryption = serverEncryption;
    }
    
    public String getServerEncryption()
    {
        return serverEncryption;
    }
    
    public static ServerEncryption parseServerEncryption(String str)
    {
        ServerEncryption serverEncryption = null;
        
        if (null != str && str.equals(OBS_KMS.toString()))
        {
            serverEncryption = OBS_KMS;
        }
        return serverEncryption;
    }
    
    @Override
    public String toString()
    {
        return serverEncryption;
    }
    
    public boolean equals(Object obj)
    {
        return (obj instanceof ServerEncryption) && toString().equals(obj.toString());
    }
    
    public int hashCode()
    {
        return serverEncryption.hashCode();
    }
}
