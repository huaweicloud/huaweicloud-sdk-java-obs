package com.obs.services.model;

/**
 * SSE-C加密算法
 */
@Deprecated
public final class ServerAlgorithm
{
    /**
     * SSE-C目前只支持AES256算法
     */
    public static final ServerAlgorithm AES256 = new ServerAlgorithm("AES256");
    
    private String serverAlgorithm = "";
    
    private ServerAlgorithm(String serverAlgorithm)
    {
        this.serverAlgorithm = serverAlgorithm;
    }
    
    public String getServerAlgorithm()
    {
        return serverAlgorithm;
    }
    
    public static ServerAlgorithm parseServerAlgorithm(String str)
    {
        ServerAlgorithm serverAlgorithm = null;
        
        if (null != str && str.equals(AES256.toString()))
        {
            serverAlgorithm = AES256;
        }
        return serverAlgorithm;
    }
    
    @Override
    public String toString()
    {
        return serverAlgorithm;
    }
    
    public boolean equals(Object obj)
    {
        return (obj instanceof ServerAlgorithm) && toString().equals(obj.toString());
    }
    
    public int hashCode()
    {
        return serverAlgorithm.hashCode();
    }
}
