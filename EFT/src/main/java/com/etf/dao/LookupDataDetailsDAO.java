package com.etf.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.etf.common.ETFStringUtil;
import com.etf.dto.ExtTableDetailsDTO;
import com.etf.dto.ExtTableFileNameDTO;
import com.etf.dto.TableIdNameDTO;
import com.etf.mapper.TableIdNameRowMapper;


/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LookupDataDetailsDAO
extends JdbcDaoSupport {
    private static final Logger LOGGER = Logger.getLogger(LookupDataDetailsDAO.class.getName());
    private PlatformTransactionManager transactionManager;

    public Map<String, Integer> insertParentData(final List<ExtTableFileNameDTO> extTableFileNameFinalList) {
        HashMap<String, Integer> map;
        block5 : {
            map = new HashMap<String, Integer>();
            DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
            TransactionStatus status = this.transactionManager.getTransaction(transactionDefinition);
            try {
                String insertQuery = "INSERT INTO ALL_EXT_TABLE_FILE_NAME(EXT_TABLE_NAME, SEQUENCE_NAME, DIRECTORY_NAME, FILE_NAME, TOKEN, ROW_ADD_TMSTP, ROW_UPD_TMSTP, ROW_EXP_TMSTP) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
                int[] result = this.getJdbcTemplate().batchUpdate(insertQuery, new BatchPreparedStatementSetter(){

                    public void setValues(PreparedStatement ps, int i2) throws SQLException {
                        ExtTableFileNameDTO dto = (ExtTableFileNameDTO)extTableFileNameFinalList.get(i2);
                        ps.setString(1, dto.getTableName());
                        if (dto.getSequenceName().length() > 26) {
                            String seqName = dto.getSequenceName().substring(0, 26);
                            ps.setString(2, "SEQ_" + seqName);
                        } else {
                            ps.setString(2, "SEQ_" + dto.getSequenceName());
                        }
                        ps.setString(3, dto.getDirectoryName());
                        ps.setString(4, dto.getFileName());
                        ps.setString(5, dto.getToken());
                        ps.setTimestamp(6, new Timestamp(new Date().getTime()));
                        ps.setTimestamp(7, new Timestamp(new Date().getTime()));
                        ps.setTimestamp(8, ETFStringUtil.convertStringToTimestamp("2049-12-31 00:00:00.001"));
                    }

                    public int getBatchSize() {
                        return extTableFileNameFinalList.size();
                    }
                });
                this.transactionManager.commit(status);
                if (null == result || result.length <= 0) break block5;
                try {
                    String selectSQL = "SELECT ETF_ID, EXT_TABLE_NAME FROM ALL_EXT_TABLE_FILE_NAME WHERE ROW_EXP_TMSTP >= SYSTIMESTAMP ORDER BY ETF_ID ASC";
                    TableIdNameRowMapper rowMapper = new TableIdNameRowMapper();
                    List<TableIdNameDTO> tableIdNameList = this.getJdbcTemplate().query(selectSQL, (RowMapper)rowMapper);
                    for (TableIdNameDTO tableIdName : tableIdNameList) {
                        map.put(tableIdName.getTableName(), tableIdName.getEtfId());
                    }
                }
                catch (Exception exception) {
                    LOGGER.error("||**---**|| " + exception.getMessage());
                    exception.printStackTrace();
                }
            }
            catch (Exception exception) {
                this.transactionManager.rollback(status);
                LOGGER.error("||*******|| " + exception.getMessage());
                exception.printStackTrace();
            }
        }
        return map;
    }

    public int[] insertChildData(List<ExtTableDetailsDTO> detailsList) {
        List<List<ExtTableDetailsDTO>> devidedLists = LookupDataDetailsDAO.splitIntoSubList(detailsList, 1000);
        int[] result = null;
        for (final List<ExtTableDetailsDTO> detailsDTOs : devidedLists) {
            DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
            TransactionStatus status = this.transactionManager.getTransaction(transactionDefinition);
            try {
                String insertQuery = "INSERT INTO ALL_EXT_TABLE_DETAILS(ETF_ID, COLUMN_POSITION, COLUMN_NAME, COLUMN_DATATYPE, COLUMN_LENGTH, START_POSITION, END_POSITION, ROW_ADD_TMSTP, ROW_UPD_TMSTP, ROW_EXP_TMSTP) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                result = this.getJdbcTemplate().batchUpdate(insertQuery, new BatchPreparedStatementSetter(){

                    public void setValues(PreparedStatement ps, int i2) throws SQLException {
                        ExtTableDetailsDTO dto = (ExtTableDetailsDTO)detailsDTOs.get(i2);
                        ps.setInt(1, dto.getEtfId());
                        ps.setInt(2, dto.getColumnPosition());
                        ps.setString(3, dto.getColumnName());
                        ps.setString(4, dto.getColumnDatatype());
                        ps.setInt(5, dto.getColumnLength());
                        ps.setInt(6, dto.getStartPosition());
                        ps.setInt(7, dto.getEndPosition());
                        ps.setTimestamp(8, new Timestamp(new Date().getTime()));
                        ps.setTimestamp(9, new Timestamp(new Date().getTime()));
                        ps.setTimestamp(10, ETFStringUtil.convertStringToTimestamp("2049-12-31 00:00:00.001"));
                    }

                    public int getBatchSize() {
                        return detailsDTOs.size();
                    }
                });
                this.transactionManager.commit(status);
            }
            catch (Exception exception) {
                this.transactionManager.rollback(status);
                LOGGER.error("||*******|| " + exception.getMessage());
                exception.printStackTrace();
            }
        }
        return result;
    }

    public static <T> List<List<T>> splitIntoSubList(List<T> list, int size) {
        ArrayList<List<T>> parts = new ArrayList<List<T>>();
        int N = list.size();
        for (int i2 = 0; i2 < N; i2 += size) {
            parts.add(new ArrayList<T>(list.subList(i2, Math.min(N, i2 + size))));
        }
        return parts;
    }

    public PlatformTransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

}

