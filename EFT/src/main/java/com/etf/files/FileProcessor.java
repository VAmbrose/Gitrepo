package com.etf.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.etf.common.ETFEmailProperties;
import com.etf.dao.CreateTableDAO;
import com.etf.dao.TableGenerationQueryDAO;
import com.etf.dto.ExtTableDetailsDTO;
import com.etf.dto.ExtTableFileNameDTO;
import com.etf.dto.FileReaderLineDTO;
import com.etf.dto.TableDetailsDTO;
import com.etf.email.SendETFStatusReport;
import com.io.index.file.reader.IndexedFileReader;

public class FileProcessor extends Thread {
	private static final Logger LOGGER = Logger.getLogger(FileProcessor.class);
	protected static ApplicationContext applicationContext;
	private TableGenerationQueryDAO tableGenerationQueryDAO = null;
	private CreateTableDAO createTableDAO = null;
	private List<ExtTableDetailsDTO> tableDetailsDTOList = null;
	private ExternalFileLoader externalFileLoader = null;
	private String folderKey = null;
	private String folderPath = null;
	private String fileName = null;
	private ETFEmailProperties emailProperties = null;
	private Map<String, String> tableRestrictMap = null;
	private List<String> zerofiles = new ArrayList<String>();
	private static String currcyc = null;
	List<String> notValid = Arrays.asList("E_L2_RATEQUOTE", "E_L2_RATEQUOTE_ERR", "E_L3_RATEQUOTE",
			"E_L3_RATEQUOTE_ERR", "E_L5_RATEQUOTE", "E_L5_RATEQUOTE_ERR", "E_L2_RATEQUOTE_INTL",
			"E_L2_RATEQUOTE_ERR_INTL", "E_L3_RATEQUOTE_INTL", "E_L3_RATEQUOTE_ERR_INTL", "E_L5_RATEQUOTE_INTL",
			"E_L5_RATEQUOTE_ERR_INTL", "E_L2_RATEQUOTE_MPS", "E_L2_RATEQUOTE_ERR_MPS", "E_L3_RATEQUOTE_MPS",
			"E_L3_RATEQUOTE_ERR_MPS", "E_L5_RATEQUOTE_MPS", "E_L5_RATEQUOTE_ERR_MPS");

	public static ApplicationContext getApplicationContext() {
		if (null == applicationContext) {
			applicationContext = new ClassPathXmlApplicationContext("spring.xml");
		}
		return applicationContext;
	}

	public <T> T getBean(String id) {
		return (T) FileProcessor.getApplicationContext().getBean(id);
	}

	public FileProcessor() {
		this.tableDetailsDTOList = new ArrayList<ExtTableDetailsDTO>();
		this.externalFileLoader = new ExternalFileLoader();
		this.createTableDAO = (CreateTableDAO) this.getBean("createTableDAO");
		this.tableGenerationQueryDAO = (TableGenerationQueryDAO) this.getBean("tableGenerationQueryDAO");
	}

	public FileProcessor(String folderKey, String folderPath, String fileName, ETFEmailProperties emailProperties,
			Map<String, String> tableRestrictMap) {
		this.folderKey = folderKey;
		this.folderPath = folderPath;
		this.fileName = fileName;
		this.emailProperties = emailProperties;
		this.tableRestrictMap = tableRestrictMap;
		this.tableDetailsDTOList = new ArrayList<ExtTableDetailsDTO>();
		this.externalFileLoader = new ExternalFileLoader();
		this.createTableDAO = (CreateTableDAO) this.getBean("createTableDAO");
		this.tableGenerationQueryDAO = (TableGenerationQueryDAO) this.getBean("tableGenerationQueryDAO");
	}

