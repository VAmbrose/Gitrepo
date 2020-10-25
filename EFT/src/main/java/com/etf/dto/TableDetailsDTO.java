package com.etf.dto;

public class TableDetailsDTO {
    private String columnName;
    private int startPosition;
    private int endPosition;
    private String cellValue;

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public int getStartPosition() {
        return this.startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getEndPosition() {
        return this.endPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    public String getCellValue() {
        return this.cellValue;
    }

    public void setCellValue(String cellValue) {
        this.cellValue = cellValue;
    }
}

