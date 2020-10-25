package com.etf.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.etf.dto.TableIdNameDTO;

public class TableIdNameRowMapper
implements RowMapper {
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        TableIdNameDTO dto = new TableIdNameDTO();
        dto.setEtfId(rs.getInt("ETF_ID"));
        dto.setTableName(rs.getString("EXT_TABLE_NAME"));
        return dto;
    }
}

