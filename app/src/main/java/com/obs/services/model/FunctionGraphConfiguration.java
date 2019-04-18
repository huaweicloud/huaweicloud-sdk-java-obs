package com.obs.services.model;

import java.util.List;

/**
 * 函数工作流服务, 消息通知配置
 *
 */
public class FunctionGraphConfiguration extends AbstractNotification {

    private String functionGraph;
    
    public FunctionGraphConfiguration() {

    }

    /**
     * 构造函数
     * 
     * @param id            事件通知配置ID
     * @param filter        过滤规则组
     * @param functionGraph 函数工作流服务的URN
     * @param events        需要发布通知消息的事件类型列表
     */
    public FunctionGraphConfiguration(String id, Filter filter, String functionGraph, List<EventTypeEnum> events) {
        super(id, filter, events);
        this.functionGraph = functionGraph;

    }

    /**
     * 获取函数工作流服务的URN
     * 
     * @return 函数工作流服务的URN
     */
    public String getFunctionGraph() {
        return functionGraph;
    }

    /**
     * 设置函数工作流服务的URN
     * 
     * @param functionGraph 函数工作流服务的URN
     */
    public void setFunctionGraph(String functionGraph) {
        this.functionGraph = functionGraph;
    }

    @Override
    public String toString() {
        return "FunctionGraphConfiguration [id=" + id + ", functionGraph=" + functionGraph + ", events=" + events + ", filter="
                + filter + "]";
    }

}
