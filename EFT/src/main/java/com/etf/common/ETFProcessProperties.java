package com.etf.common;

import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ETFProcessProperties {
    private Map<String, String> keyMap;

    public ETFProcessProperties(Map<String, String> keyMap) {
        this.keyMap = keyMap;
    }

    public Map<String, String> getKeyMap() {
        return this.keyMap;
    }

    public void setKeyMap(Map<String, String> keyMap) {
        this.keyMap = keyMap;
    }
}

