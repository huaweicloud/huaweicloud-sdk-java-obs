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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.obs.services.internal.ObsConstraint;
import com.obs.services.internal.utils.ServiceUtils;


public class PolicyTempSignatureRequest extends AbstractTemporarySignatureRequest{
    
    private Date expiryDate;
    
    private long expires = ObsConstraint.DEFAULT_EXPIRE_SECONEDS;
    
    private List<PolicyConditionItem> conditions;
    
    public PolicyTempSignatureRequest() {
    }
    

    public PolicyTempSignatureRequest(HttpMethodEnum method, String bucketName, String objectKey) {
        super(method, bucketName, objectKey);
    }


    public PolicyTempSignatureRequest(HttpMethodEnum method, String bucketName, String objectKey, Date expiryDate) {
        super(method, bucketName, objectKey);
        this.expiryDate = expiryDate;
    }
    

    public PolicyTempSignatureRequest(HttpMethodEnum method, String bucketName, String objectKey, long expires) {
        super(method, bucketName, objectKey);
        this.expires = expires;
    }
    

    public String generatePolicy() {
        Date requestDate = new Date();
        SimpleDateFormat expirationDateFormat = ServiceUtils.getExpirationDateFormat();
        Date expiryDate = this.expiryDate;
        if (expiryDate == null) {
            expiryDate = new Date(requestDate.getTime() + (this.expires <=0 ? ObsConstraint.DEFAULT_EXPIRE_SECONEDS : this.expires) * 1000);
        }
        String expiration = expirationDateFormat.format(expiryDate);
        StringBuilder policy = new StringBuilder();
        policy.append("{\"expiration\":").append("\"").append(expiration).append("\",").append("\"conditions\":[");
        if (this.conditions != null && !this.conditions.isEmpty()) {
            policy.append(ServiceUtils.join(this.conditions, ","));
        }
        policy.append("]}");
        return policy.toString();
    }



    public Date getExpiryDate()
    {
        return expiryDate;
    }
    

    public void setExpiryDate(Date expiryDate)
    {
        this.expiryDate = expiryDate;
    }
    

    public long getExpires()
    {
        return expires;
    }
    

    public void setExpires(long expires)
    {
        this.expires = expires;
    }


    public List<PolicyConditionItem> getConditions() {
        return conditions;
    }


    public void setConditions(List<PolicyConditionItem> conditions) {
        this.conditions = conditions;
    }
}
