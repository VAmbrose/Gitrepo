package com.etf.process;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import com.etf.common.ETFEmailProperties;
import com.etf.common.ETFProperties;
import com.etf.common.ETFTableProperties;
import com.etf.files.ExternalFileLoader;
import com.etf.files.FileProcessor;


/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AllFileDataProcessor {
    private static final Logger LOGGER = Logger.getLogger(AllFileDataProcessor.class);
    protected static ApplicationContext applicationContext;
    private ETFProperties etfProperties = null;
    private ETFTableProperties etfTableProperties = null;
    private ETFEmailProperties emailProperties = null;
    private ExternalFileLoader externalFileLoader = null;
    private FileProcessor fileProcessor = null;

    public static ApplicationContext getApplicationContext() {
        if (null == applicationContext) {
            applicationContext = new ClassPathXmlApplicationContext("spring.xml");
        }
        return applicationContext;
    }

    public AllFileDataProcessor() {
        this.etfProperties = (ETFProperties)this.getBean("efiProperties");
        this.etfTableProperties = (ETFTableProperties)this.getBean("efiTableProperties");
        this.emailProperties = (ETFEmailProperties)this.getBean("emailProperties");
        this.externalFileLoader = new ExternalFileLoader();
        this.fileProcessor = new FileProcessor();
    }

    @SuppressWarnings("unchecked")
	public <T> T getBean(String id) {
        return (T)AllFileDataProcessor.getApplicationContext().getBean(id);
    }

    public void process(String folderPath, String fileType , String type) throws IOException, CannotGetJdbcConnectionException, SQLException {
        LOGGER.info("||-->> All the process Started.\n");
        ETFProperties efiProperties = this.getETFProperty();
        ETFTableProperties etfTableProperties = this.getETFTableProperties();
        ETFEmailProperties emailProperties = this.getETFEmailProperties();
        Map<String, String> folderLocationMap = efiProperties.getFolderLocationMap();
        Map<String, String> tableRestrictMap = etfTableProperties.getTableProperties();
        if (null != folderLocationMap && folderLocationMap.size() > 0) {
            LOGGER.info("||-->> Total folders found size : " + folderLocationMap.size());
        } else {
            LOGGER.info("||-->> No folders found in EFI.properties file.");
        }
        if (null != tableRestrictMap && tableRestrictMap.size() > 0) {
            LOGGER.info("||-->> Total table restriction found size : " + tableRestrictMap.size());
        } else {
            LOGGER.info("||-->> Total table restriction found size : " + tableRestrictMap.size());
        }
        if (null != folderPath) {
        	String folderPathDir = "";
        	List<String> fileList = new ArrayList<String>();
        	//String pathSeperator = System.getProperty("file.separator");
        	
        	if(type.equalsIgnoreCase("SINGLE_FILE")){
        		File file = new File(fileType);
        		String fileName = file.getName();//fileType.split(pathSeperator)[fileType.split(pathSeperator).length - 1];
	            folderPathDir = fileType.replace(fileName, "");
	            File absFile = new File(folderPathDir + fileName);
	            if(absFile.exists()) {
	            	fileList.add(fileName);
	            }
        	} else {
	            folderPathDir = folderLocationMap.get(folderPath);
	            fileList = this.externalFileLoader.selectAllFilesFromDirectory(folderPathDir, fileType);
        	}
        	LOGGER.info("||-->> Folder Path : " + folderPathDir);
        	LOGGER.info("||-->> Total File found : " + (null != fileList ? fileList.size() : 0));
        	
            if (null != fileList && fileList.size() > 0) {
                for (String fileName : fileList) {
                	
                    this.fileProcessor.processFiles(folderPath, folderPathDir, fileName, emailProperties, tableRestrictMap);
                    String processedFile = folderPathDir + fileName;
                    String destinationFile = folderLocationMap.get(folderPath).replaceAll(folderPath, folderPath+"_archive") + fileName;
                    File file = new File(destinationFile);
                    if (null != file && file.exists() && !processedFile.equalsIgnoreCase(destinationFile)) {
                        LOGGER.info("||-->> File is existing in archive directory.\n");
                        file.delete();
                        LOGGER.info("||-->> Deleting the existing file and moving processed file : " + fileName + "\n");
                    }
                    if(!processedFile.equalsIgnoreCase(destinationFile))
                    	this.fileProcessor.moveFile(processedFile, destinationFile);
                    LOGGER.info("||-->> File Processed successfully : " + fileName + "\n");
                    LOGGER.info("||-->> File moved From : " + processedFile + "-->> to destination location : " + destinationFile);
                }
                
                String currentcyc = fileProcessor.getCurrcyc();
                List<String> zerofiles = fileProcessor.getZerofiles();
                if(null == currentcyc || currentcyc.isEmpty()){
                	LOGGER.info("\n\n"+"********** All are Zero Files , Please Check and run Script again *************");
                    LOGGER.info("\n\n"+"**********  Current Cycle >>> "+currentcyc+" *************");
                    LOGGER.info("\n\n"+"**********  Current Zero Files >>>  "+zerofiles+" *************");
                }
                else
                {
                	List<String> notValid = fileProcessor.notValid();
                	for (String fileName : zerofiles)
                	{
                	if(notValid.contains(fileName.toUpperCase())){
                		String result= fileProcessor.zeroSizeProcessFiles(folderPath, folderPathDir, fileName, emailProperties, tableRestrictMap);
                		LOGGER.info("\n"+"********** Result >>> "+result+"*************");
                	}              	
                	}
                }
            }
        }
        LOGGER.info("||-->> All the process completed successfully.\n");
    }
    
    public ETFProperties getETFProperty() {
        return this.etfProperties;
    }

    public ETFTableProperties getETFTableProperties() {
        return this.etfTableProperties;
    }

    public ETFEmailProperties getETFEmailProperties() {
        return this.emailProperties;
    }
}

