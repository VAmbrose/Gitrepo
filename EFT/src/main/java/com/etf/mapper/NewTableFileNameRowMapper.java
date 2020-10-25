package com.etf.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.etf.dto.ExtTableFileNameDTO;

public class NewTableFileNameRowMapper
implements RowMapper {
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        ExtTableFileNameDTO dto = new ExtTableFileNameDTO();
        dto.setEtfId(rs.getInt("ETF_ID"));
        dto.setTableName(rs.getString("EXT_TABLE_NAME"));
        dto.setSequenceName(rs.getString("SEQUENCE_NAME"));
        dto.setDirectoryName(rs.getString("DIRECTORY_NAME"));
        dto.setFileName(rs.getString("FILE_NAME"));
        dto.setToken(rs.getString("TOKEN"));
        dto.setRowAddTmstp(rs.getTimestamp("ROW_ADD_TMSTP"));
        dto.setRowUpdTmstp(rs.getTimestamp("ROW_UPD_TMSTP"));
        dto.setRowExpTmstp(rs.getTimestamp("ROW_EXP_TMSTP"));
        return dto;
    }
}

