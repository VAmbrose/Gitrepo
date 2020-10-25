package com.etf.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.PlatformTransactionManager;

import com.etf.dto.ExtTableDetailsDTO;
import com.etf.dto.ExtTableFileNameDTO;
import com.etf.mapper.NewExtTableDetailsRowMapper;
import com.etf.mapper.NewTableFileNameRowMapper;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TableGenerationQueryDAO extends JdbcDaoSupport {
	private static final Logger LOGGER = Logger.getLogger(TableGenerationQueryDAO.class.getName());
	private PlatformTransactionManager transactionManager;

	public PlatformTransactionManager getTransactionManager() {
		return this.transactionManager;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public List<ExtTableFileNameDTO> getTableFileNameList(String fileName, String directoryName) {
		List dtoList = new ArrayList<ExtTableFileNameDTO>();
		try {
			String sql = "SELECT ETF_ID, EXT_TABLE_NAME, SEQUENCE_NAME, DIRECTORY_NAME, FILE_NAME, TOKEN, ROW_ADD_TMSTP, ROW_UPD_TMSTP, ROW_EXP_TMSTP FROM ALL_EXT_TABLE_FILE_NAME WHERE FILE_NAME IN('"
					+ fileName + "') AND DIRECTORY_NAME = '" + directoryName + "' AND ROW_EXP_TMSTP >= SYSTIMESTAMP";
			LOGGER.info(sql);
			NewTableFileNameRowMapper rowMapper = new NewTableFileNameRowMapper();
			dtoList = this.getJdbcTemplate().query(sql, (RowMapper) rowMapper);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return dtoList;
	}

	public List<ExtTableDetailsDTO> getTableDetailsDTOList(String tableName) {
		List detailsDTOList = new ArrayList<ExtTableDetailsDTO>();
		try {
			String sql = "SELECT ETD_ID, ETF_ID, COLUMN_POSITION, COLUMN_NAME, COLUMN_DATATYPE, COLUMN_LENGTH, START_POSITION, END_POSITION, ROW_ADD_TMSTP, ROW_UPD_TMSTP, ROW_EXP_TMSTP FROM ALL_EXT_TABLE_DETAILS WHERE ROW_EXP_TMSTP >= SYSTIMESTAMP AND ETF_ID IN (SELECT ETF_ID FROM ALL_EXT_TABLE_FILE_NAME WHERE EXT_TABLE_NAME = '"
					+ tableName + "') ORDER BY COLUMN_POSITION";
			LOGGER.info(sql);
			NewExtTableDetailsRowMapper rowMapper = new NewExtTableDetailsRowMapper();
			detailsDTOList = this.getJdbcTemplate().query(sql, (RowMapper) rowMapper);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return detailsDTOList;
	}
}
