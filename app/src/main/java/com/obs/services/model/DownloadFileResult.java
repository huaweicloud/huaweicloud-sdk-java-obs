package com.obs.services.model;

import com.obs.services.model.ObjectMetadata;

/**
 * 下载文件的响应结果
 */
public class DownloadFileResult {
	/**
     * 获取对象的属性
     * 
     * @return 对象的属性
     */
    public ObjectMetadata getObjectMetadata() {
        return objectMetadata;
    }

    /**
     * 设置对象的属性
     * 
     * @param objectMetadata 对象的属性
     */
    public void setObjectMetadata(ObjectMetadata objectMetadata) {
        this.objectMetadata = objectMetadata;
    }
    
    private ObjectMetadata objectMetadata;

    @Override
    public String toString()
    {
        return "DownloadFileResult [objectMetadata=" + objectMetadata + "]";
    }
    
}