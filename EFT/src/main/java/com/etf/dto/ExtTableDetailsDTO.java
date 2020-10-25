package com.etf.dto;

import java.sql.Timestamp;

public class ExtTableDetailsDTO {
    private int etdId = 0;
    private int etfId = 0;
    private int columnPosition = 0;
    private String columnName = null;
    private String columnDatatype = null;
    private int columnLength = 0;
    private int startPosition = 0;
    private int endPosition = 0;
    private Timestamp rowAddTmstp = null;
    private Timestamp rowUpdTmstp = null;
    private Timestamp rowExpTmstp = null;

    public int getEtdId() {
        return this.etdId;
    }

    public void setEtdId(int etdId) {
        this.etdId = etdId;
    }

    public int getEtfId() {
        return this.etfId;
    }

    public void setEtfId(int etfId) {
        this.etfId = etfId;
    }

    public int getColumnPosition() {
        return this.columnPosition;
    }

    public void setColumnPosition(int columnPosition) {
        this.columnPosition = columnPosition;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnDatatype() {
        return this.columnDatatype;
    }

    public void setColumnDatatype(String columnDatatype) {
        this.columnDatatype = columnDatatype;
    }

    public int getColumnLength() {
        return this.columnLength;
    }

    public void setColumnLength(int columnLength) {
        this.columnLength = columnLength;
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

    public Timestamp getRowAddTmstp() {
        return this.rowAddTmstp;
    }

    public void setRowAddTmstp(Timestamp rowAddTmstp) {
        this.rowAddTmstp = rowAddTmstp;
    }

    public Timestamp getRowUpdTmstp() {
        return this.rowUpdTmstp;
    }

    public void setRowUpdTmstp(Timestamp rowUpdTmstp) {
        this.rowUpdTmstp = rowUpdTmstp;
    }

    public Timestamp getRowExpTmstp() {
        return this.rowExpTmstp;
    }

    public void setRowExpTmstp(Timestamp rowExpTmstp) {
        this.rowExpTmstp = rowExpTmstp;
    }
}

