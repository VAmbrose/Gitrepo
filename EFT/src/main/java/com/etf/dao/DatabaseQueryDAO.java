package com.etf.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.PlatformTransactionManager;

import com.etf.dto.ExtTableFileNameDTO;
import com.etf.dto.TableColumnTypeLengthDTO;
import com.etf.mapper.ExtTableFileNameRowMapper;
import com.etf.mapper.TableColumnTypeLengthRowMapper;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DatabaseQueryDAO extends JdbcDaoSupport {
	private static final Logger LOGGER = Logger.getLogger(DatabaseQueryDAO.class.getName());
	private PlatformTransactionManager transactionManager;

	public String executeDatabaseQuery(List<String> createSequenceList) {
		String result = null;
		for (String query : createSequenceList) {
			try {
				if (null == query)
					continue;
				this.getJdbcTemplate().execute(query);
			} catch (Exception exception) {
				LOGGER.error("||*******|| " + exception.getMessage());
				exception.printStackTrace();
			}
		}
		return result;
	}

	public int[] insertDatabaseQuery(String insertQuery, List<String> fileContents) {
		int[] result = null;
		String[] allSQL = fileContents.toArray(new String[fileContents.size()]);
		try {
			result = this.getJdbcTemplate().batchUpdate(allSQL);
		} catch (Exception exception) {
			LOGGER.error("||*******|| " + exception.getMessage());
			exception.printStackTrace();
		}
		return result;
	}

	public List<ExtTableFileNameDTO> generateTableFilesMapping() {
		List<ExtTableFileNameDTO> dtoList = new ArrayList();
		try {
			String sql = "SELECT A.TABLE_NAME, A.TABLE_NAME AS SEQUENCE_NAME, A.DIRECTORY_NAME, A.LOCATION, B.ACCESS_PARAMETERS FROM ALL_EXTERNAL_LOCATIONS A, ALL_EXTERNAL_TABLES B WHERE B.TABLE_NAME = A.TABLE_NAME";
			ExtTableFileNameRowMapper rowMapper = new ExtTableFileNameRowMapper();
			dtoList = this.getJdbcTemplate().query(sql, (RowMapper) rowMapper);
			if (null != dtoList) {
				LOGGER.info("Total Record selected : " + dtoList.size());
			} else {
				LOGGER.info("Total Record selected : 0");
			}
		} catch (IncorrectResultSizeDataAccessException exception) {
			LOGGER.error("Exception : " + exception);
		}
		return dtoList;
	}

	public List<TableColumnTypeLengthDTO> generateTableColumnTypeLengthDetails() {
		List<TableColumnTypeLengthDTO> columnTypeLengthList = new ArrayList();
		try {
			String sql = "SELECT A.TABLE_NAME, A.COLUMN_ID, A.COLUMN_NAME, A.DATA_TYPE, A.DATA_LENGTH FROM ALL_TAB_COLUMNS A, ALL_EXTERNAL_TABLES B WHERE A.TABLE_NAME = B.TABLE_NAME";
			TableColumnTypeLengthRowMapper rowMapper = new TableColumnTypeLengthRowMapper();
			columnTypeLengthList = this.getJdbcTemplate().query(sql, (RowMapper) rowMapper);
		} catch (IncorrectResultSizeDataAccessException exception) {
			LOGGER.error("Exception : " + exception);
		}
		return columnTypeLengthList;
	}

	public PlatformTransactionManager getTransactionManager() {
		return this.transactionManager;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
}
