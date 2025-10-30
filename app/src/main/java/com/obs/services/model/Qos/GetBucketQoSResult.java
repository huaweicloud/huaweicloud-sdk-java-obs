package com.obs.services.model.Qos;

import com.obs.services.model.HeaderResponse;

import java.util.ArrayList;
import java.util.List;

public class GetBucketQoSResult extends HeaderResponse {
    private String qosGroup = "";
    private List<QosRule> bucketQosRules = new ArrayList<>();
    private List<QosRule> groupQosRules = new ArrayList<>();

    /**
     * 获取Bucket的QoS规则列表
     */
    public List<QosRule> getBucketQosRules() {
        return bucketQosRules;
    }

    /**
     * 设置Bucket的QoS规则列表
     */
    public void setBucketQosRules(List<QosRule> bucketQosRules) {
        this.bucketQosRules = bucketQosRules;
    }

    /**
     * 获取QoS组的QoS规则列表
     */
    public List<QosRule> getGroupQosRules() {
        return groupQosRules;
    }

    /**
     * 设置QoS组的QoS规则列表
     */
    public void setGroupQosRules(List<QosRule> groupQosRules) {
        this.groupQosRules = groupQosRules;
    }

    /**
     * 获取QoS组名称
     */
    public String getQosGroup() {
        return qosGroup;
    }

    /**
     * 设置QoS组名称
     */
    public void setQosGroup(String qosGroup) {
        this.qosGroup = qosGroup;
    }

    @Override
    public String toString() {
        return "GetBucketQoSResult{" +
                "statusCode=" + getStatusCode() +
                ", requestId='" + getRequestId() + '\'' +
                ", qosGroup='" + qosGroup + '\'' +
                ", bucketQosRules=" + bucketQosRules +
                ", groupQosRules=" + groupQosRules +
                '}';
    }
}