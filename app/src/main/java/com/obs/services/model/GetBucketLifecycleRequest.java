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

package com.obs.services.model;

/**
 *
 * 
 * @since 3.25.10
 */
public class GetBucketLifecycleRequest extends BaseBucketRequest {

    {
        httpMethod = HttpMethodEnum.GET;
    }

    private String ruleId;
    private String ruleIdMarker;

    public GetBucketLifecycleRequest(String bucketName) {
        this.bucketName = bucketName;
    }

    public GetBucketLifecycleRequest(String bucketName, String ruleId) {
        this(bucketName);
        this.ruleId = ruleId;
    }

    public GetBucketLifecycleRequest(String bucketName, String ruleId, String ruleIdMarker) {
        this(bucketName, ruleId);
        this.ruleIdMarker = ruleIdMarker;
    }

    public String getRuleId() {
        return ruleId;
    }

    public String getRuleIdMarker() {
        return ruleIdMarker;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public void setRuleIdMarker(String ruleIdMarker) {
        this.ruleIdMarker = ruleIdMarker;
    }
}
