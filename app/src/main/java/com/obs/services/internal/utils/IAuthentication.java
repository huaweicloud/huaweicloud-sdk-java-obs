package com.obs.services.internal.utils;

public interface IAuthentication {
	String getCanonicalRequest();
	String getAuthorization();
	String getStringToSign();
}
