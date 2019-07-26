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
package com.obs.services.internal.security;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.EcsSecurityProvider;
import com.obs.services.ISecurityProvider;
import com.obs.services.internal.ObsConstraint;
import com.obs.services.model.AuthTypeEnum;
import com.obs.services.model.LimitedTimeSecurityKey;

public class ProviderCredentials {
    protected static final ILogger log = LoggerBuilder.getLogger(ProviderCredentials.class);

    protected String accessKey;
    protected String secretKey;
    protected AuthTypeEnum authType;
    private String securityToken;
	private ThreadLocal<AuthTypeEnum> threadLocalAuthType;
	private ISecurityProvider securityProvider;


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

    public void setSecurityProvider(ISecurityProvider securityProvider){
        this.securityProvider = securityProvider;
    }

    public void checkSecurityWillSoonExpire(){
        if(this.securityProvider != null && this.securityProvider instanceof EcsSecurityProvider){
            LimitedTimeSecurityKey securityKey = (LimitedTimeSecurityKey)securityProvider.getSecurityKey();
            this.accessKey = securityKey.getAccessKey();
            this.secretKey = securityKey.getSecretKey();
            this.securityToken = securityKey.getSecurityToken();
        }
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
