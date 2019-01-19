package com.obs.services.model;

import java.util.Map;
import java.util.TreeMap;

import com.obs.services.internal.Constants;
import com.obs.services.internal.InternalHeaderResponse;

/**
 * 公共的响应结果，包含RequestId，响应头信息，
 *
 */
public class HeaderResponse extends InternalHeaderResponse
{
    
    public HeaderResponse(){
        
    }
    
    /**
     * 获取响应头信息
     * @return 响应头信息
     */
    public Map<String,Object> getResponseHeaders()
    {
        if(responseHeaders == null){
            responseHeaders = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
        }
        return responseHeaders;
    }
    

	/**
	 * 获取服务端返回的RequestId
	 * @return 服务端返回的RequestId
	 */
	public String getRequestId() {
		Object id = this.getResponseHeaders().get(Constants.REQUEST_ID_HEADER);
		return id == null ? "" : id.toString();
	}
	
	/**
	 * 获取服务端返回的HTTP状态码
	 * @return 服务端返回的HTTP状态码
	 */
	public int getStatusCode() {
		return statusCode;
	}

	@Override
	public String toString() {
		return "HeaderResponse [responseHeaders=" + responseHeaders + ", statusCode=" + statusCode + "]";
	}
    
}
