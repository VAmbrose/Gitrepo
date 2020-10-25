package com.etf.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import com.etf.dto.ExtTableFileNameDTO;

public class ExtTableFileNameRowMapper
implements RowMapper {
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        ExtTableFileNameDTO dto = new ExtTableFileNameDTO();
        dto.setTableName(rs.getString("TABLE_NAME"));
        dto.setSequenceName(rs.getString("SEQUENCE_NAME"));
        dto.setDirectoryName(rs.getString("DIRECTORY_NAME"));
        dto.setFileName(rs.getString("LOCATION"));
        dto.setAccessParameter(rs.getString("ACCESS_PARAMETERS"));
        return dto;
    }
}