	@Override
	public void run() {
		try {
			LOGGER.info("||-->> Please wait inserting will starting soon for : " + this.fileName);
			this.processFiles(this.folderKey, this.folderPath, this.fileName, this.emailProperties,
					this.tableRestrictMap);
			String processedFile = this.folderPath + this.fileName;
			String destinationFile = this.folderPath.replaceAll("data", "archive") + this.fileName;
			File file = new File(destinationFile);
			if (null != file && file.exists()) {
				LOGGER.info("||-->> File is existing in archive directory.\n");
				file.delete();
				LOGGER.info(
						"||-->> Deleted the existing file from archive and moving processed file : " + this.fileName
								+ "\n");
				this.moveFile(processedFile, destinationFile);
				LOGGER.info("||-->> File moved successfully to archive directory : " + this.fileName
						+ "\n");
			} else {
				this.moveFile(processedFile, destinationFile);
				LOGGER.info("||-->> File moved successfully into archive directory : "
						+ this.fileName + "\n");
			}
			LOGGER.info("||-->> File moved into destination location: " + processedFile);
			LOGGER.info("||-->> Destination location: " + destinationFile);
		} catch (Exception exception) {
			LOGGER.error("Error raised : " + exception.toString());
		}
	}

	public String processFiles(String folderKey, String folderPath, String fileName,
			ETFEmailProperties emailProperties, Map<String, String> tableRestrictMap) throws IOException {
		StringBuffer resultSB = new StringBuffer();
		SendETFStatusReport etfStatusReport = new SendETFStatusReport();
		int startPositionException = 0;
		int endPositionException = 0;
		try {
			resultSB.append("File process details : " + folderKey + " : " + folderPath + " : " + fileName + ".    \n\n");
			List<ExtTableFileNameDTO> fileNameDTOList = this.tableGenerationQueryDAO.getTableFileNameList(fileName,
					folderKey);
			int totalLines = this.externalFileLoader.countLines(folderPath, fileName);
			resultSB.append("Total lines are in file : " + totalLines + "    \n\n");
			resultSB.append("List of table are associated with : " + fileName + "    \n\n");
			if (totalLines > 0) {
				int i2 = 1;
				for (ExtTableFileNameDTO tableNameDTO : fileNameDTOList) {
					resultSB.append("" + i2 + ". \"" + tableNameDTO.getTableName() + "\",\n\n");
					++i2;
				}
				etfStatusReport.sendEmailProcessing(emailProperties, fileName, resultSB.toString());
				resultSB = new StringBuffer();
				resultSB.append("File process details : " + folderKey + " : " + folderPath + " : " + fileName + "\n");
				for (ExtTableFileNameDTO dto : fileNameDTOList) {
					String tableName = dto.getTableName();
					resultSB.append("\nTable Name : \"" + tableName + "\"\n");
					ArrayList<String> createTableList = new ArrayList<String>();
					ArrayList<List<String>> allIinsertQueryList = new ArrayList<List<String>>();
					ArrayList<String> insertQueryList = new ArrayList<String>();
					this.tableDetailsDTOList = this.tableGenerationQueryDAO.getTableDetailsDTOList(tableName);
					resultSB.append("Table Number of columns : " + this.tableDetailsDTOList.size() + "\n");
					List<TableDetailsDTO> startEndList = null;
					if ("CUT_POSITION".equalsIgnoreCase(dto.getToken())) {
						resultSB.append("Processing : CUT_POSITION\n");
						startEndList = this.getStartEndList(this.tableDetailsDTOList);
					}
					resultSB.append("Total Lines in file : " + totalLines + "\n");
					List<FileReaderLineDTO> fileReaderLineDTOList = this.externalFileLoader.getFileReaderPositionList(
							totalLines, Integer.parseInt(emailProperties.getTotalLineToRead()));
					StringBuffer createTableQuerySB = new StringBuffer();
					if ("NEWLINE".equalsIgnoreCase(dto.getToken())) {
						createTableQuerySB.append("CREATE TABLE \"" + tableName + "\" (\"LINE\" VARCHAR2(1000))");
					} else {
						createTableQuerySB.append("CREATE TABLE \"" + tableName + "\" (");
					}

					// Written to avoid truncation and do update of given list
					// of tables (New Changes - Feb 2017)--- KARAN

					if (notValid.contains(tableName.toUpperCase())) {
						String delSql = deleteQuery(folderPath, fileName, totalLines, tableName);
						if (delSql != null) {
							LOGGER.info("\n\n" + "**********  Executing Delete On " + tableName + " *************");
							LOGGER.info(delSql);
							this.createTableDAO.updelDatabaseQuery(delSql, tableName, "D");
						} else
							LOGGER.info("\n\n"
									+ "********** Skipping Delete as Delete Query is not Prepared as it is Cycle 1 *************");
					}

					boolean isFirst = false;
					boolean isSecond = false;
					boolean isTabSeqCreate = false;
					for (FileReaderLineDTO fileReaderLineDTO : fileReaderLineDTOList) {
						StringBuffer insertDataSecondSB;
						StringBuffer insertDataFirstSB;
						String insertQuery;
						int x;
						int countCreateTable;
						StringBuffer insertDataFirstSB2;
						StringBuffer insertDataSecondSB2;
						int countInsertValue;
						int nomberOfColmns;
						List<Map<String, String>> fileContents = null;
						List<String> fileContentsNewLineList = null;
						List<String[]> fileContentsTabList = null;
						List<String[]> fileContentsPipeList = null;
						int startPosition = fileReaderLineDTO.getStartPosition();
						int endPosition = fileReaderLineDTO.getEndPosition();
						startPositionException = startPosition;
						endPositionException = endPosition;
						if ("TAB".equalsIgnoreCase(dto.getToken())) {
							try {
								fileContentsTabList = this.externalFileLoader.getDataFromFileByTab(folderPath,
										fileName, "\t", startPosition, endPosition, totalLines, tableName,
										tableRestrictMap);
								x = 0;
								for (String[] stringArray : fileContentsTabList) {
									insertDataFirstSB = new StringBuffer();
									insertDataSecondSB = new StringBuffer();
									insertDataFirstSB.append("INSERT INTO \"" + tableName + "\" (");
									insertDataSecondSB.append(") VALUES(");
									nomberOfColmns = this.tableDetailsDTOList.size();
									countCreateTable = 0;
									countInsertValue = 0;
									for (ExtTableDetailsDTO detailsDTO : this.tableDetailsDTOList) {
										if (!isFirst && !isSecond) {
											createTableQuerySB.append("\"" + detailsDTO.getColumnName() + "\" "
													+ detailsDTO.getColumnDatatype() + "("
													+ detailsDTO.getColumnLength() + ")");
											if (++countCreateTable < nomberOfColmns) {
												createTableQuerySB.append(", ");
											} else {
												createTableQuerySB.append(")");
											}
										}
										insertDataFirstSB.append("\"" + detailsDTO.getColumnName() + "\"");
										try {
											if (null != stringArray[x] && stringArray[x].trim().length() > 0
													&& stringArray[x].trim().length() <= detailsDTO.getColumnLength()) {
												insertDataSecondSB.append("'");
												insertDataSecondSB.append(null != stringArray[x] ? stringArray[x]
														.trim() : "");
												insertDataSecondSB.append("'");
											} else {
												insertDataSecondSB.append("''");
											}
										} catch (ArrayIndexOutOfBoundsException aiobe) {
											insertDataSecondSB.append("''");
										}
										if (countInsertValue < nomberOfColmns - 1) {
											insertDataFirstSB.append(", ");
											insertDataSecondSB.append(", ");
										}
										++countInsertValue;
										++x;
									}
									isFirst = true;
									isSecond = true;
									insertDataSecondSB.append(")");
									insertQuery = insertDataFirstSB.toString() + insertDataSecondSB.toString();
									insertQueryList.add(insertQuery);
									x = 0;
								}
								LOGGER.info("insertQueryList.size() : " + insertQueryList.size());
							} catch (Exception exception) {
								LOGGER.error(exception.toString());
							}
						} else if ("NEWLINE".equalsIgnoreCase(dto.getToken())) {
							try {
								fileContentsNewLineList = this.externalFileLoader.getDataFromFileByNewLine(folderPath,
										fileName, "\n", startPosition, endPosition, totalLines);
								for (String fileContent : fileContentsNewLineList) {
									insertDataFirstSB2 = new StringBuffer();
									insertDataSecondSB2 = new StringBuffer();
									if (null != this.tableDetailsDTOList && this.tableDetailsDTOList.size() > 0) {
										insertDataFirstSB2.append("INSERT INTO \"" + tableName + "\" (");
										insertDataSecondSB2.append(") VALUES(");
										for (ExtTableDetailsDTO detailsDTO : this.tableDetailsDTOList) {
											if (isFirst || !isSecond) {
												// empty if block
											}
											insertDataFirstSB2.append("\"" + detailsDTO.getColumnName() + "\"");
											if (null != fileContent && fileContent.trim().length() <= 1000) {
												insertDataSecondSB2.append("'");
												insertDataSecondSB2.append(null != fileContent ? fileContent.trim()
														: "");
												insertDataSecondSB2.append("'");
												continue;
											}
											insertDataSecondSB2.append("''");
										}
										isFirst = true;
										isSecond = true;
										insertDataSecondSB2.append(")");
										insertQueryList.add(insertDataFirstSB2.toString()
												+ insertDataSecondSB2.toString());
										continue;
									}
									insertDataFirstSB2.append("INSERT INTO \"" + tableName + "\" (\"LINE\") VALUES('"
											+ fileContent + "')");
									insertDataFirstSB2.append(fileContent);
									insertDataFirstSB2.append("')");
									String insertQuery2 = insertDataFirstSB2.toString()
											+ insertDataSecondSB2.toString();
									insertQueryList.add(insertQuery2);
								}
							} catch (Exception exception) {
								LOGGER.error(exception.toString());
							}
						} else if ("PIPE".equalsIgnoreCase(dto.getToken())) {
							try {
								fileContentsPipeList = this.externalFileLoader.getDataFromFileByPipe(folderPath,
										fileName, "\\|", startPosition, endPosition, totalLines, tableName,
										tableRestrictMap);
								x = 0;
								for (String[] stringPipe : fileContentsPipeList) {
									insertDataFirstSB = new StringBuffer();
									insertDataSecondSB = new StringBuffer();
									insertDataFirstSB.append("INSERT INTO \"" + tableName + "\" (");
									insertDataSecondSB.append(") VALUES(");
									nomberOfColmns = this.tableDetailsDTOList.size();
									countCreateTable = 0;
									countInsertValue = 0;
									for (ExtTableDetailsDTO detailsDTO : this.tableDetailsDTOList) {
										if (!isFirst && !isSecond) {
											createTableQuerySB.append("\"" + detailsDTO.getColumnName() + "\" "
													+ detailsDTO.getColumnDatatype() + "("
													+ detailsDTO.getColumnLength() + ")");
											if (++countCreateTable < nomberOfColmns) {
												createTableQuerySB.append(", ");
											} else {
												createTableQuerySB.append(")");
											}
										}
										insertDataFirstSB.append("\"" + detailsDTO.getColumnName() + "\"");
										try {
											if (null != stringPipe[x] && stringPipe[x].trim().length() > 0
													&& stringPipe[x].trim().length() <= detailsDTO.getColumnLength()) {
												insertDataSecondSB.append("'");
												insertDataSecondSB.append(null != stringPipe[x] ? stringPipe[x].trim()
														: "");
												insertDataSecondSB.append("'");
											} else {
												insertDataSecondSB.append("''");
											}
										} catch (ArrayIndexOutOfBoundsException aiobe) {
											LOGGER.error("Data Error at [PIPE]" + aiobe.getMessage());
											insertDataSecondSB.append("''");
										}
										if (countInsertValue < nomberOfColmns - 1) {
											insertDataFirstSB.append(", ");
											insertDataSecondSB.append(", ");
										}
										++countInsertValue;
										++x;
									}
									isFirst = true;
									isSecond = true;
									insertDataSecondSB.append(")");
									insertQuery = insertDataFirstSB.toString() + insertDataSecondSB.toString();
									insertQueryList.add(insertQuery);
									x = 0;
								}
							} catch (Exception exception) {
								LOGGER.error(exception.toString());
							}
						} else if ("CUT_POSITION".equalsIgnoreCase(dto.getToken())) {
							try {
								fileContents = this.externalFileLoader.getDataFromFile(folderPath, fileName,
										startEndList, startPosition, endPosition, totalLines, tableName,
										tableRestrictMap);
								for (Map<String, String> map : fileContents) {
									insertDataFirstSB2 = new StringBuffer();
									insertDataSecondSB2 = new StringBuffer();
									insertDataFirstSB2.append("INSERT INTO \"" + tableName + "\" (");
									insertDataSecondSB2.append(") VALUES(");
									int nomberOfColmns2 = this.tableDetailsDTOList.size();
									int countCreateTable2 = 0;
									int countInsertValue2 = 0;
									for (ExtTableDetailsDTO detailsDTO : this.tableDetailsDTOList) {
										if (!isFirst && !isSecond) {
											createTableQuerySB.append("\"" + detailsDTO.getColumnName() + "\" "
													+ detailsDTO.getColumnDatatype() + "("
													+ detailsDTO.getColumnLength() + ")");
											if (++countCreateTable2 < nomberOfColmns2) {
												createTableQuerySB.append(", ");
											} else {
												createTableQuerySB.append(")");
											}
										}
										insertDataFirstSB2.append("\"" + detailsDTO.getColumnName() + "\"");
										String colValue = map.get(detailsDTO.getColumnName());
										if (null != colValue && colValue.trim().length() > 0
												&& colValue.trim().length() <= detailsDTO.getColumnLength()) {
											insertDataSecondSB2.append("'"
													+ colValue.trim().replaceAll(",", "\\,").replaceAll("'", "") + "'");
										} else {
											insertDataSecondSB2.append("''");
										}
										if (countInsertValue2 < nomberOfColmns2 - 1) {
											insertDataFirstSB2.append(", ");
											insertDataSecondSB2.append(", ");
										}
										++countInsertValue2;
									}
									isFirst = true;
									isSecond = true;
									insertDataSecondSB2.append(")");
									String insertQuery3 = insertDataFirstSB2.toString()
											+ insertDataSecondSB2.toString();
									insertQueryList.add(insertQuery3);
								}
							} catch (Exception exception) {
								LOGGER.error("Error at [CUT_POSITION] : " + exception.toString());
							}
						}
						if (null != insertQueryList && insertQueryList.size() > 0) {
							allIinsertQueryList.add(insertQueryList);
							createTableList.add(createTableQuerySB.toString());
							if (!isTabSeqCreate) {
								LOGGER.info("||-->> Started creating table for : " + fileName);
								this.createTableDAO.executeDatabaseQuery(tableName, "TABLE", folderKey, fileName,
										createTableList);
								createTableList.clear();
								isTabSeqCreate = true;
							}
							int[] result = this.createTableDAO.insertDatabaseQuery(allIinsertQueryList);
							insertQueryList.clear();
							allIinsertQueryList.clear();
							if (totalLines == endPosition) {
								this.writeReadingStatusToFile(tableName + "," + fileName + "," + startPosition + ","
										+ endPosition + ",Completed");
							} else {
								this.writeReadingStatusToFile(tableName + "," + fileName + "," + startPosition + ","
										+ endPosition + ",Processing");
							}
						}
						LOGGER.info("||-->> Records inserted for : " + fileName + "\n\n");
					}
					fileReaderLineDTOList.clear();
				}

				etfStatusReport.sendEmailProcessed(emailProperties, fileName, resultSB.toString());
			}

			else
			{
				zerofiles.add(fileName);
				if(!notValid.contains(fileName.toUpperCase())){
					etfStatusReport.sendEmailProcessing(emailProperties, fileName, resultSB.toString());
					LOGGER.info("||-->> Zerro Size File -- No recorsd \n");
					etfStatusReport.sendEmailProcessed(emailProperties, fileName, resultSB.toString());
				}
			}
				
		} catch (Exception exception) {
			String fileDetails = "Input File : '" + fileName + "' error in between " + startPositionException + " to "
					+ endPositionException + "\n\n";
			etfStatusReport.sendEmailException(emailProperties, fileDetails + "\n\n" + exception.getMessage());
		}
		return resultSB.toString();
	}

