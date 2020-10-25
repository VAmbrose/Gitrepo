package com.etf.dao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.PlatformTransactionManager;

import com.etf.dto.ExtTableFileNameDTO;
import com.etf.dto.TableColumnTypeLengthDTO;
import com.etf.mapper.ExtTableFileNameRowMapper;
import com.etf.mapper.TableColumnTypeLengthRowMapper;

public class CreateTableDAO extends JdbcDaoSupport {
	private static final Logger LOGGER = Logger.getLogger(CreateTableDAO.class.getName());
	private PlatformTransactionManager transactionManager;
	List<String> notValid = Arrays.asList("E_L2_RATEQUOTE", "E_L2_RATEQUOTE_ERR", "E_L3_RATEQUOTE",
			"E_L3_RATEQUOTE_ERR", "E_L5_RATEQUOTE", "E_L5_RATEQUOTE_ERR", "E_L2_RATEQUOTE_INTL",
			"E_L2_RATEQUOTE_ERR_INTL", "E_L3_RATEQUOTE_INTL", "E_L3_RATEQUOTE_ERR_INTL", "E_L5_RATEQUOTE_INTL",
			"E_L5_RATEQUOTE_ERR_INTL", "E_L2_RATEQUOTE_MPS", "E_L2_RATEQUOTE_ERR_MPS", "E_L3_RATEQUOTE_MPS",
			"E_L3_RATEQUOTE_ERR_MPS", "E_L5_RATEQUOTE_MPS", "E_L5_RATEQUOTE_ERR_MPS");

	public String executeDatabaseQuery(String tableName, String objectType, String directoryName, String fileName,
			List<String> queryList) throws Exception {
		String result = null;
		int objectExist = 0;

		if ("TABLE".equalsIgnoreCase(objectType)) {
			String sql = "SELECT COUNT(*) FROM USER_OBJECTS WHERE OBJECT_NAME = '" + tableName + "' AND OBJECT_TYPE = '"
					+ objectType + "'";
			String sqlTableLookup = "SELECT COUNT(*) FROM ALL_EXT_TABLE_FILE_NAME WHERE DIRECTORY_NAME = '"
					+ directoryName + "' AND FILE_NAME = '" + fileName + "' AND ROW_EXP_TMSTP >= SYSTIMESTAMP";
			String sqlFileLookup = "SELECT COUNT(EXT_TABLE_NAME) FROM ALL_EXT_TABLE_FILE_NAME WHERE EXT_TABLE_NAME = '"
					+ tableName + "' AND ROW_EXP_TMSTP >= SYSTIMESTAMP GROUP BY EXT_TABLE_NAME";
			objectExist = this.getJdbcTemplate().queryForInt(sql);
			int multipleTableOneFile = this.getJdbcTemplate().queryForInt(sqlTableLookup);
			int oneTableMultiplefile = this.getJdbcTemplate().queryForInt(sqlFileLookup);
			if (objectExist == 1) {
				if (!notValid.contains(tableName.toUpperCase())) {
					if (multipleTableOneFile >= 1) {
						if (oneTableMultiplefile <= 1) {
							this.getJdbcTemplate().execute("TRUNCATE TABLE " + tableName);
							LOGGER.info("||-->>" + "Table truncated : " + tableName);
						} else {
							LOGGER.info("||-->>" + "Data table Not truncated from : " + tableName);
						}
					} else {
						String query = "TRUNCATE TABLE " + tableName;
						this.getJdbcTemplate().execute(query);
						LOGGER.info("||-->>" + "Data table truncated successfully from : " + tableName);
					}
				} else {
					LOGGER.info("||-->>" + "Data table skipped from truncation successfully : " + tableName);
				}
			} else {
				for (String query : queryList) {
					LOGGER.info("SQL Query : " + query);
					try {
						if (null == query)
							continue;
						this.getJdbcTemplate().execute(query);
						LOGGER.info("Table creating : " + query);
						continue;
					} catch (Exception exception) {
						LOGGER.error("||*******|| " + exception.getMessage());
						LOGGER.error("||*******|| " + query);
						exception.printStackTrace();
						throw new Exception(exception.getMessage());
					}
				}
			}
		}
		return result;
	}

	public int[] insertDatabaseQuery(List<List<String>> allFileContents) throws Exception {
		int[] result = null;
		for (List<String> fileContents : allFileContents) {
			String[] allSQL = fileContents.toArray(new String[fileContents.size()]);

			try {
				LOGGER.info("||-->>  Inserting Data ...");
				result = this.getJdbcTemplate().batchUpdate(allSQL);
				continue;
			} catch (Exception exception) {
				for (String query : allSQL) {
					LOGGER.error("||*******|| " + query);
				}
				LOGGER.error("||*******|| " + exception.getMessage());
				throw new Exception(exception.getMessage());
			}

		}
		return result;
	}

