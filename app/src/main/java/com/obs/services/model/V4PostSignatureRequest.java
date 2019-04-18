package com.obs.services.model;

import java.util.Date;

@Deprecated
public class V4PostSignatureRequest extends PostSignatureRequest
{
    
    
    public V4PostSignatureRequest()
    {
        
    }
    
    public V4PostSignatureRequest(long expires, String bucketName, String objectKey)
    {
        super(expires, bucketName, objectKey);
    }
    
    public V4PostSignatureRequest(Date expiryDate, String bucketName, String objectKey)
    {
    	super(expiryDate, bucketName, objectKey);
    }
    
    public V4PostSignatureRequest(long expires, Date requestDate, String bucketName, String objectKey)
    {
    	super(expires, requestDate, bucketName, objectKey);
    }
    
    public V4PostSignatureRequest(Date expiryDate, Date requestDate, String bucketName, String objectKey)
    {
    	super(expiryDate,requestDate, bucketName, objectKey);
    }
    
    
}
