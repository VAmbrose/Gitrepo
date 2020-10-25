package com.etf.common;

import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ETFTableProperties {
    private Map<String, String> tableProperties;

    public ETFTableProperties(Map<String, String> tableProperties) {
        this.tableProperties = tableProperties;
    }

    public Map<String, String> getTableProperties() {
        return this.tableProperties;
    }

    public void setTableProperties(Map<String, String> tableProperties) {
        this.tableProperties = tableProperties;
    }
}

