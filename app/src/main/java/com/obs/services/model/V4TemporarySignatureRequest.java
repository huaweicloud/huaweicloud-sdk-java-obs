package com.obs.services.model;

import java.util.Date;

@Deprecated
public class V4TemporarySignatureRequest extends TemporarySignatureRequest {

	public V4TemporarySignatureRequest() {
	}

	public V4TemporarySignatureRequest(HttpMethodEnum method, long expires) {
		super(method, null, null, null, expires);
	}

	public V4TemporarySignatureRequest(HttpMethodEnum method, String bucketName, String objectKey,
			SpecialParamEnum specialParam, long expires) {
		super(method, bucketName, objectKey, specialParam, expires, null);
	}

	public V4TemporarySignatureRequest(HttpMethodEnum method, String bucketName, String objectKey,
			SpecialParamEnum specialParam, long expires, Date requestDate) {
		super(method, bucketName, objectKey, specialParam, expires, requestDate);
	}

}
