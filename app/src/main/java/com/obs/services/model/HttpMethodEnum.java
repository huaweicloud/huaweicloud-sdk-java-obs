package com.obs.services.model;

/**
 * HTTP/HTTPS请求方法
 */
public enum HttpMethodEnum
{
    /**
     * GET方法，一般用于查询
     */
    GET("Get"),

    /**
     * PUT方法，一般用于新增、修改
     */
    PUT("Put"),

    /**
     * POST方法，一般用于新增
     */
    POST("Post"),

    /**
     * DELETE方法，一般用于删除
     */
    DELETE("Delete"),

    /**
     * HEAD方法，一般用于查询响应头
     */
    HEAD("Head"),

    /**
     * OPTIONS方法，一般用于预请求
     */
    OPTIONS("Options");
    
    private String operationType;
    
    private HttpMethodEnum(String operationType)
    {
        if (operationType == null)
        {
            throw new IllegalArgumentException("operation type code is null");
        }
        this.operationType = operationType;
    }
    
    public String getOperationType()
    {
        return this.operationType.toUpperCase();
    }
    
    public static HttpMethodEnum getValueFromStringCode(String operationType)
    {
        if (operationType == null)
        {
            throw new IllegalArgumentException("operation type is null");
        }
        
        for (HttpMethodEnum installMode : HttpMethodEnum.values())
        {
            if (installMode.getOperationType().equals(operationType.toUpperCase()))
            {
                return installMode;
            }
        }
        
        throw new IllegalArgumentException("operation type is illegal");
    }
}
