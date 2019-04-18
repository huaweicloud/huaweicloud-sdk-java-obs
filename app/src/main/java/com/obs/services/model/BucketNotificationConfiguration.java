package com.obs.services.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 桶的消息通知配置
 *
 */
public class BucketNotificationConfiguration extends HeaderResponse
{
    private List<TopicConfiguration> topicConfigurations;
    private List<FunctionGraphConfiguration> functionGraphConfigurations;
    
    public BucketNotificationConfiguration(){
        
    }
    
    /**
     * 新增事件通知配置
     * @param topicConfiguration 事件通知配置
     * @return 桶的消息通知配置
     */
    public BucketNotificationConfiguration addTopicConfiguration(TopicConfiguration topicConfiguration){
        this.getTopicConfigurations().add(topicConfiguration);
        return this;
    }
    
    /**
     * 新增函数工作流服务通知配置
     * @param functionGraphConfiguration 函数工作流通知配置
     * @return 桶的消息通知配置
     */
    public BucketNotificationConfiguration addFunctionGraphConfiguration(FunctionGraphConfiguration functionGraphConfiguration){
        this.getFunctionGraphConfigurations().add(functionGraphConfiguration);
        return this;
    }
    
    /**
     * 获取事件通知配置列表
     * @return 事件通知配置列表
     */
    public List<TopicConfiguration> getTopicConfigurations()
    {
        if(this.topicConfigurations == null){
            this.topicConfigurations = new ArrayList<TopicConfiguration>();
        }
        return topicConfigurations;
    }
    
    /**
     * 获取函数工作流通知配置列表
     * @return 函数工作流通知配置列表
     */
    public List<FunctionGraphConfiguration> getFunctionGraphConfigurations()
    {
        if(this.functionGraphConfigurations == null){
            this.functionGraphConfigurations = new ArrayList<FunctionGraphConfiguration>();
        }
        return functionGraphConfigurations;
    }

    /**
     * 设置事件通知配置列表
     * @param topicConfigurations 事件通知配置列表
     */
    public void setTopicConfigurations(List<TopicConfiguration> topicConfigurations)
    {
        this.topicConfigurations = topicConfigurations;
    }
    
    /**
     * 设置函数工作流通知配置列表
     * @param functionGraphConfigurations 函数工作流通知配置列表
     */
    public void setFunctionGraphConfigurations(List<FunctionGraphConfiguration> functionGraphConfigurations)
    {
        this.functionGraphConfigurations = functionGraphConfigurations;
    }

    @Override
    public String toString() {
        return "BucketNotificationConfiguration [topicConfigurations=" + topicConfigurations
                + ", functionGraphConfigurations=" + functionGraphConfigurations + "]";
    }
    
}
