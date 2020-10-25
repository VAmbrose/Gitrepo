package com.etf.dto;

import java.sql.Timestamp;
import java.util.List;

import com.etf.dto.ColStartEndDTO;
import com.etf.dto.ExtTableDetailsDTO;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ExtTableFileNameDTO {
    private int etfId = 0;
    private String tableName = null;
    private String sequenceName = null;
    private String fileName = null;
    private String directoryName = null;
    private String accessParameter = null;
    private String token = null;
    private List<ExtTableDetailsDTO> tableDetailsDTOList = null;
    private List<ColStartEndDTO> colStartEndDTOList = null;
    private Timestamp rowAddTmstp = null;
    private Timestamp rowUpdTmstp = null;
    private Timestamp rowExpTmstp = null;

    public int getEtfId() {
        return this.etfId;
    }

    public void setEtfId(int etfId) {
        this.etfId = etfId;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSequenceName() {
        return this.sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDirectoryName() {
        return this.directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getAccessParameter() {
        return this.accessParameter;
    }

    public void setAccessParameter(String accessParameter) {
        this.accessParameter = accessParameter;
    }

    public List<ExtTableDetailsDTO> getTableDetailsDTOList() {
        return this.tableDetailsDTOList;
    }

    public void setTableDetailsDTOList(List<ExtTableDetailsDTO> tableDetailsDTOList) {
        this.tableDetailsDTOList = tableDetailsDTOList;
    }

    public List<ColStartEndDTO> getColStartEndDTOList() {
        return this.colStartEndDTOList;
    }

    public void setColStartEndDTOList(List<ColStartEndDTO> colStartEndDTOList) {
        this.colStartEndDTOList = colStartEndDTOList;
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

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

