package com.oef.services.model;

import com.obs.services.model.SpecialParamEnum;

public enum RequestParamEnum {
	/**
     * 获取/设置/删除异步策略
     */
    EXTENSION_POLICY("v1/extension_policy"),
    /**
     * 获取/设置/删除异步任务
     */
	ASYNCH_FETCH_JOB("v1/async-fetch/jobs");
    
    private String stringCode;
    
    private RequestParamEnum(String stringCode)
    {
        if (stringCode == null)
        {
            throw new IllegalArgumentException("stringCode is null");
        }
        this.stringCode = stringCode;
    }
    
    public String getStringCode()
    {
        return this.stringCode.toLowerCase();
    }
    
    public String getOriginalStringCode()
    {
        return this.stringCode;
    }
    
    public static SpecialParamEnum getValueFromStringCode(String stringCode)
    {
        if (stringCode == null)
        {
            throw new IllegalArgumentException("string code is null");
        }
        
        for (SpecialParamEnum installMode : SpecialParamEnum.values())
        {
            if (installMode.getStringCode().equals(stringCode.toLowerCase()))
            {
                return installMode;
            }
        }
        
        throw new IllegalArgumentException("string code is illegal");
    }
}
