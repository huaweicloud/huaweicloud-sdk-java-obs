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

import java.util.Map;
import java.util.TreeMap;


public abstract class AbstractTemporarySignatureRequest {
    
    protected HttpMethodEnum method;

    protected String bucketName;

    protected String objectKey;

    protected SpecialParamEnum specialParam;

    protected Map<String, String> headers;

    protected Map<String, Object> queryParams;
    
    public AbstractTemporarySignatureRequest() {
    }


    public AbstractTemporarySignatureRequest(HttpMethodEnum method, String bucketName, String objectKey) {
        this.method = method;
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }


    public HttpMethodEnum getMethod()
    {
        return method;
    }
    

    public void setMethod(HttpMethodEnum method)
    {
        this.method = method;
    }
    

    public String getBucketName()
    {
        return bucketName;
    }
    

    public void setBucketName(String bucketName)
    {
        this.bucketName = bucketName;
    }
    

    public String getObjectKey()
    {
        return objectKey;
    }
    

    public void setObjectKey(String objectKey)
    {
        this.objectKey = objectKey;
    }
    

    public Map<String, String> getHeaders()
    {
        if (headers == null)
        {
            headers = new TreeMap<String, String>();
        }
        return headers;
    }
    

    public Map<String, Object> getQueryParams()
    {
        if (queryParams == null)
        {
            queryParams = new TreeMap<String, Object>();
        }
        return queryParams;
    }
    

    public SpecialParamEnum getSpecialParam()
    {
        return specialParam;
    }
    

    public void setSpecialParam(SpecialParamEnum specialParam)
    {
        this.specialParam = specialParam;
    }


    public void setHeaders(Map<String, String> headers)
    {
        this.headers = headers;
    }


    public void setQueryParams(Map<String, Object> queryParams)
    {
        this.queryParams = queryParams;
    }
}
