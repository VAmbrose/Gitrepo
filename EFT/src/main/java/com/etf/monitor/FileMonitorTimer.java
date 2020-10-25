package com.etf.monitor;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.etf.common.ETFEmailProperties;
import com.etf.files.FileProcessor;


/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FileMonitorTimer
extends TimerTask {
    private static final Logger LOGGER = Logger.getLogger(FileMonitorTimer.class.getName());
    long initTime = System.currentTimeMillis();
    private String threadId = null;
    private String folderKey = null;
    private String folderPath = null;
    private ETFEmailProperties emailProperties = null;
    private Map<String, String> tableRestrictMap = null;

    public FileMonitorTimer(String threadId, String folderKey, String folderPath, ETFEmailProperties emailProperties, Map<String, String> tableRestrictMap) {
        this.threadId = threadId;
        this.folderKey = folderKey;
        this.folderPath = folderPath;
        this.emailProperties = emailProperties;
        this.tableRestrictMap = tableRestrictMap;
        LOGGER.info("processAllMessage called for Thread : " + threadId + " On " + folderPath);
    }

    @Override
    public void run() {
        LOGGER.info("||---> File import service running for  : " + this.folderPath + " directory \n");
        LOGGER.info("||---> " + this.threadId + " : Monitoring directory " + (System.currentTimeMillis() - this.initTime) / 60000 + " minuts\n");
        try {
            ArrayList<String> fileProcessingList = new ArrayList<String>();
            File directory = new File(this.folderPath);
            if (directory.isDirectory()) {
                String[] fileNameList = directory.list();
                LOGGER.info("||---> Total files available in directory : " + fileNameList.length);
                long startTime = this.getStartTime();
                long endTime = this.getEndTime();
                long currentTime = this.getCurrentTime();
                LOGGER.info("||---> Start Time : " + startTime);
                LOGGER.info("||---> End Time : " + endTime);
                LOGGER.info("||---> Current Time : " + currentTime + "\n");
                for (String fileName : fileNameList) {
                    File file = new File(this.folderPath + fileName);
                    long updateFileTime = file.lastModified();
                    LOGGER.info("||---> Last Updated time : " + fileName + " : " + updateFileTime);
                    if (updateFileTime >= startTime) {
                        if (updateFileTime <= endTime) {
                            LOGGER.info("||--->> Files are ready for processing : " + fileName);
                            fileProcessingList.add(fileName);
                            continue;
                        }
                        LOGGER.info("||--->> File is copying : " + fileName);
                        continue;
                    }
                    LOGGER.info("||--->> File became old or big file in processing state : " + fileName);
                }
                fileNameList = null;
            }
            for (String fileName : fileProcessingList) {
                LOGGER.info("||---> File processing started : " + fileName);
                FileProcessor fileProcessor = new FileProcessor(this.folderKey, this.folderPath, fileName, this.emailProperties, this.tableRestrictMap);
                fileProcessor.start();
            }
            fileProcessingList.clear();
        }
        catch (Exception exception) {
            LOGGER.error("Error in file processing " + exception.toString());
        }
    }

    private long getStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.add(12, -12);
        return cal.getTimeInMillis();
    }

    private long getEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.add(12, -2);
        return cal.getTimeInMillis();
    }

    private long getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        return cal.getTimeInMillis();
    }
}

