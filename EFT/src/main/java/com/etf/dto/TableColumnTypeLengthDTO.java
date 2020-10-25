package com.etf.dto;

public class TableColumnTypeLengthDTO {
    private String tableName;
    private int columnId;
    private String columnName;
    private String dataType;
    private int dataLength;

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getColumnId() {
        return this.columnId;
    }

    public void setColumnId(int columnId) {
        this.columnId = columnId;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return this.dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getDataLength() {
        return this.dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }
}

