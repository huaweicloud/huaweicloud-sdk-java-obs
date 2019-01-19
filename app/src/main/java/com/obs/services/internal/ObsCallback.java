package com.obs.services.internal;

interface ObsCallback<T, K extends Exception> {
	
	public void onSuccess(T Result);
	
	public void onFailure(K e);
}
