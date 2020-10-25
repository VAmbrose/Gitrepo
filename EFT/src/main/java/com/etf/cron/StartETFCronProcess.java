package com.etf.cron;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StartETFCronProcess {
    public static final Logger LOGGER = Logger.getLogger(StartETFCronProcess.class);

    public static void main(String[] args) {
        LOGGER.info("<<-->> Started CSR Process.");
        try {
            new ClassPathXmlApplicationContext("spring-quartz.xml");
        }
        catch (Exception exception) {
            LOGGER.error("Error in loading spring-quartz.xml");
            LOGGER.error("Exception in loading " + exception);
        }
    }
}

