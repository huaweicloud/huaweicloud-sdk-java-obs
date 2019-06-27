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
**/
package com.obs.services.model;

import java.util.List;

import com.obs.services.internal.ObsConstraint;


public class RestoreObjectsRequest extends AbstractBulkRequest{
    
    private int days;

    private RestoreTierEnum tier;

    private String prefix;
    
    private boolean versionRestored;

    private List<KeyAndVersion> keyAndVersions;

    private TaskCallback<RestoreObjectResult, RestoreObjectRequest> callback;

    public RestoreObjectsRequest() {

    }


    public RestoreObjectsRequest(String bucketName) {
        super(bucketName);
    }


    public RestoreObjectsRequest(String bucketName, int days, RestoreTierEnum tier) {
        super(bucketName);
        this.days = days;
        this.tier = tier;
    }
    


    public int getDays() {
        return days;
    }


    public void setDays(int days) {
        this.days = days;
    }


    public RestoreTierEnum getRestoreTier() {
        return tier;
    }


    public void setRestoreTier(RestoreTierEnum tier) {
        this.tier = tier;
    }


    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }


    public String getPrefix() {
        return prefix;
    }
    

    public boolean isVersionRestored() {
        return versionRestored;
    }


    public void setVersionRestored(boolean versionRestored) {
        this.versionRestored = versionRestored;
    }


    public void setKeyAndVersions(List<KeyAndVersion> keyAndVersions) {
        this.keyAndVersions = keyAndVersions;
    }


    public List<KeyAndVersion> getKeyAndVersions() {
        return this.keyAndVersions;
    }


    public KeyAndVersion addKeyAndVersion(String objectKey, String versionId) {
        KeyAndVersion kv = new KeyAndVersion(objectKey, versionId);
        this.getKeyAndVersions().add(kv);
        return kv;
    }


    public KeyAndVersion addKeyAndVersion(String objectKey) {
        return this.addKeyAndVersion(objectKey, null);
    }


    public TaskCallback<RestoreObjectResult, RestoreObjectRequest> getCallback() {
        return callback;
    }


    public void setCallback(TaskCallback<RestoreObjectResult, RestoreObjectRequest> callback) {
        this.callback = callback;
    }


    @Override
    public String toString() {
        return "RestoreObjectsRequest [bucketName=" + bucketName + ", days=" + days + ", tier=" + tier + "]";
    }

}
