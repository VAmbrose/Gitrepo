package com.etf.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.etf.dto.ExtTableDetailsDTO;

public class NewExtTableDetailsRowMapper
implements RowMapper {
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        ExtTableDetailsDTO dto = new ExtTableDetailsDTO();
        dto.setEtdId(rs.getInt("ETD_ID"));
        dto.setEtfId(rs.getInt("ETF_ID"));
        dto.setColumnPosition(rs.getInt("COLUMN_POSITION"));
        dto.setColumnName(rs.getString("COLUMN_NAME"));
        dto.setColumnDatatype(rs.getString("COLUMN_DATATYPE"));
        dto.setColumnLength(rs.getInt("COLUMN_LENGTH"));
        dto.setStartPosition(rs.getInt("START_POSITION"));
        dto.setEndPosition(rs.getInt("END_POSITION"));
        dto.setRowAddTmstp(rs.getTimestamp("ROW_ADD_TMSTP"));
        dto.setRowUpdTmstp(rs.getTimestamp("ROW_UPD_TMSTP"));
        dto.setRowExpTmstp(rs.getTimestamp("ROW_EXP_TMSTP"));
        return dto;
    }
}

