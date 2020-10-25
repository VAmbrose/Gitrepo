package com.etf.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.etf.dto.ColStartEndDTO;
import com.etf.dto.ExtTableDetailsDTO;
import com.etf.dto.TableColumnTypeLengthDTO;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ETFCommonProcess {
    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static List<ExtTableDetailsDTO> generateExtTableDetails(Map<String, List<ColStartEndDTO>> map, Map<String, Integer> tableIdNameMap, List<TableColumnTypeLengthDTO> columnTypeLengthList) {
        ArrayList<ExtTableDetailsDTO> detailsList = new ArrayList<ExtTableDetailsDTO>();
        if (null != map && map.size() > 0 && null != columnTypeLengthList && columnTypeLengthList.size() > 0) {
            for (TableColumnTypeLengthDTO columnTypeLengthDTO : columnTypeLengthList) {
                ExtTableDetailsDTO extTableDetailsDTO = new ExtTableDetailsDTO();
                List<ColStartEndDTO> colStartEndDTOs = null;
                colStartEndDTOs = map.get(columnTypeLengthDTO.getTableName());
                boolean status = false;
                if (null != colStartEndDTOs) {
                    for (ColStartEndDTO dto : colStartEndDTOs) {
                        if (status || !columnTypeLengthDTO.getColumnName().equalsIgnoreCase(dto.getColumnName())) continue;
                        extTableDetailsDTO.setEtfId(tableIdNameMap.get(dto.getTableName()));
                        extTableDetailsDTO.setStartPosition(dto.getStartPosition());
                        extTableDetailsDTO.setEndPosition(dto.getEndPosition());
                        extTableDetailsDTO.setColumnPosition(columnTypeLengthDTO.getColumnId());
                        extTableDetailsDTO.setColumnName(columnTypeLengthDTO.getColumnName());
                        extTableDetailsDTO.setColumnDatatype(columnTypeLengthDTO.getDataType());
                        extTableDetailsDTO.setColumnLength(columnTypeLengthDTO.getDataLength());
                        detailsList.add(extTableDetailsDTO);
                        status = true;
                    }
                    continue;
                }
                extTableDetailsDTO.setEtfId(tableIdNameMap.get(columnTypeLengthDTO.getTableName()));
                extTableDetailsDTO.setColumnPosition(columnTypeLengthDTO.getColumnId());
                extTableDetailsDTO.setColumnName(columnTypeLengthDTO.getColumnName());
                extTableDetailsDTO.setColumnDatatype(columnTypeLengthDTO.getDataType());
                extTableDetailsDTO.setColumnLength(columnTypeLengthDTO.getDataLength());
                extTableDetailsDTO.setStartPosition(0);
                extTableDetailsDTO.setEndPosition(0);
                detailsList.add(extTableDetailsDTO);
            }
            return detailsList;
        }
        if (null == columnTypeLengthList || columnTypeLengthList.size() <= 0) return detailsList;
        for (TableColumnTypeLengthDTO columnTypeLengthDTO : columnTypeLengthList) {
            ExtTableDetailsDTO extTableDetailsDTO = new ExtTableDetailsDTO();
            extTableDetailsDTO.setEtfId(tableIdNameMap.get(columnTypeLengthDTO.getTableName()));
            extTableDetailsDTO.setColumnPosition(columnTypeLengthDTO.getColumnId());
            extTableDetailsDTO.setColumnName(columnTypeLengthDTO.getColumnName());
            extTableDetailsDTO.setColumnDatatype(columnTypeLengthDTO.getDataType());
            extTableDetailsDTO.setColumnLength(columnTypeLengthDTO.getDataLength());
            extTableDetailsDTO.setStartPosition(0);
            extTableDetailsDTO.setEndPosition(0);
            detailsList.add(extTableDetailsDTO);
        }
        return detailsList;
    }
}

