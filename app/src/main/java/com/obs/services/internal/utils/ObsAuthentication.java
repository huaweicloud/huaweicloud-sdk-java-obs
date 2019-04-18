package com.obs.services.internal.utils;

import com.obs.services.internal.IHeaders;
import com.obs.services.internal.ObsHeaders;

public class ObsAuthentication extends AbstractAuthentication
{

	private static ObsAuthentication instance = new ObsAuthentication();
	
	private ObsAuthentication() {
		
	}
	
	public static AbstractAuthentication getInstance() {
		return instance;
	}
	@Override
	protected IHeaders getIHeaders() {
		return ObsHeaders.getInstance();
	}

	@Override
	protected String getAuthPrefix() {
		return "OBS";
	}
    
}
