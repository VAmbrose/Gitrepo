package com.etf.common;

import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ETFProperties {
    private Map<String, String> folderLocationMap;

    public ETFProperties(Map<String, String> folderLocationMap) {
        this.folderLocationMap = folderLocationMap;
    }

    public Map<String, String> getFolderLocationMap() {
        return this.folderLocationMap;
    }

    public void setFolderLocationMap(Map<String, String> folderLocationMap) {
        this.folderLocationMap = folderLocationMap;
    }
}