	private void writeReadingStatusToFile(String data) throws IOException {
		File file = new File("logs/processing.txt");
		FileUtils.writeStringToFile(file, data);
	}

	private String[] readCompletedStatusFromFile() throws IOException {
		File file = new File("logs/processing.txt");
		String[] lineArray = null;
		if (file.exists()) {
			String line = FileUtils.readFileToString(file);
			lineArray = line.split(",");
		} else {
			lineArray = new String[] { "TEST", "test.txt", "0", "0", "Processing" };
		}
		return lineArray;
	}

	private List<TableDetailsDTO> getStartEndList(List<ExtTableDetailsDTO> tableDetailsDTOList) {
		ArrayList<TableDetailsDTO> detailsDTOList = new ArrayList<TableDetailsDTO>();
		if (null != tableDetailsDTOList && tableDetailsDTOList.size() > 0) {
			for (ExtTableDetailsDTO dto : tableDetailsDTOList) {
				TableDetailsDTO detailsDTO = new TableDetailsDTO();
				detailsDTO.setColumnName(dto.getColumnName());
				detailsDTO.setStartPosition(dto.getStartPosition());
				detailsDTO.setEndPosition(dto.getEndPosition());
				detailsDTOList.add(detailsDTO);
			}
		}
		return detailsDTOList;
	}

