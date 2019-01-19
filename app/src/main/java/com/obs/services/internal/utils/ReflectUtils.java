package com.obs.services.internal.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;

public class ReflectUtils {

	private static Class<?> androidBase64Class;
	
	private static Class<?> jdkBase64EncoderClass;
	
	private static Class<?> jdkBase64DecoderClass;
	
	private static Map<String, Field> fields = new ConcurrentHashMap<String, Field>();
	
	static{
	    try
        {
            androidBase64Class = Class.forName("android.util.Base64");
        }
        catch (ClassNotFoundException e)
        {
        }
	    
	    try{
	        jdkBase64EncoderClass = Class.forName("sun.misc.BASE64Encoder");
	    }catch (ClassNotFoundException e) {
        }
	    
	    try{
	        jdkBase64DecoderClass = Class.forName("sun.misc.BASE64Decoder");
	    }catch (ClassNotFoundException e) {
        }
	    
	}
	
	public static String toBase64(byte[] data)
	{
	    if(androidBase64Class != null){
	        try{
	            Method m = androidBase64Class.getMethod("encode", byte[].class, int.class);
	            return new String((byte[])m.invoke(null, data, 2));
	        }catch (Exception e) {
            }
	    }
	    
	    if(jdkBase64EncoderClass != null){
	        try{
	            Method m = jdkBase64EncoderClass.getMethod("encode", byte[].class);
	            String temp = (String)m.invoke(jdkBase64EncoderClass.newInstance(), data);
	            return temp.replaceAll("\\s", "");
	        }catch (Exception e) {
            }
	    }
		return null;
	}
	
	public static byte[] fromBase64(String b64Data) throws UnsupportedEncodingException
	{
	    if(androidBase64Class != null){
	        try{
	            Method m = androidBase64Class.getMethod("decode", byte[].class, int.class);
	            return (byte[])m.invoke(null, b64Data.getBytes("UTF-8"), 2);
	        }catch (Exception e) {
            }
	    }
	    
	    if(jdkBase64DecoderClass != null){
	        try{
                Method m = jdkBase64DecoderClass.getMethod("decodeBuffer", String.class);
                return (byte[])m.invoke(jdkBase64DecoderClass.newInstance(), b64Data);
            }catch (Exception e) {
            }
	    }
		return null;
	}
	
	
	public static void setInnerClient(Object obj, ObsClient obsClient) {
		if(obj != null && obsClient != null) {
			Class<?> clazz = obj.getClass();
			String name = clazz.getName();
			Field f = fields.get(name);
			try {
				if(f == null) {
					f = getFieldFromClass(clazz, "innerClient");
					f.setAccessible(true);
					fields.put(name, f);
				}
				f.set(obj, obsClient);
			} catch (Exception e) {
				throw new ObsException(e.getMessage(), e);
			} 
		}
	}

	private static Field getFieldFromClass(Class<?> clazz, String key) {
		do {
			try {
				return clazz.getDeclaredField(key);
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			} 
		}while(clazz != null);
		return null;
	}
	
}
