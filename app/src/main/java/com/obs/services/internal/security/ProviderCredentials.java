/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.obs.services.internal.security;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.BasicObsCredentialsProvider;
import com.obs.services.EcsObsCredentialsProvider;
import com.obs.services.IObsCredentialsProvider;
import com.obs.services.internal.ObsConstraint;
import com.obs.services.model.AuthTypeEnum;

public class ProviderCredentials {
    protected static final ILogger log = LoggerBuilder.getLogger(ProviderCredentials.class);

    protected AuthTypeEnum authType;
    private ThreadLocal<AuthTypeEnum> threadLocalAuthType;
    private IObsCredentialsProvider obsCredentialsProvider;


    public String getRegion() {
        return ObsConstraint.DEFAULT_BUCKET_LOCATION_VALUE;
    }

    public ProviderCredentials(String accessKey, String secretKey) {
        this.setObsCredentialsProvider(new BasicObsCredentialsProvider(accessKey, secretKey));
    }

    public ProviderCredentials(String accessKey, String secretKey, String securityToken) {
        this.setObsCredentialsProvider(new BasicObsCredentialsProvider(accessKey, secretKey, securityToken));
    }

    public AuthTypeEnum getAuthType() {
        return (threadLocalAuthType == null) ? authType : threadLocalAuthType.get();
    }

    public void setAuthType(AuthTypeEnum authType) {
        this.authType = authType;
    }

    public void setObsCredentialsProvider(IObsCredentialsProvider ObsCredentialsProvider) {
        this.obsCredentialsProvider = ObsCredentialsProvider;
    }

    public IObsCredentialsProvider getObsCredentialsProvider(){
        return  this.obsCredentialsProvider;
    }

    public BasicSecurityKey getSecurityKey(){
        return (BasicSecurityKey)this.obsCredentialsProvider.getSecurityKey();
    }


    public void setThreadLocalAuthType(AuthTypeEnum authType) {
        if (threadLocalAuthType != null) {
            threadLocalAuthType.set(authType);
        }
    }

    public void removeThreadLocalAuthType() {
        if (threadLocalAuthType != null) {
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
