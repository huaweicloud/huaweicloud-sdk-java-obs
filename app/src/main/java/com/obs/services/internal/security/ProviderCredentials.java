package com.obs.services.internal.security;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.internal.ObsConstraint;
import com.obs.services.model.AuthTypeEnum;

public class ProviderCredentials {
    protected static final ILogger log = LoggerBuilder.getLogger(ProviderCredentials.class);

    protected String accessKey;
    protected String secretKey;
    protected AuthTypeEnum authType;
    private String securityToken;
	private ThreadLocal<AuthTypeEnum> threadLocalAuthType;


    public String getRegion()
    {
        return ObsConstraint.DEFAULT_BUCKET_LOCATION_VALUE;
    }

    public ProviderCredentials(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }
    
    public ProviderCredentials(String accessKey, String secretKey, String securityToken)
    {
        this(accessKey, secretKey);
        this.securityToken = securityToken;
    }

    public AuthTypeEnum getAuthType() {
    	return (threadLocalAuthType == null) ? authType : threadLocalAuthType.get();
    }

    public void setAuthType(AuthTypeEnum authType) {
        this.authType = authType;
    }
    
    public String getSecurityToken()
    {
        return securityToken;
    }

    public void setSecurityToken(String securityToken)
    {
        this.securityToken = securityToken;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setThreadLocalAuthType(AuthTypeEnum authType) {
    	if(threadLocalAuthType != null) {
    		threadLocalAuthType.set(authType);
    	}
    }
    
    public void removeThreadLocalAuthType() {
    	if(threadLocalAuthType != null) {
    		threadLocalAuthType.remove();
    	}
    }

    public void initThreadLocalAuthType() {
    	if (threadLocalAuthType == null) {
    		threadLocalAuthType = new ThreadLocal<AuthTypeEnum>() {
			    @Override
			    protected AuthTypeEnum initialValue() {
				    return ProviderCredentials.this.authType;
			    }
		    };
	    }
    }
}
