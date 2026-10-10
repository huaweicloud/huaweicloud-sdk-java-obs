package com.obs.services.model.Qos;

import com.obs.services.model.HeaderResponse;

import java.util.ArrayList;
import java.util.List;

public class GetBucketQoSResult extends HeaderResponse {
    private String resourceCluster = "";
    private List<QosRule> bucketQosRules = new ArrayList<>();
    private List<QosRule> clusterQosRules = new ArrayList<>();

    /**
     * 获取Bucket的QoS规则列表
     *
     * @return Bucket级别的QoS规则列表
     */
    public List<QosRule> getBucketQosRules() {
        return bucketQosRules;
    }

    /**
     * 设置Bucket的QoS规则列表
     *
     * @param bucketQosRules Bucket级别的QoS规则列表
     */
    public void setBucketQosRules(List<QosRule> bucketQosRules) {
        this.bucketQosRules = bucketQosRules;
    }

    /**
     * 获取集群的QoS规则列表
     *
     * @return 集群级别的QoS规则列表
     */
    public List<QosRule> getClusterQosRules() {
        return clusterQosRules;
    }

    /**
     * 设置集群的QoS规则列表
     *
     * @param clusterQosRules 集群级别的QoS规则列表
     */
    public void setClusterQosRules(List<QosRule> clusterQosRules) {
        this.clusterQosRules = clusterQosRules;
    }

    /**
     * 获取集群名称
     *
     * @return 集群名称字符串
     */
    public String getResourceCluster() {
        return resourceCluster;
    }

    /**
     * 设置集群名称
     *
     * @param resourceCluster 集群名称字符串
     */
    public void setResourceCluster(String resourceCluster) {
        this.resourceCluster = resourceCluster;
    }

    /**
     * 获取QoS组名称
     * <p>
     * 此方法已废弃，请使用 {@link #getResourceCluster()} 替代。
     * "QoSGroup"概念已重命名为"ResourceCluster"（集群）。
     *
     * @return 集群名称字符串
     */
    @Deprecated
    public String getQosGroup() {
        return resourceCluster;
    }

    /**
     * 设置QoS组名称
     * <p>
     * 此方法已废弃，请使用 {@link #setResourceCluster(String)} 替代。
     * "QoSGroup"概念已重命名为"ResourceCluster"（集群）。
     *
     * @param qosGroup 集群名称字符串
     */
    @Deprecated
    public void setQosGroup(String qosGroup) {
        this.resourceCluster = qosGroup;
    }

    /**
     * 获取QoS组的QoS规则列表
     * <p>
     * 此方法已废弃，请使用 {@link #getClusterQosRules()} 替代。
     * "QoSGroup"概念已重命名为"ResourceCluster"（集群）。
     *
     * @return 集群级别的QoS规则列表
     */
    @Deprecated
    public List<QosRule> getGroupQosRules() {
        return clusterQosRules;
    }

    /**
     * 设置QoS组的QoS规则列表
     * <p>
     * 此方法已废弃，请使用 {@link #setClusterQosRules(List)} 替代。
     * "QoSGroup"概念已重命名为"ResourceCluster"（集群）。
     *
     * @param groupQosRules 集群级别的QoS规则列表
     */
    @Deprecated
    public void setGroupQosRules(List<QosRule> groupQosRules) {
        this.clusterQosRules = groupQosRules;
    }

    @Override
    public String toString() {
        return "GetBucketQoSResult{" +
                "statusCode=" + getStatusCode() +
                ", requestId='" + getRequestId() + '\'' +
                ", resourceCluster='" + resourceCluster + '\'' +
                ", bucketQosRules=" + bucketQosRules +
                ", clusterQosRules=" + clusterQosRules +
                '}';
    }
}
