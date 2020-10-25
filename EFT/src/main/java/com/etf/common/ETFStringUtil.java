package com.etf.common;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.etf.dto.ColStartEndDTO;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ETFStringUtil {
    public static final List<ColStartEndDTO> getColStartEndPosition(String tableName, String inputPositions) {
        ArrayList<ColStartEndDTO> colStartEndDTOList = new ArrayList<ColStartEndDTO>();
        if (null == inputPositions) {
            throw new NullPointerException();
        }
        String inputDataString = inputPositions.replaceAll("\n", "");
        if (!inputDataString.contains("|") || !inputDataString.contains("\t")) {
            try {
                if (null != inputDataString && (inputDataString.contains("FIELDS") || inputDataString.contains("fields"))) {
                    String[] columnPositionArray;
                    String a2 = null;
                    a2 = inputDataString.indexOf("FIELDS") > 0 ? inputDataString.substring(inputDataString.indexOf("FIELDS")) : inputDataString.substring(inputDataString.indexOf("fields"));
                    String b2 = a2.replaceAll("POSITION", "").replaceAll("position", "").replaceAll("\t", "");
                    String c2 = b2.replaceAll("FIELDS", "").replaceAll("fields", "").replaceAll("LRTRIM", "").replaceAll("LTRIM", "").replaceAll("RTRIM", "");
                    String d2 = c2.substring(c2.indexOf("("));
                    String e2 = d2.replaceFirst("\\(", "");
                    String f2 = e2.replaceAll("\\)", "").trim();
                    for (String columnPosition : columnPositionArray = f2.split(",")) {
                        String[] startEnd;
                        ColStartEndDTO colStartEndDTO = new ColStartEndDTO();
                        if (!columnPosition.contains(":") && !columnPosition.contains("-")) continue;
                        String[] colPos = columnPosition.split("\\(");
                        colStartEndDTO.setColumnName(colPos[0].trim());
                        if (null != colPos[1] && colPos[1].contains(":")) {
                            startEnd = colPos[1].split(":");
                            colStartEndDTO.setStartPosition(Integer.parseInt(startEnd[0].trim()));
                            colStartEndDTO.setEndPosition(Integer.parseInt(startEnd[1].trim()));
                        } else if (null != colPos[1] && colPos[1].contains("-")) {
                            startEnd = colPos[1].split("-");
                            colStartEndDTO.setStartPosition(Integer.parseInt(startEnd[0].trim()));
                            colStartEndDTO.setEndPosition(Integer.parseInt(startEnd[1].trim()));
                        }
                        colStartEndDTO.setTableName(tableName);
                        colStartEndDTOList.add(colStartEndDTO);
                    }
                }
            }
            catch (Exception exception) {
                System.out.println(tableName);
                exception.printStackTrace();
            }
            return colStartEndDTOList;
        }
        return null;
    }

    public static final Timestamp convertStringToTimestamp(String stringDate) {
        Timestamp timestamp = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date parsedDate = dateFormat.parse(stringDate);
            timestamp = new Timestamp(parsedDate.getTime());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return timestamp;
    }
}

