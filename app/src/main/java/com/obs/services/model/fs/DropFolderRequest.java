package com.obs.services.model.fs;

import com.obs.services.model.AbstractBulkRequest;
import com.obs.services.model.DeleteObjectResult;
import com.obs.services.model.TaskCallback;

/**
 * 删除文件夹请求参数 同时删除文件夹下的文件和子文件夹
 * 暂不支持多版本桶
 */
public class DropFolderRequest extends AbstractBulkRequest {

    private String folderName;

    private TaskCallback<DeleteObjectResult, String> callback;

    public DropFolderRequest() {
    }

    /**
     * 构造函数
     * 
     * @param bucketName 桶名
     */
    public DropFolderRequest(String bucketName) {
        super(bucketName);
    }

    public DropFolderRequest(String bucketName, String folderName) {
        super(bucketName);
        this.folderName = folderName;
    }

    /**
     * 获取目录名
     * 
     * @return 目录名
     */
    public String getFolderName() {
        return folderName;
    }

    /**
     * 设置目录名
     * 
     * @param folderName 目录名
     */
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    /**
     * 获取批量任务的回调对象
     * 
     * @return 回调对象
     */
    public TaskCallback<DeleteObjectResult, String> getCallback() {
        return callback;
    }

    /**
     * 设置批量任务的回调对象
     * 
     * @param callback 回调对象
     */
    public void setCallback(TaskCallback<DeleteObjectResult, String> callback) {
        this.callback = callback;
    }

    @Override
    public String toString() {
        return "DropFolderRequest [bucketName=" + bucketName + ", folderName=" + folderName + "]";
    }
}
