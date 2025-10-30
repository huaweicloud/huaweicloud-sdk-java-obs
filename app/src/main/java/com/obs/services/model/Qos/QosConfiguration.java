package com.obs.services.model.Qos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QosConfiguration {
    private List<QosRule> rules;

    public QosConfiguration(){

    }

    public QosConfiguration(QosRule rule1, QosRule rule2) {
        // 传递两个参数 分别设置内网 和 外网的qos
        this.rules = new ArrayList<>();
        this.rules.add(rule1);
        if (rule2 != null) {
            this.rules.add(rule2);
        }
        this.rules = Collections.unmodifiableList(this.rules);
    }

    public QosConfiguration(QosRule rule) {
        // 传递一个参数直接设置total的qos
        this.rules = new ArrayList<>();
        this.rules.add(rule);
        this.rules = Collections.unmodifiableList(this.rules);
    }

    public List<QosRule> getRules() {
        return rules;
    }

    public void setRules(List<QosRule> rules){
        if (rules == null){
            this.rules = null;
        }else{
            this.rules = Collections.unmodifiableList(new ArrayList<>(rules));
        }
    }
}