	public void moveFile(String processedFile, String destinationFile) throws IOException {
		File source = new File(processedFile);
		File destination = new File(destinationFile);
		FileUtils.moveFile(source, destination);
		File proccedFile = new File("logs/processing.txt");
		if (proccedFile.exists()) {
			proccedFile.delete();
		}
	}

	public void deleteDoneFile(String doneFile) throws IOException {
		File proccedFile = new File(doneFile);
		if (proccedFile.exists()) {
			proccedFile.delete();
			LOGGER.info("Done file deleted : " + doneFile);
		} else {
			LOGGER.info("Done file NOT deleted : " + doneFile);
		}
	}

	public void deleteProcessingFile(String processingFileName) throws IOException {
		String processingFile = processingFileName + ".processing";
		File proccedFile = new File(processingFile);
		if (proccedFile.exists()) {
			proccedFile.delete();
			LOGGER.info("Processing file deleted : " + processingFile);
		} else {
			LOGGER.info("Processing file NOT deleted : " + processingFile);
		}
	}

	public boolean isFileExists(String destinationFile) {
		boolean isFileExists = false;
		File file = new File(destinationFile);
		if (null != file) {
			isFileExists = file.exists();
		}
		return isFileExists;
	}

	// Written to avoid truncation and do update of given list of tables other
	// than C1 (New Changes - Feb 2017)--- KARAN
	private String updateQuery(String oldtablename, String currcyc) throws IOException {
		StringBuffer updateDataFirstSB = new StringBuffer();
		StringBuffer updateDataSecondSB = new StringBuffer();
		StringBuffer newtbl = new StringBuffer();
		String updateQuery = null;
		newtbl.append(oldtablename.substring(0, 7));
		newtbl.append(currcyc);
		if (oldtablename.substring(8, 9).contentEquals("0"))
			newtbl.append(oldtablename.substring(9, oldtablename.length()));
		else
			newtbl.append(oldtablename.substring(8, oldtablename.length()));
		updateDataFirstSB.append("UPDATE ignoretables SET TABLE_NAME='");
		updateDataFirstSB.append(newtbl);
		updateDataSecondSB.append("' WHERE TABLE_NAME ='");
		updateDataSecondSB.append(oldtablename);
		updateDataSecondSB.append("'");
		updateQuery = updateDataFirstSB.toString() + updateDataSecondSB.toString();
		return updateQuery;
	}

