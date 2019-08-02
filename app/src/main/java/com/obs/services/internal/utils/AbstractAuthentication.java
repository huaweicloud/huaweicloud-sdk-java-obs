/**
 * JetS3t : Java S3 Toolkit
 * Project hosted at http://bitbucket.org/jmurty/jets3t/
 *
 * Copyright 2006-2010 James Murty
 * 
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
package com.obs.services.internal.utils;

import com.obs.services.internal.Constants;
import com.obs.services.internal.IHeaders;
import com.obs.services.internal.ServiceException;
import com.obs.services.internal.security.BasicSecurityKey;
import com.obs.services.internal.security.ProviderCredentials;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

public abstract class AbstractAuthentication{
	
	protected abstract IHeaders getIHeaders();
	
	protected abstract String getAuthPrefix();
	
	public static String caculateSignature(String stringToSign, String sk) throws ServiceException {
		return ServiceUtils.signWithHmacSha1(sk, stringToSign);
	}
	
	public final IAuthentication makeAuthorizationString(String method, Map<String, String> headers, String fullUrl, List<String> serviceResourceParameterNames,ProviderCredentials credent) throws ServiceException{
        String canonicalString;
        try
        {
            canonicalString = this.makeServiceCanonicalString(method, fullUrl,
                headers, null, serviceResourceParameterNames);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new ServiceException(e);
        }

        BasicSecurityKey securityKey = credent.getSecurityKey();
        String accessKey = securityKey.getAccessKey();
        String secretKey = securityKey.getSecretKey();
        String signedCanonical = AbstractAuthentication.caculateSignature(canonicalString, secretKey);
        String auth = new StringBuilder(this.getAuthPrefix()).append(" ").append(accessKey).append(":").append(signedCanonical).toString();
        return new DefaultAuthentication(canonicalString, canonicalString, auth);
    }
    
    public final String makeServiceCanonicalString(String method, String resource, Map<String, String> headersMap, String expires, List<String> serviceResourceParameterNames)
        throws UnsupportedEncodingException
    {
        StringBuilder canonicalStringBuf = new StringBuilder();
        canonicalStringBuf.append(method).append("\n");
        
        String dateHeader = Constants.CommonHeaders.DATE.toLowerCase();
        String contentTypeHeader = Constants.CommonHeaders.CONTENT_TYPE.toLowerCase();
        String contentMd5Header = Constants.CommonHeaders.CONTENT_MD5.toLowerCase();
        String headerPrefix = this.getIHeaders().headerPrefix();
        String headerMetaPrefix = this.getIHeaders().headerMetaPrefix();
        
        SortedMap<String, Object> interestingHeaders = new TreeMap<String, Object>();
        if (headersMap != null && headersMap.size() > 0)
        {
            for (Map.Entry<String, String> entry : headersMap.entrySet())
            {
                String key = entry.getKey();
                String value = entry.getValue();
                
                if (key == null)
                {
                    continue;
                }
                String lk = key.toLowerCase(Locale.getDefault());
                
                if (lk.equals(contentTypeHeader) || lk.equals(contentMd5Header) || lk.equals(dateHeader))
                {
                    interestingHeaders.put(lk, value);
                }else if(lk.startsWith(headerPrefix)) {
                	List<String> values;
                	if(interestingHeaders.containsKey(lk)) {
                		values = (List<String>)interestingHeaders.get(lk);
                	}else {
                		values = new ArrayList<String>();
                		interestingHeaders.put(lk, values);
                	}
                	values.add(value);
                }
            }
        }
        if (interestingHeaders.containsKey(this.getIHeaders().dateHeader()))
        {
            interestingHeaders.put(dateHeader, "");
        }
        
        if (expires != null)
        {
            interestingHeaders.put(dateHeader, expires);
        }
        
        if (!interestingHeaders.containsKey(contentTypeHeader))
        {
            interestingHeaders.put(contentTypeHeader, "");
        }
        if (!interestingHeaders.containsKey(contentMd5Header))
        {
            interestingHeaders.put(contentMd5Header, "");
        }
        
        for (Map.Entry<String, Object> entry : interestingHeaders.entrySet())
        {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if(value instanceof List) {
            	value = ServiceUtils.join((List<String>)value, ",", true);
            }else if(value == null) {
            	value = "";
            }
            
            if(key.startsWith(headerMetaPrefix)) {
            	canonicalStringBuf.append(key).append(':').append(value.toString().trim());
            }else if (key.startsWith(headerPrefix))
            {
                canonicalStringBuf.append(key).append(':').append(value);
            }
            else
            {
                canonicalStringBuf.append(value);
            }
            canonicalStringBuf.append("\n");
        }
        
        int queryIndex = resource.indexOf('?');
        if (queryIndex == -1)
        {
            canonicalStringBuf.append(resource);
        }
        else
        {
            canonicalStringBuf.append(resource.substring(0, queryIndex));
        }
        
        if (queryIndex >= 0)
        {
            SortedMap<String, String> sortedResourceParams = new TreeMap<String, String>();
            
            // Parse parameters from resource string
            String query = resource.substring(queryIndex + 1);
            for (String paramPair : query.split("&"))
            {
                String[] paramNameValue = paramPair.split("=");
                String name = URLDecoder.decode(paramNameValue[0], Constants.DEFAULT_ENCODING);
                String value = null;
                if (paramNameValue.length > 1)
                {
                    value = URLDecoder.decode(paramNameValue[1], Constants.DEFAULT_ENCODING);
                }
                if (serviceResourceParameterNames.contains(name.toLowerCase()) || name.toLowerCase().startsWith(headerPrefix))
                {
                    sortedResourceParams.put(name, value);
                }
            }
            
            if (sortedResourceParams.size() > 0)
            {
                canonicalStringBuf.append("?");
            }
            boolean addedParam = false;
            for (Map.Entry<String, String> entry : sortedResourceParams.entrySet())
            {
                if (addedParam)
                {
                    canonicalStringBuf.append("&");
                }
                canonicalStringBuf.append(entry.getKey());
                if (entry.getValue() != null)
                {
                    canonicalStringBuf.append("=").append(entry.getValue());
                }
                addedParam = true;
            }
        }
        
        return canonicalStringBuf.toString();
    }
}
