package com.obs.services.model.Qos;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum NetworkType {
    INTRANET("intranet"),
    EXTRANET("extranet"),
    TOTAL("total");
    private String code;
    private static Map<String,NetworkType> mp = new HashMap<>();

    static {
        for (NetworkType type : NetworkType.values()) {
            mp.put(type.code.toLowerCase(Locale.ENGLISH), type);
        }
    }

    NetworkType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static NetworkType getValueFromCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        return mp.get(code.toLowerCase(Locale.ENGLISH));
    }
}