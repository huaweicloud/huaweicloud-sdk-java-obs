package com.obs.services.model;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class S3BucketCors extends HeaderResponse
{
    
    private List<BucketCorsRule> rules;
    
    public S3BucketCors()
    {
    }
    
    public S3BucketCors(List<BucketCorsRule> rules)
    {
        this.rules = rules;
    }
    
    public List<BucketCorsRule> getRules()
    {
        if (null == rules)
        {
            rules = new ArrayList<BucketCorsRule>();
        }
        return rules;
    }
    
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
