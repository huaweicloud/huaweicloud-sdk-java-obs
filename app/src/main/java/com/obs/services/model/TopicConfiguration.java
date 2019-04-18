package com.obs.services.model;

import java.util.ArrayList;
import java.util.List;

import com.obs.services.internal.ObsConvertor;

/**
 * 事件通知配置
 *
 */
public class TopicConfiguration extends HeaderResponse
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
        this.id = id;
        this.filter = filter;
        this.topic = topic;
        this.events = events;
    }

    private String id;
    
    private Filter filter;
    
    private String topic;
    
    private List<EventTypeEnum> events;
    
    /**
     * 表示事件通知配置的过滤规则列表
     *
     */
    public static class Filter{
        
        private List<FilterRule> filterRules;
        
        /**
         * 表示事件通知配置的过滤规则 
         *
         */
        public static class FilterRule{
            
            private String name;
            
            private String value;

            public FilterRule(){
                
            }
            
            /**
             * 构造函数
             * @param name 指定过滤规则按对象名的前缀或后缀进行过滤
             * @param value 规律规则中对象名关键字
             */
            public FilterRule(String name, String value){
                this.name = name;
                this.value = value;
            }
            
            /**
             * 获取按对象名的前缀或后缀进行过滤标识
             * @return 按对象名前缀或后缀进行过滤标识
             */
            public String getName()
            {
                return name;
            }

            /**
             * 设置按对象名的前缀或后缀进行过滤标识
             * @param name 按对象名前缀或后缀进行过滤标识
             */
            public void setName(String name)
            {
                this.name = name;
            }

            /**
             * 获取对象名关键字 
             * @return 对象名关键字
             */
            public String getValue()
            {
                return value;
            }

            /**
             * 设置对象名关键字
             * @param value 对象名关键字
             */
            public void setValue(String value)
            {
                this.value = value;
            }

            @Override
            public int hashCode()
            {
                final int prime = 31;
                int result = 1;
                result = prime * result + ((name == null) ? 0 : name.hashCode());
                result = prime * result + ((value == null) ? 0 : value.hashCode());
                return result;
            }

            @Override
            public boolean equals(Object obj)
            {
                if (this == obj)
                    return true;
                if (obj == null)
                    return false;
                if (getClass() != obj.getClass())
                    return false;
                FilterRule other = (FilterRule)obj;
                if (name == null)
                {
                    if (other.name != null)
                        return false;
                }
                else if (!name.equals(other.name))
                    return false;
                if (value == null)
                {
                    if (other.value != null)
                        return false;
                }
                else if (!value.equals(other.value))
                    return false;
                return true;
            }

            @Override
            public String toString()
            {
                return "FilterRule [name=" + name + ", value=" + value + "]";
            }
            
        }
        
        /**
         * 获取过滤规则列表
         * @return 过滤规则列表
         */
        public List<FilterRule> getFilterRules()
        {
            if(this.filterRules == null){
                this.filterRules = new ArrayList<FilterRule>();
            }
            return filterRules;
        }

        /**
         * 设置过滤规则列表
         * @param filterRules 过滤规则列表
         */
        public void setFilterRules(List<FilterRule> filterRules)
        {
            this.filterRules = filterRules;
        }
        
        /**
         * 新增过滤规则
         * @param name 指定过滤规则按对象名的前缀或后缀进行过滤
         * @param value 过滤规则中对象名关键字
         */
        public void addFilterRule(String name, String value){
            this.getFilterRules().add(new FilterRule(name, value));
        }

        @Override
        public String toString()
        {
            return "Filter [fileterRules=" + filterRules + "]";
        }
        
    }

    /**
     * 获取事件通知配置ID
     * @return 事件通知配置ID
     */
    public String getId()
    {
        return id;
    }

    /**
     * 设置事件通知配置ID
     * @param id 事件通知配置ID
     */
    public void setId(String id)
    {
        this.id = id;
    }

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
    
    @Deprecated
    public List<String> getEvents()
    {
    	List<String> list = new ArrayList<String>();
    	for(EventTypeEnum e : this.getEventTypes()) {
    		list.add(ObsConvertor.transEventTypeStatic(e));
    	}
        return list;
    }

    @Deprecated
    public void setEvents(List<String> events)
    {
    	if(events != null) {
    		for(String event : events) {
    			EventTypeEnum e = EventTypeEnum.getValueFromCode(event);
    			if(e != null) {
    				this.getEventTypes().add(e);
    			}
    		}
    	}
    }

    /**
     * 获取需要发布通知消息的事件类型列表
     * @return 事件类型列表
     */
    public List<EventTypeEnum> getEventTypes()
    {
        if(this.events == null){
            this.events = new ArrayList<EventTypeEnum>();
        }
        return events;
    }

    /**
     * 设置需要发布通知消息的事件类型列表
     * @param events 事件类型列表
     */
    public void setEventTypes(List<EventTypeEnum> events)
    {
    	this.events = events;
    }

    /**
     * 获取过滤规则组
     * @return 过滤规则组
     */
    public Filter getFilter()
    {
        return filter;
    }

    /**
     * 设置过滤规则组
     * @param filter 过滤规则组
     */
    public void setFilter(Filter filter)
    {
        this.filter = filter;
    }

    @Override
    public String toString()
    {
        return "TopicConfiguration [id=" + id + ", topic=" + topic + ", events=" + events + ", filter=" + filter + "]";
    }
    
}
