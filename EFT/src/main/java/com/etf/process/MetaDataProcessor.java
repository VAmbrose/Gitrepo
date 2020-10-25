package com.etf.process;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import com.etf.common.ETFCommonProcess;
import com.etf.common.ETFStringUtil;
import com.etf.dao.DatabaseQueryDAO;
import com.etf.dao.LookupDataDetailsDAO;
import com.etf.dto.ColStartEndDTO;
import com.etf.dto.ExtTableDetailsDTO;
import com.etf.dto.ExtTableFileNameDTO;
import com.etf.dto.TableColumnTypeLengthDTO;


/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MetaDataProcessor {
    private static final Logger LOGGER = Logger.getLogger(MetaDataProcessor.class);
    protected static ApplicationContext applicationContext;
    private DatabaseQueryDAO databaseQueryDAO = null;
    private LookupDataDetailsDAO lookupDataDetailsDAO = null;

    public static ApplicationContext getApplicationContext() {
        if (null == applicationContext) {
            applicationContext = new ClassPathXmlApplicationContext("spring.xml");
        }
        return applicationContext;
    }

    public MetaDataProcessor() {
        this.databaseQueryDAO = (DatabaseQueryDAO)this.getBean("databaseQueryDAO");
        this.lookupDataDetailsDAO = (LookupDataDetailsDAO)this.getBean("lookupDataDetailsDAO");
    }

    public <T> T getBean(String id) {
        return (T)MetaDataProcessor.getApplicationContext().getBean(id);
    }

    public void process() throws IOException, CannotGetJdbcConnectionException, SQLException {
        LOGGER.info("||-->> All the process completed Started.\n");
        List<ExtTableFileNameDTO> extTableFileNameDTOList = this.databaseQueryDAO.generateTableFilesMapping();
        ArrayList<ExtTableFileNameDTO> extTableFileNameFinalList = new ArrayList<ExtTableFileNameDTO>();
        List<ColStartEndDTO> colStartEndDTOList = null;
        HashMap<String, List<ColStartEndDTO>> cutPositionMap = new HashMap<String, List<ColStartEndDTO>>();
        for (ExtTableFileNameDTO extTableFileNameDTO : extTableFileNameDTOList) {
            String inputData = extTableFileNameDTO.getAccessParameter();
            String inputDataRNT = inputData.replaceAll("[\r\n]+", " ");
            String inputDataBlank = inputDataRNT.replaceAll("  ", " ");
            LOGGER.info(inputDataBlank);
            if (null != inputDataBlank && inputDataBlank.contains("\"|\"")) {
                extTableFileNameDTO.setToken("PIPE");
            } else if (null != inputDataBlank && inputDataBlank.contains("\\t") && !inputDataBlank.contains("POSITION") && !inputDataBlank.contains("position")) {
                extTableFileNameDTO.setToken("TAB");
            } else if (null != inputDataBlank && inputDataBlank.contains("'\\n'") && !inputDataBlank.contains("POSITION") && !inputDataBlank.contains("position")) {
                extTableFileNameDTO.setToken("NEWLINE");
            } else if (null != inputDataBlank && inputDataBlank.contains("'\\n'") && (inputDataBlank.contains("POSITION") || inputDataBlank.contains("position"))) {
                extTableFileNameDTO.setToken("CUT_POSITION");
                colStartEndDTOList = ETFStringUtil.getColStartEndPosition(extTableFileNameDTO.getTableName(), inputDataBlank);
                cutPositionMap.put(extTableFileNameDTO.getTableName(), colStartEndDTOList);
            } else {
                extTableFileNameDTO.setToken("CUT_POSITION");
                colStartEndDTOList = ETFStringUtil.getColStartEndPosition(extTableFileNameDTO.getTableName(), inputDataBlank);
                cutPositionMap.put(extTableFileNameDTO.getTableName(), colStartEndDTOList);
            }
            extTableFileNameDTO.setColStartEndDTOList(colStartEndDTOList);
            extTableFileNameFinalList.add(extTableFileNameDTO);
        }
        Map<String, Integer> tableIdNameMap = this.lookupDataDetailsDAO.insertParentData(extTableFileNameFinalList);
        List<TableColumnTypeLengthDTO> columnTypeLengthList = this.databaseQueryDAO.generateTableColumnTypeLengthDetails();
        List<ExtTableDetailsDTO> detailsList = ETFCommonProcess.generateExtTableDetails(cutPositionMap, tableIdNameMap, columnTypeLengthList);
        int[] results = this.lookupDataDetailsDAO.insertChildData(detailsList);
        LOGGER.info("||-->> results : " + results.length);
        LOGGER.info("||-->> All the process completed successfully.\n");
    }
}

