
package com.etf.files;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import com.etf.dto.FileReaderLineDTO;
import com.etf.dto.TableDetailsDTO;
import com.io.index.file.reader.IndexedFileReader;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ExternalFileLoader {
    private static final Logger LOG = Logger.getLogger(ExternalFileLoader.class);

    public List<String> selectAllFilesFromDirectory(String folderLocation, String fileTypeTxt) {
        LOG.info("||-->> Starting process to select all files names from : " + folderLocation);
        ArrayList<String> fileList = null;
        if (null != folderLocation) {
            fileList = new ArrayList<String>();
            try {
                File dir = new File(folderLocation);
                for (File file : dir.listFiles()) {
                    if (!file.getName().endsWith(fileTypeTxt)) continue;
                    fileList.add(file.getName());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        LOG.info("||-->> Completed process to select all files names from : " + folderLocation);
        return fileList;
    }

    public List<Map<String, String>> getDataFromFile(String folderPath, String fileName, List<TableDetailsDTO> startEndList, int startPosition, int endPosition, int totalLines, String restrictTableName, Map<String, String> tableRestrictMap) throws IOException {
        LOG.info("||-->> Started getting data from : " + folderPath + fileName);
        ArrayList<Map<String, String>> fileContent = new ArrayList<Map<String, String>>();
        IndexedFileReader reader = null;
        boolean isContains = tableRestrictMap.containsKey(restrictTableName.toUpperCase());
        boolean equalCondition = true;
        if (isContains) {
            String restrictTableDetails = tableRestrictMap.get(restrictTableName);
            String[] restrictTableDetailsArray = null;
            if (null != restrictTableDetails && (restrictTableDetailsArray = restrictTableDetails.split(","))[2].contains("!")) {
                equalCondition = false;
            }
            try {
                File file = new File(folderPath + "" + fileName);
                reader = new IndexedFileReader(file);
                LOG.info("Reading line data started from : " + startPosition + " To : " + endPosition + " Total Lines : " + totalLines);
                SortedMap<Integer, String> lineSortedMap = reader.readLines(startPosition, endPosition);
                Collection<String> lineList = lineSortedMap.values();
                for (String line : lineList) {
                    String value;
                    HashMap<String, String> map = new HashMap<String, String>();
                    for (int i2 = 0; null != startEndList && i2 < startEndList.size(); ++i2) {
                        try {
                            int a2 = startEndList.get(i2).getStartPosition();
                            int b2 = startEndList.get(i2).getEndPosition();
                            int lineLength = line.length();
                            String columnValue = null;
                            columnValue = b2 <= lineLength ? line.substring(a2 - 1, b2) : line.substring(a2 - 1, lineLength);
                            map.put(startEndList.get(i2).getColumnName(), columnValue);
                            continue;
                        }
                        catch (IndexOutOfBoundsException boundsException) {
                            map.put(startEndList.get(i2).getColumnName(), "");
                        }
                    }
                    if (equalCondition) {
                        String value2;
                        if (null == restrictTableDetailsArray || restrictTableDetailsArray.length <= 0 || null == (value2 = (String)map.get(restrictTableDetailsArray[0].toUpperCase())) || !value2.trim().equalsIgnoreCase(restrictTableDetailsArray[2])) continue;
                        fileContent.add(map);
                        continue;
                    }
                    if (null == restrictTableDetailsArray || restrictTableDetailsArray.length <= 0 || null == (value = (String)map.get(restrictTableDetailsArray[0])) || value.trim().equalsIgnoreCase(restrictTableDetailsArray[2].replaceAll("!", ""))) continue;
                    fileContent.add(map);
                }
                reader.close();
            }
            catch (Exception exception) {
                reader.close();
                exception.printStackTrace();
            }
        } else {
            try {
                File file = new File(folderPath + "" + fileName);
                reader = new IndexedFileReader(file);
                LOG.info("Reading line data started from : " + startPosition + " To : " + endPosition + " Total Lines : " + totalLines);
                SortedMap<Integer, String> lineSortedMap = reader.readLines(startPosition, endPosition);
                Collection<String> lineList = lineSortedMap.values();
                for (String line : lineList) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    for (int i3 = 0; null != startEndList && i3 < startEndList.size(); ++i3) {
                        try {
                            int a3 = startEndList.get(i3).getStartPosition();
                            int b3 = startEndList.get(i3).getEndPosition();
                            int lineLength = line.length();
                            String columnValue = null;
                            columnValue = b3 <= lineLength ? line.substring(a3 - 1, b3).replaceAll("'", "''") : line.substring(a3 - 1, lineLength).replaceAll("'", "''");
                            map.put(startEndList.get(i3).getColumnName(), columnValue);
                            continue;
                        }
                        catch (IndexOutOfBoundsException boundsException) {
                            map.put(startEndList.get(i3).getColumnName(), "");
                        }
                    }
                    fileContent.add(map);
                }
                reader.close();
            }
            catch (Exception exception) {
                reader.close();
                exception.printStackTrace();
            }
        }
        LOG.info("||-->> Completed getting data from : " + folderPath + fileName);
        return fileContent;
    }

    public List<String> getDataFromFileByNewLine(String folderPath, String fileName, String token, int startPosition, int endPosition, int totalLines) throws IOException {
        LOG.info("||-->> Started getting data from : " + folderPath + fileName);
        ArrayList<String> fileContent = new ArrayList<String>();
        IndexedFileReader reader = null;
        try {
            File file = new File(folderPath + "" + fileName);
            reader = new IndexedFileReader(file);
            LOG.info("Reading line data started from : " + startPosition + " To : " + endPosition + " Total Lines : " + totalLines);
            SortedMap<Integer, String> lineSortedMap = reader.readLines(startPosition, endPosition);
            Collection<String> lineList = lineSortedMap.values();
            for (String line : lineList) {
                String valueString = line.replaceAll(",", "\\,").replaceAll("'", "''").trim();
                fileContent.add(valueString);
            }
            reader.close();
        }
        catch (Exception exception) {
            reader.close();
            exception.printStackTrace();
        }
        LOG.info("||-->> Completed getting data from : " + folderPath + fileName);
        return fileContent;
    }

    public List<String[]> getDataFromFileByPipe(String folderPath, String fileName, String token, int startPosition, int endPosition, int totalLines, String restrictTableName, Map<String, String> tableRestrictMap) throws IOException {
        LOG.info("||-->> Started getting data from : " + folderPath + fileName);
        ArrayList<String[]> fileContent = new ArrayList<String[]>();
        IndexedFileReader reader = null;
        boolean isContains = tableRestrictMap.containsKey(restrictTableName.toUpperCase());
        boolean equalCondition = true;
        if (isContains) {
            String restrictTableDetails = tableRestrictMap.get(restrictTableName.toUpperCase());
            String[] restrictTableDetailsArray = null;
            if (null != restrictTableDetails && (restrictTableDetailsArray = restrictTableDetails.split(","))[2].contains("!")) {
                equalCondition = false;
            }
            try {
                File file = new File(folderPath + "" + fileName);
                reader = new IndexedFileReader(file);
                LOG.info("Reading line data started from : " + startPosition + " To : " + endPosition + " Total Lines : " + totalLines);
                SortedMap<Integer, String> lineSortedMap = reader.readLines(startPosition, endPosition);
                Collection<String> lineList = lineSortedMap.values();
                LOG.info("lineList : " + lineList.size());
                for (String line : lineList) {
                    String value;
                    String value1 = line.replaceAll(",", "\\,").replaceAll("'", "''").trim();
                    String[] valueArray = value1.split(token);
                    if (equalCondition) {
                        if (null == restrictTableDetailsArray || restrictTableDetailsArray.length <= 0 || null == (value = valueArray[Integer.parseInt(restrictTableDetailsArray[1])]) || !value.equalsIgnoreCase(restrictTableDetailsArray[2])) continue;
                        fileContent.add(valueArray);
                        continue;
                    }
                    if (null == restrictTableDetailsArray || restrictTableDetailsArray.length <= 0 || null == (value = valueArray[Integer.parseInt(restrictTableDetailsArray[1])]) || value.equalsIgnoreCase(restrictTableDetailsArray[2].replaceAll("!", ""))) continue;
                    fileContent.add(valueArray);
                }
                reader.close();
            }
            catch (Exception exception) {
                reader.close();
                exception.printStackTrace();
            }
        } else {
            try {
                File file = new File(folderPath + "" + fileName);
                reader = new IndexedFileReader(file);
                LOG.info("Reading line data started from : " + startPosition + " To : " + endPosition + " Total Lines : " + totalLines);
                SortedMap<Integer, String> lineSortedMap = reader.readLines(startPosition, endPosition);
                Collection<String> lineList = lineSortedMap.values();
                LOG.info("lineList : " + lineList.size());
                for (String line : lineList) {
                    String value1 = line.replaceAll(",", "\\,").replaceAll("'", "''");
                    String[] valueArray = value1.split(token);
                    fileContent.add(valueArray);
                }
                reader.close();
            }
            catch (Exception exception) {
                reader.close();
                exception.printStackTrace();
            }
        }
        LOG.info("||-->> Completed getting data from : " + folderPath + fileName);
        return fileContent;
    }

    public List<String[]> getDataFromFileByTab(String folderPath, String fileName, String token, int startPosition, int endPosition, int totalLines, String restrictTableName, Map<String, String> tableRestrictMap) throws IOException {
        LOG.info("||-->> Started getting data from : " + folderPath + fileName);
        ArrayList<String[]> fileContent = new ArrayList<String[]>();
        IndexedFileReader reader = null;
        boolean isContains = tableRestrictMap.containsKey(restrictTableName.toUpperCase());
        boolean equalCondition = true;
        if (isContains) {
            String restrictTableDetails = tableRestrictMap.get(restrictTableName.toUpperCase());
            String[] restrictTableDetailsArray = null;
            if (null != restrictTableDetails && (restrictTableDetailsArray = restrictTableDetails.split(","))[2].contains("!")) {
                equalCondition = false;
            }
            try {
                File file = new File(folderPath + "" + fileName);
                reader = new IndexedFileReader(file);
                LOG.info("Reading line data started from : " + startPosition + " To : " + endPosition + " Total Lines : " + totalLines);
                SortedMap<Integer, String> lineSortedMap = reader.readLines(startPosition, endPosition);
                Collection<String> lineList = lineSortedMap.values();
                for (String line : lineList) {
                    String value;
                    String valueString = line.replaceAll(",", "\\,").replaceAll("'", "''").trim();
                    String[] valueArray = valueString.split(token);
                    if (equalCondition) {
                        if (null == restrictTableDetailsArray || restrictTableDetailsArray.length <= 0 || null == (value = valueArray[Integer.parseInt(restrictTableDetailsArray[1])]) || !value.equalsIgnoreCase(restrictTableDetailsArray[2])) continue;
                        fileContent.add(valueArray);
                        continue;
                    }
                    if (null == restrictTableDetailsArray || restrictTableDetailsArray.length <= 0 || null == (value = valueArray[Integer.parseInt(restrictTableDetailsArray[1])]) || value.equalsIgnoreCase(restrictTableDetailsArray[2].replaceAll("!", ""))) continue;
                    fileContent.add(valueArray);
                }
                reader.close();
            }
            catch (Exception exception) {
                reader.close();
                exception.printStackTrace();
            }
        } else {
            try {
                File file = new File(folderPath + "" + fileName);
                reader = new IndexedFileReader(file);
                LOG.info("Reading line data started from : " + startPosition + " To : " + endPosition + " Total Lines : " + totalLines);
                SortedMap<Integer, String> lineSortedMap = reader.readLines(startPosition, endPosition);
                Collection<String> lineList = lineSortedMap.values();
                for (String line : lineList) {
                    String valueString = line.replaceAll(",", "\\,").replaceAll("'", "''").trim();
                    String[] valueArray = valueString.split(token);
                    fileContent.add(valueArray);
                }
                reader.close();
            }
            catch (Exception exception) {
                reader.close();
                exception.printStackTrace();
            }
        }
        LOG.info("||-->> Data reading completed");
        return fileContent;
    }

    public int countLines(String folderPath, String fileName) throws IOException {
        LineNumberReader reader = new LineNumberReader(new FileReader(folderPath + fileName));
        int cnt = 0;
        String lineRead = "";
        while ((lineRead = reader.readLine()) != null) {
        }
        cnt = reader.getLineNumber();
        reader.close();
        return cnt;
    }

    public List<FileReaderLineDTO> getFileReaderPositionList(int totalLines, int maxSize) {
        ArrayList<FileReaderLineDTO> totaLinesPositionList = new ArrayList<FileReaderLineDTO>();
        if (totalLines > 0 && maxSize > 0) {
            int modulo = totalLines / maxSize;
            int remains = totalLines % maxSize;
            int startPosition = 0;
            int endPosition = 0;
            for (int i2 = 0; i2 < modulo; ++i2) {
                FileReaderLineDTO dto = new FileReaderLineDTO();
                startPosition = i2 * maxSize + 1;
                endPosition = i2 * maxSize + maxSize;
                dto.setStartPosition(startPosition);
                dto.setEndPosition(endPosition);
                totaLinesPositionList.add(dto);
            }
            if (modulo == 0 && remains > 0) {
                FileReaderLineDTO dto = new FileReaderLineDTO();
                dto.setStartPosition(1);
                dto.setEndPosition(remains);
                totaLinesPositionList.add(dto);
            } else if (remains > 0) {
                FileReaderLineDTO dto = new FileReaderLineDTO();
                dto.setStartPosition(endPosition + 1);
                dto.setEndPosition(endPosition + remains);
                totaLinesPositionList.add(dto);
            }
        }
        return totaLinesPositionList;
    }

    public List<FileReaderLineDTO> getSubList(int maxSizeOfList, int maxSize) {
        ArrayList<FileReaderLineDTO> totaLinesPositionList = new ArrayList<FileReaderLineDTO>();
        if (maxSizeOfList > 0 && maxSize > 0) {
            int modulo = maxSizeOfList / maxSize;
            int remains = maxSizeOfList % maxSize;
            int startPosition = 0;
            int endPosition = 0;
            for (int i2 = 0; i2 < modulo; ++i2) {
                FileReaderLineDTO dto = new FileReaderLineDTO();
                startPosition = i2 * maxSize + 1;
                endPosition = i2 * maxSize + maxSize;
                dto.setStartPosition(startPosition);
                dto.setEndPosition(endPosition);
                totaLinesPositionList.add(dto);
            }
            if (modulo == 0 && remains > 0) {
                FileReaderLineDTO dto = new FileReaderLineDTO();
                dto.setStartPosition(1);
                dto.setEndPosition(remains);
                totaLinesPositionList.add(dto);
            } else if (remains > 0) {
                FileReaderLineDTO dto = new FileReaderLineDTO();
                dto.setStartPosition(endPosition + 1);
                dto.setEndPosition(endPosition + remains);
                totaLinesPositionList.add(dto);
            }
        }
        return totaLinesPositionList;
    }
}

