package com.obs.services.internal.security;

public class ProviderCredentialThreadContext
{   
    
    private ThreadLocal<ProviderCredentials> context = new ThreadLocal<ProviderCredentials>();
    
    private ProviderCredentialThreadContext(){
        
    }
    
    private static class ProviderCredentialThreadContextHolder{
        private static ProviderCredentialThreadContext instance = new ProviderCredentialThreadContext();
    }
    
    public static ProviderCredentialThreadContext getInstance(){
        return ProviderCredentialThreadContextHolder.instance;
    }
    
    public void setProviderCredentials(ProviderCredentials providerCredentials){
        context.set(providerCredentials);
    }
    
    public ProviderCredentials getProviderCredentials(){
        return context.get();
    }
    
    public void clearProviderCredentials(){
        context.remove();
    }
    
}
