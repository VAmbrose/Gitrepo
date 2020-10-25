package com.etf.process;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.etf.common.ETFEmailProperties;
import com.etf.common.ETFProperties;
import com.etf.common.ETFTableProperties;
import com.etf.monitor.FileMonitorTimer;


/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EventDataProcessor {
    private static final Logger LOGGER = Logger.getLogger(EventDataProcessor.class);
    protected static ApplicationContext applicationContext;
    private ETFProperties efiProperties = null;
    private ETFTableProperties etfTableProperties = null;
    private ETFEmailProperties emailProperties = null;

    public static ApplicationContext getApplicationContext() {
        if (null == applicationContext) {
            applicationContext = new ClassPathXmlApplicationContext("spring.xml");
        }
        return applicationContext;
    }

    public EventDataProcessor() {
        this.efiProperties = (ETFProperties)this.getBean("efiProperties");
        this.etfTableProperties = (ETFTableProperties)this.getBean("efiTableProperties");
        this.emailProperties = (ETFEmailProperties)this.getBean("emailProperties");
    }

    public <T> T getBean(String id) {
        return (T)EventDataProcessor.getApplicationContext().getBean(id);
    }

    public void process() throws IOException {
        LOGGER.info("||-->> All the process Started...\n");
        try {
            ETFProperties efiProperties = this.getEFIProperty();
            ETFTableProperties etfTableProperties = this.getETFTableProperties();
            Map<String, String> etfTablePropertiesMap = etfTableProperties.getTableProperties();
            Map<String, String> folderLocationMap = efiProperties.getFolderLocationMap();
            Set<String> allKeySet = folderLocationMap.keySet();
            String[] folderPathArray = allKeySet.toArray(new String[allKeySet.size()]);
            if (null != folderLocationMap && folderLocationMap.size() > 0) {
                LOGGER.info("||-->> Total folders found size : " + folderLocationMap.size());
            } else {
                LOGGER.info("||-->> No folders found in EFI.properties file.");
            }
            for (int i2 = 0; i2 < folderPathArray.length; ++i2) {
                String folderPath = folderLocationMap.get(folderPathArray[i2]);
                LOGGER.info("||-->> Watcher Started on Filder : " + folderPath);
                this.keepDirectoryWatch("Thread-" + (i2 + 1), folderPathArray[i2], folderPath, this.emailProperties, etfTablePropertiesMap);
            }
        }
        catch (Exception e) {
            LOGGER.error("Exception : " + e.toString());
        }
        LOGGER.info("||-->> All the process completed successfully.\n");
    }

    public ETFProperties getEFIProperty() {
        return this.efiProperties;
    }

    public ETFTableProperties getETFTableProperties() {
        return this.etfTableProperties;
    }

    private void keepDirectoryWatch(String threadId, String folderKey, String folderPath, ETFEmailProperties emailProperties, Map<String, String> etfTablePropertiesMap) {
        long interval = 600000;
        FileMonitorTimer fileMonitorTimer = new FileMonitorTimer(threadId, folderKey, folderPath, emailProperties, etfTablePropertiesMap);
        Timer timer = new Timer();
        timer.schedule((TimerTask)fileMonitorTimer, new Date(), interval);
    }
}

