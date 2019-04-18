package com.obs.services.model;

/**
 * 服务端加密类型
 *
 */
public enum SSEAlgorithmEnum {
    /**
     * KMS 加密方式
     */
    KMS("kms"),
    
    /**
     * AES256 加密方式
     */
    AES256("AES256");
    
    private String code;
    
    private SSEAlgorithmEnum(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
    
    public static SSEAlgorithmEnum getValueFromCode(String code) {
        for (SSEAlgorithmEnum val : SSEAlgorithmEnum.values()) {
            if (val.code.equals(code)) {
                return val;
            }
        }
        return null;
    }

}
