package com.obs.services.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 桶的跨域资源共享配置
 */
public class BucketCors extends S3BucketCors
{
    private List<BucketCorsRule> rules;
    
    public BucketCors()
    {
    }
    
    /**
     * 构造函数
     * @param rules 桶的跨域资源共享规则列表
     */
    public BucketCors(List<BucketCorsRule> rules)
    {
        this.rules = rules;
    }
    
    /**
     * 获取桶的跨域资源共享规则列表
     * @return 桶的跨域资源共享规则列表
     */
    public List<BucketCorsRule> getRules()
    {
        if (null == rules)
        {
            rules = new ArrayList<BucketCorsRule>();
        }
        return rules;
    }
    
    /**
     * 设置桶的跨域资源共享规则列表
     * @param rules 桶的跨域资源共享规则列表
     */
    public void setRules(List<BucketCorsRule> rules)
    {
        this.rules = rules;
    }
    
    
    @Override
    public String toString()
    {
        return "ObsBucketCors [rules=" + rules + "]";
    }
}
