package com.obs.services.internal;

import java.util.Map;

public class InternalHeaderResponse {

	protected Map<String,Object> responseHeaders;
    
    protected int statusCode;


	protected void setResponseHeaders(Map<String,Object> responseHeaders) {
		this.responseHeaders = responseHeaders;
	}


	protected void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
    
    
}
