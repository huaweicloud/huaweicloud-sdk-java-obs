package com.obs.services.model;

import java.io.InputStream;

@Deprecated
public class S3Object
{
    
	@Deprecated
    public static final String STANDARD = "STANDARD";
    
	@Deprecated
    public static final String STANDARD_IA = "STANDARD_IA";
    
	@Deprecated
    public static final String GLACIER = "GLACIER";
    
	protected String bucketName;
    
	protected String objectKey;
    
	protected Owner owner;
    
	protected ObjectMetadata metadata;
    
	protected InputStream objectContent;
    
    public String getBucketName()
    {
        return bucketName;
    }
    
    public void setBucketName(String bucketName)
    {
        this.bucketName = bucketName;
    }
    
    public String getObjectKey()
    {
        return objectKey;
    }
    
    public void setObjectKey(String objectKey)
    {
        this.objectKey = objectKey;
    }
    
	public ObjectMetadata getMetadata() {
		if (metadata == null) {
			this.metadata = new ObjectMetadata();
		}
		return metadata;
	}
    
    public void setMetadata(ObjectMetadata metadata)
    {
        this.metadata = metadata;
    }
    
    public InputStream getObjectContent()
    {
        return objectContent;
    }
    
    public void setObjectContent(InputStream objectContent)
    {
        this.objectContent = objectContent;
    }
    
    public Owner getOwner()
    {
        return owner;
    }
    
    public void setOwner(Owner owner)
    {
        this.owner = owner;
    }

    @Override
    public String toString()
    {
        return "ObsObject [bucketName=" + bucketName + ", objectKey=" + objectKey + ", owner=" + owner + ", metadata=" + metadata
            + ", objectContent=" + objectContent + "]";
    }
}
