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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.obs.services.internal.utils.JSONChange;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

public class EcsSecurityUtils {
    /** Default root url for the openstack metadata apis. */
    private static final String OPENSTACK_METADATA_ROOT = "/openstack/latest";

    /** Default endpoint for the ECS Instance Metadata Service. */
    private static final String ECS_METADATA_SERIVCE_URL = "http://169.254.169.254";

    private static final String EC2_METADATA_SERVICE_OVERRIDE_URL = "ecsMetadataServiceOverrideEndpoint";

    private static OkHttpClient httpClient = new OkHttpClient.Builder().followRedirects(false)
            .retryOnConnectionFailure(false).cache(null).build();

    /**
     * The temporary security credentials (access, secret, securitytoken,
     * and expires_at) associated with the IAM role.
     */
    public static class SecurityKey {
        @JsonProperty("access")
        public String accessKey;
        @JsonProperty("secret")
        public String secretKey;
        @JsonProperty("securitytoken")
        public String securityToken;
        @JsonProperty("expires_at")
        public String expiresDate;
    }

    /**
     * Returns the temporary security credentials (access, secret, securitytoken,
     * and expires_at) associated with the IAM roles on the instance.
     */
    public static SecurityKey getSecurityKey() throws IOException {
        String securityKeyInfo = getResource(OPENSTACK_METADATA_ROOT + "/securitykey");
        return (SecurityKey)JSONChange.jsonToObj(new SecurityKey(), securityKeyInfo);
    }

    /**
     * Returns the host address of the ECS Instance Metadata Service.
     */
    public static String getEndpointForECSMetadataService() {
        String overridUrl = System.getProperty(EC2_METADATA_SERVICE_OVERRIDE_URL);
        return overridUrl != null ? overridUrl : ECS_METADATA_SERIVCE_URL;
    }

    /**
     * Get resource and return contents from metadata service
     * with the specify path.
     */
    private static String getResource(String path) throws IOException {
        Request.Builder builder = new Request.Builder();
        String endpoint = getEndpointForECSMetadataService();
        builder.header("Accept", "*/*");
        Request request = builder.url(endpoint).get().build();
        Call c = httpClient.newCall(request);
        Response res = c.execute();
        System.out.println("\tStatus:" + res.code());
        String content = new String();
        if (res.body() != null) {
            content = res.body().string();
        }
        res.close();
        return content;
    }
}