	private String deleteQuery(String folderPath, String fileName, int totallines, String tableName) throws IOException {
		IndexedFileReader reader = null;
		List<String> oldtablenames = new ArrayList<String>();
		String deteteQuery = null;
		String upSql = null;
		HashSet<String> tableset = new HashSet<String>();
		HashSet<String> testidset = new HashSet<String>();
		StringBuffer deleteDataSB = new StringBuffer();
		try {
			File file = new File(folderPath + "" + fileName);
			reader = new IndexedFileReader(file);
			SortedMap<Integer, String> lineSortedMap = reader.readLines(1, totallines);
			Collection<String> lineList = lineSortedMap.values();
			for (String line : lineList) {
				String valueString = line.replaceAll(",", "\\,").replaceAll("'", "''").trim();
				String[] valueArray = valueString.split("\t");
				tableset.add(valueArray[0]);
				testidset.add(valueArray[1]);
			}
			reader.close();
			if (tableset.size() != 0) {
				List<String> innertbls = new ArrayList<String>(tableset);
				if (innertbls.get(0).substring(8, 9).contentEquals("0"))
					currcyc = innertbls.get(0).substring(7, 9);
				else
					currcyc = innertbls.get(0).substring(7, 8);
			}
			if (!currcyc.equals("1")) {
				oldtablenames = this.createTableDAO.selectDatabaseQuery(tableName);
				for (String oldtablename : oldtablenames) {
					upSql = updateQuery(oldtablename, currcyc);
					if (upSql != null) {
						upSql = upSql.replace("ignoretables", tableName);
						LOGGER.info("\n" + "********** Executing Update On " + tableName + " *************");
						LOGGER.info(upSql);
						this.createTableDAO.updelDatabaseQuery(upSql, tableName, "U");
					}
				}
				String testIdListStr = inList(testidset);
				String tableListStr = inList(tableset);
				deleteDataSB.append("DELETE FROM " + tableName + " WHERE TABLE_NAME IN ");
				deleteDataSB.append(tableListStr);
				deleteDataSB.append(" AND ");
				deleteDataSB.append("TEST_ID IN ");
				deleteDataSB.append(testIdListStr);
				deteteQuery = deleteDataSB.toString();
			} else {
				String trSql = "TRUNCATE TABLE " + tableName;
				LOGGER.info("\n\n" + "**********  Truncating Table " + tableName
						+ " as Data is for Cycle 1 *************");
				this.createTableDAO.updelDatabaseQuery(trSql, tableName, "T");
			}
		} catch (Exception exception) {
			reader.close();
			exception.printStackTrace();
		}
		return deteteQuery;
	}