	// Written to avoid truncation and do update of given list of tables (New
	// Changes - Feb 2017)--- KARAN
	public void updelDatabaseQuery(String UDsql, String tableName, String UDT) throws Exception {
		if (notValid.contains(tableName.toUpperCase())) {
			try {
				if (UDT.equals("T")) {
					this.getJdbcTemplate().execute(UDsql);
					LOGGER.info("||-->>" + "Table truncated : " + tableName);
				} else {
					int result = this.getJdbcTemplate().update(UDsql);
					if (UDT.equals("U"))
						LOGGER.info("||-->>" + result + "  Rows UPDATED for : " + tableName + "\n\n");
					else
						LOGGER.info("||-->>" + result + "  Rows DELETED for : " + tableName + "\n\n");
				}
			} catch (Exception exception) {
				LOGGER.error("||*******|| " + exception.getMessage());
				throw new Exception(exception.getMessage());
			}
		}
	}

	public List<String> selectDatabaseQuery(String tableName) throws Exception {
		List<String> data = new ArrayList<String>();
		if (notValid.contains(tableName.toUpperCase())) {
			try {
				String sql = "SELECT DISTINCT TABLE_NAME FROM " + tableName;
				data = this.getJdbcTemplate().queryForList(sql, String.class);
			} catch (Exception exception) {
				LOGGER.error("||*******|| " + exception.getMessage());
				throw new Exception(exception.getMessage());
			}
		}
		return data;
	}

	public List<ExtTableFileNameDTO> generateTableFilesMapping() throws Exception {
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
			throw new Exception(exception.getMessage());
		}
		return dtoList;
	}

	public List<TableColumnTypeLengthDTO> generateTableColumnTypeLengthDetails() throws Exception {
		List<TableColumnTypeLengthDTO> columnTypeLengthList = new ArrayList();
		try {
			String sql = "SELECT A.TABLE_NAME, A.COLUMN_ID, A.COLUMN_NAME, A.DATA_TYPE, A.DATA_LENGTH FROM ALL_TAB_COLUMNS A, ALL_EXTERNAL_TABLES B WHERE A.TABLE_NAME = B.TABLE_NAME";
			TableColumnTypeLengthRowMapper rowMapper = new TableColumnTypeLengthRowMapper();
			columnTypeLengthList = this.getJdbcTemplate().query(sql, (RowMapper) rowMapper);
		} catch (IncorrectResultSizeDataAccessException exception) {
			LOGGER.error("Exception : " + exception);
			throw new Exception(exception.getMessage());
		}
		return columnTypeLengthList;
	}

	public PlatformTransactionManager getTransactionManager() {
		return this.transactionManager;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void deleteDataFromTable(String tableName) throws Exception {
		try {
			String query = "DELETE FROM " + tableName;
			this.getJdbcTemplate().execute(query);
			LOGGER.info("Data deleted successfully from : " + tableName);
		} catch (Exception exception) {
			LOGGER.error("Error in deleting data : " + exception.getMessage());
			throw new Exception(exception.getMessage());
		}
	}

	public void executeTruncateQuery(String tableName) throws Exception {
		try {
			String query = "TRUNCATE TABLE " + tableName;
			this.getJdbcTemplate().execute(query);
			LOGGER.info("Data table truncated successfully from : " + tableName);
		} catch (Exception exception) {
			LOGGER.error("Error in deleting data : " + exception.getMessage());
			throw new Exception(exception.getMessage());
		}
	}

	private void writeReadingStatusToFile(String fileName, String data) throws Exception {
		try {
			File file = new File("logs/" + fileName + ".txt");
			FileUtils.writeStringToFile(file, data);
		} catch (IOException exception) {
			LOGGER.error("Error in writting data : " + exception.getMessage());
			throw new Exception(exception.getMessage());
		}
	}

	private String readTruncateStatus(String fileName) throws Exception {
		String line = null;
		try {
			File file = new File("logs/" + fileName + ".txt");
			if (file.exists()) {
				line = FileUtils.readFileToString(file);
			} else {
				this.writeReadingStatusToFile(fileName, "0");
			}
		} catch (IOException exception) {
			LOGGER.error("Error in reding data : " + exception.getMessage());
			throw new Exception(exception.getMessage());
		}
		return line;
	}
}
