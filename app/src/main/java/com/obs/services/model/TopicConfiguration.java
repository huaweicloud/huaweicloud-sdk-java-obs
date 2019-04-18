package com.obs.services.model;

import java.util.List;

/**
 * 简单通知服务(SMN), 消息通知配置
 *
 */
public class TopicConfiguration extends AbstractNotification
{
    
    public TopicConfiguration(){
        
    }
    
    /**
     * 构造函数
     * @param id 事件通知配置ID
     * @param filter 过滤规则组
     * @param topic 事件通知主题的URN
     * @param events 需要发布通知消息的事件类型列表
     */
    public TopicConfiguration(String id, Filter filter, String topic, List<EventTypeEnum> events)
    {
        super(id, filter, events);
        this.topic = topic;
        
    }
      
    private String topic;
   

    /**
     * 获取事件通知主题的URN
     * @return 事件通知主题的URN
     */
    public String getTopic()
    {
        return topic;
    }

    /**
     * 设置事件通知主题的URN
     * @param topic 事件通知主题的URN
     */
    public void setTopic(String topic)
    {
        this.topic = topic;
    }
    

    @Override
    public String toString()
    {
        return "TopicConfiguration [id=" + id + ", topic=" + topic + ", events=" + events + ", filter=" + filter + "]";
    }
    
}