	private String inList(HashSet<String> testidset) throws IOException {
		String ListStr = "('";
		int testIdCount = 0;
		for (String testId : testidset) {
			ListStr += testId + "','";
			testIdCount++;
			if (testIdCount % 1000 == 0) {
				ListStr = ListStr.substring(0, ListStr.length() - 3);
				ListStr += "') OR TEST_ID IN ('";
			}
		}
		if (testIdCount % 1000 == 0) {
			ListStr = ListStr.substring(0, ListStr.length() - 20);
		}
		ListStr = ListStr.substring(0, ListStr.length() - 3);
		ListStr += "')";
		return ListStr;
	}

	public String zeroSizeProcessFiles(String folderKey, String folderPath, String fileName,
			ETFEmailProperties emailProperties, Map<String, String> tableRestrictMap) throws IOException {
		StringBuffer resultSB = new StringBuffer();
		List<String> oldtablenames = new ArrayList<String>();
		int i = 1;
		SendETFStatusReport etfStatusReport = new SendETFStatusReport();
		resultSB.append("File process details : " + folderKey + " : " + folderPath + " : " + fileName + ".    \n\n");
		resultSB.append("List of table are associated with : " + fileName + "    \n\n");
		List<ExtTableFileNameDTO> fileNameDTOList = this.tableGenerationQueryDAO.getTableFileNameList(fileName,
				folderKey);
		for (ExtTableFileNameDTO tableNameDTO : fileNameDTOList) {
			resultSB.append("" + i + ". \"" + tableNameDTO.getTableName() + "\",\n\n");
			++i;
		}
		etfStatusReport.sendEmailProcessing(emailProperties, fileName, resultSB.toString());

		for (ExtTableFileNameDTO dto : fileNameDTOList) {
			String tableName = dto.getTableName();
			try {

				if (!currcyc.equals("1")) {
					oldtablenames = this.createTableDAO.selectDatabaseQuery(tableName);
					for (String oldtablename : oldtablenames) {
						String upSql = updateQuery(oldtablename, currcyc);
						if (upSql != null) {
							upSql = upSql.replace("ignoretables", tableName);
							LOGGER.info("\n\n" + "**********  Executing Update On Zero Size " + tableName
									+ " *************");
							LOGGER.info(upSql);
							this.createTableDAO.updelDatabaseQuery(upSql, tableName, "U");
						}
					}
				} else {
					String trSql = "TRUNCATE TABLE " + tableName;
					LOGGER.info("\n\n" + "**********  Truncating Table " + tableName
							+ " as Data is for Cycle 1 *************");
					this.createTableDAO.updelDatabaseQuery(trSql, tableName, "T");

				}
			} catch (Exception exception) {
				etfStatusReport.sendEmailException(emailProperties, fileName + "\n\n" + exception.getMessage());
			}
			etfStatusReport.sendEmailProcessed(emailProperties, fileName, resultSB.toString());
		}
		return resultSB.toString();
	}

	public List<String> getZerofiles() {
		return zerofiles;
	}
	
	public List<String> notValid() {
		return notValid;
	}
	
	public String getCurrcyc() {
		return currcyc;
	}

	// END (New Changes - Feb 2017)--- KARAN
}
