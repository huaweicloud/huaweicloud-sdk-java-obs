package com.obs.services.model;

public class PolicyConditionItem {
    
    private ConditionOperator operator;
    
    private String key;
    
    private String value;

    public PolicyConditionItem(ConditionOperator operator, String key, String value) {
        this.operator = operator;
        this.key = key;
        this.value = value;
    }
    
    
    public String toString() {
        return String.format("[\"%s\",\"$%s\",\"%s\"]", operator.getOperationCode(), key, value);
    }


    public static enum ConditionOperator {

        EQUAL("eq"),

        STARTS_WITH("starts-with");

        private String operationCode;

        private ConditionOperator(String operationCode) {
            this.operationCode = operationCode;
        }

        public String getOperationCode() {
            return this.operationCode;
        }
    }
}
