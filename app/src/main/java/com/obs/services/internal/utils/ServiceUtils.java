/**
 * 
 * JetS3t : Java S3 Toolkit
 * Project hosted at http://bitbucket.org/jmurty/jets3t/
 *
 * Copyright 2006-2010 James Murty
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.internal.Constants;
import com.obs.services.internal.ObsConstraint;
import com.obs.services.internal.ObsProperties;
import com.obs.services.internal.ServiceException;
import com.obs.services.model.AuthTypeEnum;
import com.obs.services.model.HttpProtocolTypeEnum;

import okhttp3.Headers;

public class ServiceUtils
{
	private static final ILogger log = LoggerBuilder.getLogger(ServiceUtils.class);

	protected static final String iso8601DateParserString = Constants.EXPIRATION_DATE_FORMATTER;
	protected static final String iso8601DateMidnightParserString = "yyyy-MM-dd'T'00:00:00'Z'";
	protected static final String iso8601DateParser_WalrusString = "yyyy-MM-dd'T'HH:mm:ss";
	protected static final String rfc822DateParserString = Constants.HEADER_DATE_FORMATTER;
	protected static final String _iso8601DateParserString = "yyyy-MM-dd";
	
	private static Pattern pattern = Pattern.compile("^((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)$");
	
	
	public static boolean isValid(String s) {
		return s != null && !s.trim().equals("");
	}
	
	public static boolean isValid2(String s) {
		return s != null && !s.equals("");
	}
	
	public static String toValid(String s) {
		return s == null ? "" : s;
	}
	
	public static void asserParameterNotNull(String value, String errorMessage) {
		if (!isValid(value)) {
			throw new IllegalArgumentException(errorMessage);
		}
	}

	public static void asserParameterNotNull2(String value, String errorMessage) {
		if (value == null) {
			throw new IllegalArgumentException(errorMessage);
		}
	}

	public static void asserParameterNotNull(Object value, String errorMessage) {
		if (value == null) {
			throw new IllegalArgumentException(errorMessage);
		}
	}
	
	public static Date parseIso8601Date(String dateString) throws ParseException
	{
	    ParseException exception = null;
        SimpleDateFormat iso8601DateParser = new SimpleDateFormat(iso8601DateParserString);
        TimeZone gmt = Constants.GMT_TIMEZONE;
        iso8601DateParser.setTimeZone(gmt);
        try
        {
            return iso8601DateParser.parse(dateString);
        }
        catch (ParseException e)
        {
            exception = e;
        }
        SimpleDateFormat iso8601DateParser_Walrus = new SimpleDateFormat(iso8601DateParser_WalrusString);
        iso8601DateParser_Walrus.setTimeZone(gmt);
        try
        {
            return iso8601DateParser_Walrus.parse(dateString);
        }
        catch (ParseException e)
        {
            
            exception = e;
        }
        // Throw original exception if the Walrus work-around doesn't save us.
        
        SimpleDateFormat _iso8601DateParser = new SimpleDateFormat(_iso8601DateParserString);
        _iso8601DateParser.setTimeZone(gmt);
        try{
            return _iso8601DateParser.parse(dateString);
        }catch (Exception e) {
        }
        throw exception;
	}

	public static String formatIso8601Date(Date date)
	{
	    SimpleDateFormat iso8601DateParser = new SimpleDateFormat(iso8601DateParserString);
        iso8601DateParser.setTimeZone(Constants.GMT_TIMEZONE);
		return iso8601DateParser.format(date);
	}
	
	public static String formatIso8601MidnightDate(Date date){
	    SimpleDateFormat iso8601DateParser = new SimpleDateFormat(iso8601DateMidnightParserString);
        iso8601DateParser.setTimeZone(Constants.GMT_TIMEZONE);
        return iso8601DateParser.format(date);
	}

	public static Date parseRfc822Date(String dateString) throws ParseException
	{
	    SimpleDateFormat rfc822DateParser = new SimpleDateFormat(rfc822DateParserString, Locale.US);
	    rfc822DateParser.setTimeZone(Constants.GMT_TIMEZONE);
		return rfc822DateParser.parse(dateString);
	}

	public static String formatRfc822Date(Date date)
	{
	    SimpleDateFormat rfc822DateParser = new SimpleDateFormat(rfc822DateParserString, Locale.US);
        rfc822DateParser.setTimeZone(Constants.GMT_TIMEZONE);
		return rfc822DateParser.format(date);
	}

	public static String signWithHmacSha1(String sk, String canonicalString) throws ServiceException
	{
		SecretKeySpec signingKey = null;
		try
		{
			signingKey = new SecretKeySpec(sk.getBytes(Constants.DEFAULT_ENCODING), Constants.HMAC_SHA1_ALGORITHM);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new ServiceException("Unable to get bytes from secret string", e);
		}

		// Acquire the MAC instance and initialize with the signing key.
		Mac mac = null;
		try
		{
			mac = Mac.getInstance(Constants.HMAC_SHA1_ALGORITHM);
		}
		catch (NoSuchAlgorithmException e)
		{
			// should not happen
			throw new ServiceException("Could not find sha1 algorithm", e);
		}
		try
		{
			mac.init(signingKey);
		}
		catch (InvalidKeyException e)
		{
			// also should not happen
			throw new RuntimeException("Could not initialize the MAC algorithm", e);
		}

		// Compute the HMAC on the digest, and set it.
		try
		{
			return ServiceUtils.toBase64(mac.doFinal(canonicalString.getBytes(Constants.DEFAULT_ENCODING)));
		}
		catch (UnsupportedEncodingException e)
		{
			throw new ServiceException("Unable to get bytes from canonical string", e);
		}
	}


	public static Map<String, Object> cleanRestMetadataMap(Map<String, List<String>> metadata, String headerPrefix, String metadataPrefix)
	{
		if (log.isDebugEnabled())
		{
			log.debug("Cleaning up REST metadata items");
		}
		Map<String, Object> cleanMap = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
		if (metadata != null)
		{
			for (Map.Entry<String, List<String>> entry : metadata.entrySet())
			{
				String key = entry.getKey();
				List<String> values = entry.getValue();
				
				if(key == null || values == null) {
					continue;
				}
				
				Object value = values.size() == 1 ? values.get(0) : values;
				if ((Constants.CommonHeaders.DATE.equalsIgnoreCase(key) || Constants.CommonHeaders.LAST_MODIFIED.equalsIgnoreCase(key)))
				{
					if (log.isDebugEnabled())
					{
						log.debug("Parsing date string '" + value + "' into Date object for key: " + key);
					}
					try
					{
						value = ServiceUtils.parseRfc822Date(value.toString());
					}
					catch (ParseException pe)
					{
						// Try ISO-8601 date format, just in case
						try
						{
							value = ServiceUtils.parseIso8601Date(value.toString());
						}
						catch (ParseException pe2)
						{
							// Log original exception if the work-around fails.
							if (log.isWarnEnabled())
							{
								log.warn("Date string is not RFC 822 compliant for metadata field " + key, pe);
							}
						}
					}
				}
				else if (key.toLowerCase().startsWith(headerPrefix))
				{
					try
                    {	
						if (key.toLowerCase().startsWith(metadataPrefix)){
							key = key.substring(metadataPrefix.length(), key.length());
							key = URLDecoder.decode(key, Constants.DEFAULT_ENCODING);
							if (log.isDebugEnabled())
							{
								log.debug("Removed meatadata header prefix " + metadataPrefix + " from key: " + key + "=>" + key);
							}
						}else {
							key = key.substring(headerPrefix.length(), key.length());
						}
						if(value instanceof List) {
							List<String> _values = new ArrayList<String>(values.size());
							for(String _value : values) {
								_values.add(_value != null ? URLDecoder.decode(_value, Constants.DEFAULT_ENCODING) : null);
							}
							value = _values;
						}else {
							value = URLDecoder.decode(value.toString(), Constants.DEFAULT_ENCODING);
						}
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug("Error to decode value of key:" + key);
                        }
                    }
				}
				else if (key.toLowerCase().startsWith(Constants.OBS_HEADER_PREFIX))
				{
					try
                    {	
						if (key.toLowerCase().startsWith(Constants.OBS_HEADER_META_PREFIX)){
							key = key.substring(Constants.OBS_HEADER_META_PREFIX.length(), key.length());
							key = URLDecoder.decode(key, Constants.DEFAULT_ENCODING);
							if (log.isDebugEnabled())
							{
								log.debug("Removed meatadata header prefix " + Constants.OBS_HEADER_META_PREFIX + " from key: " + key + "=>" + key);
							}
						}else {
							key = key.substring(Constants.OBS_HEADER_PREFIX.length(), key.length());
						}
						if(value instanceof List) {
							List<String> _values = new ArrayList<String>(values.size());
							for(String _value : values) {
								_values.add(_value != null ? URLDecoder.decode(_value, Constants.DEFAULT_ENCODING) : null);
							}
							value = _values;
						}else {
							value = URLDecoder.decode(value.toString(), Constants.DEFAULT_ENCODING);
						}
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug("Error to decode value of key:" + key);
                        }
                    }
				}
				else if (Constants.ALLOWED_RESPONSE_HTTP_HEADER_METADATA_NAMES.contains(key.toLowerCase(Locale.getDefault())))
				{
					if (log.isDebugEnabled())
					{
						log.debug("Leaving HTTP header item unchanged: " + key + "=" + values);
					}
				}
				else
				{
					if (log.isDebugEnabled())
					{
						log.debug("Ignoring metadata item: " + key + "=" + values);
					}
					continue;
				}
				cleanMap.put(key, value);
			}
		}
		return cleanMap;
	}
	
	public static Map<String, String> cleanRestMetadataMapV2(Map<String, String> metadata, String headerPrefix, String metadataPrefix)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Cleaning up REST metadata items");
        }
        Map<String, String> cleanMap = new IdentityHashMap<String, String>();
        if (metadata != null)
        {
            for (Map.Entry<String, String> entry : metadata.entrySet())
            {
                String key = entry.getKey();
                String value = entry.getValue();

                // Trim prefixes from keys.
                String keyStr = key != null ? key.toString() : "";
                if (keyStr.toLowerCase().startsWith(headerPrefix))
                {
                    try
                    {	
                    	if (key.toLowerCase().startsWith(metadataPrefix)){
    						key = key.substring(metadataPrefix.length(), key.length());
    						key = URLDecoder.decode(key, Constants.DEFAULT_ENCODING);
    						if (log.isDebugEnabled())
    						{
    							log.debug("Removed meatadata header prefix " + metadataPrefix + " from key: " + key + "=>" + key);
    						}
    					}else {
    						key = key.substring(headerPrefix.length(), key.length());
    					}
						value = URLDecoder.decode(value.toString(), Constants.DEFAULT_ENCODING);
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug("Error to decode value of key:" + key);
                        }
                    }
                    
                }
				else if (key.toLowerCase().startsWith(Constants.OBS_HEADER_PREFIX))
				{
					try
                    {	
						if (key.toLowerCase().startsWith(Constants.OBS_HEADER_META_PREFIX)){
							key = key.substring(Constants.OBS_HEADER_META_PREFIX.length(), key.length());
							key = URLDecoder.decode(key, Constants.DEFAULT_ENCODING);
							if (log.isDebugEnabled())
							{
								log.debug("Removed meatadata header prefix " + Constants.OBS_HEADER_META_PREFIX + " from key: " + key + "=>" + key);
							}
						}else {
							key = key.substring(Constants.OBS_HEADER_PREFIX.length(), key.length());
						}
						value = URLDecoder.decode(value.toString(), Constants.DEFAULT_ENCODING);
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug("Error to decode value of key:" + key);
                        }
                    }
				}
                else if (Constants.ALLOWED_RESPONSE_HTTP_HEADER_METADATA_NAMES.contains(keyStr.toLowerCase(Locale.getDefault())))
                {
                    key = keyStr;
                    if (log.isDebugEnabled())
                    {
                        log.debug("Leaving HTTP header item unchanged: " + key + "=" + value);
                    }
                }
                else
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("Ignoring metadata item: " + keyStr + "=" + value);
                    }
                    continue;
                }

                cleanMap.put(new String(key), value);
            }
        }
        return cleanMap;
    }

	public static String toHex(byte[] data)
	{
		StringBuilder sb = new StringBuilder(data.length * 2);
		for (int i = 0; i < data.length; i++)
		{
			String hex = Integer.toHexString(data[i]);
			if (hex.length() == 1)
			{
				// Append leading zero.
				sb.append("0");
			}
			else if (hex.length() == 8)
			{
				// Remove ff prefix from negative numbers.
				hex = hex.substring(6);
			}
			sb.append(hex);
		}
		return sb.toString().toLowerCase(Locale.getDefault());
	}

	public static byte[] fromHex(String hexData)
	{
		if ((hexData.length() & 1) != 0 || hexData.replaceAll("[a-fA-F0-9]", "").length() > 0)
		{
			throw new java.lang.IllegalArgumentException("'" + hexData + "' is not a hex string");
		}

		byte[] result = new byte[(hexData.length() + 1) / 2];
		String hexNumber = null;
		int stringOffset = 0;
		int byteOffset = 0;
		while (stringOffset < hexData.length())
		{
			hexNumber = hexData.substring(stringOffset, stringOffset + 2);
			stringOffset += 2;
			result[byteOffset++] = (byte) Integer.parseInt(hexNumber, 16);
		}
		return result;
	}

	public static String toBase64(byte[] data)
	{
	    return ReflectUtils.toBase64(data);
	}

	public static String join(Object[] items, String delimiter)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < items.length; i++)
		{
			sb.append(items[i]);
			if (i < items.length - 1)
			{
				sb.append(delimiter);
			}
		}
		return sb.toString();
	}
	
	public static String join(List<?> items, String delimiter, boolean needTrim)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++)
        {
        	String item = items.get(i).toString();
            sb.append(needTrim ? item.trim() : item);
            if (i < items.size() - 1)
            {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }
	
   public static String join(List<?> items, String delimiter)
   {
	   return join(items, delimiter, false);
   }
	
   public static String join(Headers headers, String delimiter, List<String> excludes)
    {
        if(excludes == null){
            excludes = new ArrayList<String>();
        }
        StringBuilder sb = new StringBuilder();
        Map<String, List<String>> map = headers.toMultimap();
        int i = 0;
        for (Map.Entry<String, List<String>> entry : map.entrySet())
        {
            if(!excludes.contains(entry.getKey())){
                sb.append(entry.getValue());
            }
            if (i < map.size() - 1)
            {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }
	

	public static String join(int[] ints, String delimiter)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ints.length; i++)
		{
			sb.append(ints[i]);
			if (i < ints.length - 1)
			{
				sb.append(delimiter);
			}
		}
		return sb.toString();
	}

	public static byte[] fromBase64(String b64Data) throws UnsupportedEncodingException
	{
	    return ReflectUtils.fromBase64(b64Data);
	}

	public static byte[] computeMD5Hash(InputStream is) throws NoSuchAlgorithmException, IOException
	{
		BufferedInputStream bis = new BufferedInputStream(is);
		try
		{
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[16384];
			int bytesRead = -1;
			while ((bytesRead = bis.read(buffer, 0, buffer.length)) != -1)
			{
				messageDigest.update(buffer, 0, bytesRead);
			}
			return messageDigest.digest();
		}
		finally
		{
			closeStream(bis);
		}
	}

	public static byte[] computeMD5Hash(InputStream is, long length, long offset) throws NoSuchAlgorithmException, IOException
	{
		BufferedInputStream bis = null;
		try
		{
		    bis = new BufferedInputStream(is);
		    if(offset > 0){
		        long skipByte = bis.skip(offset);
		        if(log.isDebugEnabled()){
		            log.debug("computeMD5Hash: Skip " + skipByte + " bytes");
		        }
		    }
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[16384];
			int bytesRead = -1;
			long readLen = 0;
			long bufLen = 16384 > length ? length : 16384;
			while (readLen < length && (bytesRead = bis.read(buffer, 0, (int) bufLen)) != -1)
			{
				messageDigest.update(buffer, 0, bytesRead);
				readLen += bytesRead;
				bufLen = (length - readLen) > 16384 ? 16384 : (length - readLen);
			}
			return messageDigest.digest();
		}
		finally
		{
			closeStream(bis);
		}
	}
	
	public static String computeMD5(String data) throws ServiceException
    {
        try
        {
            return ServiceUtils.toBase64(ServiceUtils.computeMD5Hash(data.getBytes(Constants.DEFAULT_ENCODING)));
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new ServiceException("Failed to get MD5 for requestXmlElement:" + data);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new ServiceException("Failed to get MD5 for requestXmlElement:" + data);
        }
        catch (IOException e)
        {
            throw new ServiceException("Failed to get MD5 for requestXmlElement:" + data);
        }
    }

	public static byte[] computeMD5Hash(byte[] data) throws NoSuchAlgorithmException, IOException
	{
		return computeMD5Hash(new ByteArrayInputStream(data));
	}

	public static boolean isBucketNameValidDNSName(String bucketName)
	{
		if (bucketName == null || bucketName.length() > 63 || bucketName.length() < 3)
		{
			return false;
		}

		// Only lower-case letters, numbers, '.' or '-' characters allowed
		if (!Pattern.matches("^[a-z0-9][a-z0-9.-]+$", bucketName))
		{
			return false;
		}

		// Cannot be an IP address, i.e. must not contain four '.'-delimited
		// sections with 1 to 3 digits each.
		if (Pattern.matches("([0-9]{1,3}\\.){3}[0-9]{1,3}", bucketName))
		{
			return false;
		}

		// Components of name between '.' characters cannot start or end with '-',
		// and cannot be empty
		String[] fragments = bucketName.split("\\.");
		for (int i = 0; i < fragments.length; i++)
		{
			if (Pattern.matches("^-.*", fragments[i]) || Pattern.matches(".*-$", fragments[i]) || Pattern.matches("^$", fragments[i]))
			{
				return false;
			}
		}

		return true;
	}

	public static String generateHostnameForBucket(String bucketName, boolean pathStyle, String endpoint)
	{
		if(!isBucketNameValidDNSName(bucketName))
		{
			throw new IllegalArgumentException("the bucketName is illegal");
		}
		
		if (!pathStyle)
		{
			return bucketName + "." + endpoint;
		}
		else
		{
			return endpoint;
		}
	}

	public static XMLReader loadXMLReader() throws ServiceException
	{
		Exception ex;
		try
		{
			return XMLReaderFactory.createXMLReader();
		}
		catch (Exception e)
		{
			// Ignore failure
			ex = e;
		}

		// No dice using the standard approach, try loading alternatives...
		String[] altXmlReaderClasspaths = new String[] { "org.apache.crimson.parser.XMLReaderImpl", // JDK 1.4
		        "org.xmlpull.v1.sax2.Driver", // Android
		};
		for (int i = 0; i < altXmlReaderClasspaths.length; i++)
		{
			String xmlReaderClasspath = altXmlReaderClasspaths[i];
			try
			{
				return XMLReaderFactory.createXMLReader(xmlReaderClasspath);
			}
			catch (Exception e)
			{
				// Ignore failure
			}
		}
		// If we haven't found and returned an XMLReader yet, give up.
		throw new ServiceException("Failed to initialize a SAX XMLReader", ex);
	}

    public static SimpleDateFormat getShortDateFormat()
    {
        SimpleDateFormat format = new SimpleDateFormat(Constants.SHORT_DATE_FORMATTER);
        format.setTimeZone(Constants.GMT_TIMEZONE);
        return format;
    }
    
    public static SimpleDateFormat getLongDateFormat()
    {
        SimpleDateFormat format = new SimpleDateFormat(Constants.LONG_DATE_FORMATTER);
        format.setTimeZone(Constants.GMT_TIMEZONE);
        return format;
    }
    
    public static SimpleDateFormat getHeaderDateFormat()
    {
        SimpleDateFormat format = new SimpleDateFormat(Constants.HEADER_DATE_FORMATTER);
        format.setTimeZone(Constants.GMT_TIMEZONE);
        return format;
    }
    
    public static SimpleDateFormat getExpirationDateFormat()
    {
        SimpleDateFormat format = new SimpleDateFormat(Constants.EXPIRATION_DATE_FORMATTER);
        format.setTimeZone(Constants.GMT_TIMEZONE);
        return format;
    }
    
    public static ObsException changeFromServiceException(ServiceException se)
    {
        ObsException exception;
        if (se.getResponseCode() < 0)
        {
            exception = new ObsException("OBS servcie Error Message. " + se.getMessage(), se.getCause());
        }
        else
        {
            exception = new ObsException((se.getMessage() != null ? "Error message:" + se.getMessage() : "") + "OBS servcie Error Message.", se.getXmlMessage(), se.getCause());
            exception.setErrorCode(se.getErrorCode());
            exception.setErrorMessage(se.getErrorMessage() == null ? se.getMessage() : se.getErrorMessage());
            exception.setErrorRequestId(se.getErrorRequestId());
            exception.setErrorHostId(se.getErrorHostId());
            exception.setResponseCode(se.getResponseCode());
            exception.setResponseStatus(se.getResponseStatus());
            exception.setResponseHeaders(se.getResponseHeaders());
            exception.setErrorIndicator(se.getErrorIndicator());
        }
        return exception;
    }
    
    public static void closeStream(Closeable closeable) {
    	if(closeable != null) {
    		try {
				closeable.close();
			} catch (IOException e) {
				if(log.isWarnEnabled()) {
					log.warn(e);
				}
			}
    	}
    }
    
	public static String toString(InputStream in) throws IOException{
	    String ret = null;
	    if(in != null){
	        StringBuilder sb = new StringBuilder();
	        BufferedReader br = null;
	        try{
	            br = new BufferedReader(new InputStreamReader(in, Constants.DEFAULT_ENCODING));
	            String temp;
	            while((temp=br.readLine())!=null){
	                sb.append(temp);
	            }
	            ret = sb.toString();
	        }finally{
	            closeStream(br);
	            closeStream(in);
	        }
	    }
	    return ret;
	}
    
    public static ObsProperties changeFromObsConfiguration(ObsConfiguration config)
    {
        ObsProperties obsProperties = new ObsProperties();
        
        String endPoint = config.getEndPoint();
        
        int index;
        while((index = endPoint.lastIndexOf("/")) == endPoint.length()-1) {
        	endPoint = endPoint.substring(0, index);
        }
        
        if(endPoint.startsWith("http://")) {
        	config.setHttpsOnly(false);
        	endPoint = endPoint.substring("http://".length());
        }else if(endPoint.startsWith("https://")) {
    		config.setHttpsOnly(true);
    		endPoint = endPoint.substring("https://".length());
    	}
        
        if((index = endPoint.lastIndexOf(":")) > 0) {
        	int port = Integer.parseInt(endPoint.substring(index + 1));
        	if(config.isHttpsOnly()) {
        		config.setEndpointHttpsPort(port);
        	}else {
        		config.setEndpointHttpPort(port);
        	}
        	endPoint = endPoint.substring(0, index);
        }
        
        Matcher m = pattern.matcher(endPoint);
        if(m.matches()) {
        	config.setPathStyle(true);
        }
        
        if(config.isPathStyle() || config.isCname()) {
        	config.setAuthTypeNegotiation(false);
        	if(config.getAuthType() == AuthTypeEnum.OBS) {
        		config.setAuthType(AuthTypeEnum.V2);
        	}
        }
        
        config.setEndPoint(endPoint);
        obsProperties.setProperty(ObsConstraint.END_POINT, config.getEndPoint());
        obsProperties.setProperty(ObsConstraint.HTTP_PORT, String.valueOf(config.getEndpointHttpPort()));
        obsProperties.setProperty(ObsConstraint.HTTPS_ONLY, String.valueOf(config.isHttpsOnly()));
        obsProperties.setProperty(ObsConstraint.DISABLE_DNS_BUCKET, String.valueOf(config.isPathStyle()));
        obsProperties.setProperty(ObsConstraint.HTTPS_PORT, String.valueOf(config.getEndpointHttpsPort()));
        obsProperties.setProperty(ObsConstraint.HTTP_SOCKET_TIMEOUT, String.valueOf(config.getSocketTimeout()));
        obsProperties.setProperty(ObsConstraint.HTTP_MAX_CONNECT, String.valueOf(config.getMaxConnections()));
        obsProperties.setProperty(ObsConstraint.HTTP_RETRY_MAX, String.valueOf(config.getMaxErrorRetry()));
        obsProperties.setProperty(ObsConstraint.HTTP_CONNECT_TIMEOUT, String.valueOf(config.getConnectionTimeout()));
        obsProperties.setProperty(ObsConstraint.PROXY_ISABLE, String.valueOf(Boolean.FALSE));
        obsProperties.setProperty(ObsConstraint.BUFFER_STREAM, String.valueOf(config.getUploadStreamRetryBufferSize() > 0 ? config.getUploadStreamRetryBufferSize(): ObsConstraint.DEFAULT_BUFFER_STREAM));
        obsProperties.setProperty(ObsConstraint.VALIDATE_CERTIFICATE, String.valueOf(config.isValidateCertificate()));
        obsProperties.setProperty(ObsConstraint.VERIFY_RESPONSE_CONTENT_TYPE, String.valueOf(config.isVerifyResponseContentType()));
        obsProperties.setProperty(ObsConstraint.WRITE_BUFFER_SIZE, String.valueOf(config.getWriteBufferSize()));
        obsProperties.setProperty(ObsConstraint.READ_BUFFER_SIZE, String.valueOf(config.getReadBufferSize()));
		obsProperties.setProperty(ObsConstraint.SOCKET_WRITE_BUFFER_SIZE,
				String.valueOf(config.getSocketWriteBufferSize()));
		obsProperties.setProperty(ObsConstraint.SOCKET_READ_BUFFER_SIZE,
				String.valueOf(config.getSocketReadBufferSize()));
        obsProperties.setProperty(ObsConstraint.HTTP_STRICT_HOSTNAME_VERIFICATION, String.valueOf(config.isStrictHostnameVerification()));
        obsProperties.setProperty(ObsConstraint.HTTP_IDLE_CONNECTION_TIME,
				String.valueOf(config.getIdleConnectionTime()));
        obsProperties.setProperty(ObsConstraint.HTTP_MAX_IDLE_CONNECTIONS,
				String.valueOf(config.getMaxIdleConnections()));
        obsProperties.setProperty(ObsConstraint.SSL_PROVIDER, config.getSslProvider() == null ? "": config.getSslProvider());
        obsProperties.setProperty(ObsConstraint.KEEP_ALIVE, String.valueOf(config.isKeepAlive()));
        obsProperties.setProperty(ObsConstraint.FS_DELIMITER, config.getDelimiter() == null ? "/" : config.getDelimiter());
        obsProperties.setProperty(ObsConstraint.HTTP_PROTOCOL, config.getHttpProtocolType() == null ? HttpProtocolTypeEnum.HTTP1_1.getCode() : config.getHttpProtocolType().getCode());
        
        obsProperties.setProperty(ObsConstraint.IS_CNAME, String.valueOf(config.isCname()));
        obsProperties.setProperty(ObsConstraint.AUTH_TYPE_NEGOTIATION, String.valueOf(config.isAuthTypeNegotiation()));
        if (null != config.getHttpProxy())
        {
            obsProperties.setProperty(ObsConstraint.PROXY_ISABLE, String.valueOf(Boolean.TRUE));
            obsProperties.setProperty(ObsConstraint.PROXY_HOST, config.getHttpProxy().getProxyAddr());
            obsProperties.setProperty(ObsConstraint.PROXY_PORT, String.valueOf(config.getHttpProxy().getProxyPort()));
            obsProperties.setProperty(ObsConstraint.PROXY_UNAME, config.getHttpProxy().getProxyUName());
            obsProperties.setProperty(ObsConstraint.PROXY_PAWD, config.getHttpProxy().getUserPaaswd());
            obsProperties.setProperty(ObsConstraint.PROXY_DOMAIN, config.getHttpProxy().getDomain());
            obsProperties.setProperty(ObsConstraint.PROXY_WORKSTATION, config.getHttpProxy().getWorkstation());
        }
        
        return obsProperties;
    }
}
