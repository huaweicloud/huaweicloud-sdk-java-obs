package com.obs.services.internal.utils;

public class DefaultAuthentication implements IAuthentication{

    private String stringToSign;
    
    private String authorization;
    
    private String canonicalRequest;
    
	public DefaultAuthentication( String canonicalRequest, String stringToSign, String authorization) {
		this.canonicalRequest = canonicalRequest;
		this.stringToSign = stringToSign;
		this.authorization = authorization;
	}

	@Override
	public String getCanonicalRequest() {
		return this.canonicalRequest;
	}

	@Override
	public String getAuthorization() {
		return this.authorization;
	}

	@Override
	public String getStringToSign() {
		return this.stringToSign;
	}

}
