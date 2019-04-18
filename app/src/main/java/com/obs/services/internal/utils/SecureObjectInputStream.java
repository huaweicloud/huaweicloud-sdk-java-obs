package com.obs.services.internal.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public final class SecureObjectInputStream extends ObjectInputStream {

	public SecureObjectInputStream() throws IOException, SecurityException {
		super();
	}
	
	public SecureObjectInputStream(InputStream in) throws IOException {
		super(in);
	}

	protected Class<?> resolveClass(ObjectStreamClass desc)
	        throws IOException, ClassNotFoundException
	    {
	        String name = desc.getName();
	        //白名单校验
	        if(!name.equals("com.obs.services.internal.ResumableClient$DownloadCheckPoint") 
	        		&& !name.equals("com.obs.services.internal.ResumableClient$UploadCheckPoint")) {
	        	throw new ClassNotFoundException(name + "not find");
	        }
	        return super.resolveClass(desc);
	    }
}
