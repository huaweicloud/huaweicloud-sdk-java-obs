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
package com.obs.services;

import com.obs.services.internal.ObsConstraint;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.BasicSecurityKey;
import com.obs.services.model.ISecurityKey;

import java.util.Iterator;
import java.util.Map;

public class EnvironmentVariableSecurityProvider implements ISecurityProvider {
    @Override
    public void setSecurityKey(ISecurityKey securityKey) {

    }

    @Override
    public ISecurityKey getSecurityKey() {
        String accessKey = stringTrim(System.getenv(ObsConstraint.ACCESS_KEY_ENV_VAR));
        String secretKey = stringTrim(System.getenv(ObsConstraint.SECRET_KEY_ENV_VAR));
        String securityToken = stringTrim(System.getenv(ObsConstraint.SECURITY_TOKEN_ENV_VAR));

        ServiceUtils.asserParameterNotNull(accessKey, "access key should not be null or empty.");
        ServiceUtils.asserParameterNotNull(secretKey, "secret key should not be null or empty.");

        return new BasicSecurityKey(accessKey, secretKey, securityToken);
    }

    private static String stringTrim(String value) {
        if (value == null) {
            return null;
        }
        return value.trim();
    }
}
