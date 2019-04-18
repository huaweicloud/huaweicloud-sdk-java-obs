package com.obs.services.model;

import java.io.InputStream;

/**
 * OBS中的对象
 */
public class ObsObject extends S3Object{

	/**
	 * 获取对象所属的桶
	 * 
	 * @return 对象所属的桶
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * 设置对象所属的桶
	 * 
	 * @param bucketName
	 *            对象所属的桶
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * 获取对象名
	 * 
	 * @return 对象名
	 */
	public String getObjectKey() {
		return objectKey;
	}

	/**
	 * 设置对象名
	 * 
	 * @param objectKey
	 *            对象名
	 */
	public void setObjectKey(String objectKey) {
		this.objectKey = objectKey;
	}

	/**
	 * 获取对象的属性，包括content-type，content-length，自定义元数据等
	 * 
	 * @return 对象的属性
	 */
	public ObjectMetadata getMetadata() {
		if (metadata == null) {
			this.metadata = new ObjectMetadata();
		}
		return metadata;
	}

	/**
	 * 设置对象的属性，包括content-type，content-length，自定义元数据等
	 * 
	 * @param metadata
	 *            对象的属性
	 */
	public void setMetadata(ObjectMetadata metadata) {
		this.metadata = metadata;
	}

	/**
	 * 获取对象的数据流
	 * 
	 * @return 对象的数据流
	 */
	public InputStream getObjectContent() {
		return objectContent;
	}

	/**
	 * 设置对象的数据流
	 * 
	 * @param objectContent
	 *            对象的数据流
	 */
	public void setObjectContent(InputStream objectContent) {
		this.objectContent = objectContent;
	}

	/**
	 * 获取对象的所有者
	 * 
	 * @return 对象的所有者
	 */
	public Owner getOwner() {
		return owner;
	}

	/**
	 * 设置对象的所有者
	 * 
	 * @param owner
	 *            对象的所有者
	 */
	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		return "ObsObject [bucketName=" + bucketName + ", objectKey=" + objectKey + ", owner=" + owner + ", metadata="
				+ metadata + ", objectContent=" + objectContent + "]";
	}
}
