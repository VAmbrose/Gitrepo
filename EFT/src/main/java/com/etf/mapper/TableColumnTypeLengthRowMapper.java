package com.etf.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.etf.dto.TableColumnTypeLengthDTO;

public class TableColumnTypeLengthRowMapper
implements RowMapper {
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        TableColumnTypeLengthDTO dto = new TableColumnTypeLengthDTO();
        dto.setTableName(rs.getString("TABLE_NAME"));
        dto.setColumnId(rs.getInt("COLUMN_ID"));
        dto.setColumnName(rs.getString("COLUMN_NAME"));
        dto.setDataType(rs.getString("DATA_TYPE"));
        dto.setDataLength(rs.getInt("DATA_LENGTH"));
        return dto;
    }
}

