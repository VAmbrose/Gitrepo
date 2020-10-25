package com.etf.main;

import org.apache.log4j.Logger;

import com.etf.process.AllFileDataProcessor;
import com.etf.process.EventDataProcessor;
import com.etf.process.MetaDataProcessor;




public class StartExternalTableFileProcess {
    public static final Logger LOGGER = Logger.getLogger(StartExternalTableFileProcess.class);
    private MetaDataProcessor metaDataProcessor = new MetaDataProcessor();
    private AllFileDataProcessor allFileDataProcessor = new AllFileDataProcessor();
    private EventDataProcessor eventDataProcessor = new EventDataProcessor();

    public void processAll(String[] argsVal) {
        LOGGER.info("||--->> External File Import process started.");
        if (null == argsVal) {
            throw new NullPointerException();
        }
        try {
            if (argsVal.length > 0) {
                LOGGER.info("[0] : " + argsVal[0]);
                if (argsVal[0].equalsIgnoreCase("GENERATE_LOOKUP")) {
                	this.metaDataProcessor.process();
                } else if (argsVal[0].equalsIgnoreCase("ALL_FILES")) {
                    LOGGER.info("[1] : " + argsVal[1]);
                    LOGGER.info("[2] : " + argsVal[2]);
                    String folderPath = argsVal[1];
                    String fileType = argsVal[2];
                    this.allFileDataProcessor.process(folderPath, fileType, argsVal[0]);
                } else if (argsVal[0].equalsIgnoreCase("SINGLE_FILE")) {
                	if(argsVal.length == 3 ) {
                		LOGGER.info("FolderKey : " + argsVal[1]);
                        LOGGER.info("File : " + argsVal[2]);
	                	String folderPath = argsVal[1] != "" ? argsVal[1] : null;
	                    String absFileName = argsVal[2] != "" ? argsVal[2] : null;
	                    this.allFileDataProcessor.process(folderPath, absFileName, argsVal[0]);
                	}
                } else if (argsVal[0].equalsIgnoreCase("EVENT_DATA")) {
                	this.eventDataProcessor.process();
                } else {
                    LOGGER.info("----------------------------------------------------------------------------------------");
                    LOGGER.info("Please verify parameter.");
                    LOGGER.info("----------------------------------------------------------------------------------------");
                    LOGGER.info("Lookup Generation Command : java -jar EFI-jar-with-dependencies.jar GENERATE_LOOKUP");
                    LOGGER.info("Import Files Command :      java -jar EFI-jar-with-dependencies.jar ALL_FILES [EXT_RSC_L2/EXT_RSC_L3/EXT_RSC_L5/EXT_REV_L2/EXT_REV_L3/EXT_REV_L5] [.txt/.out]");
                    LOGGER.info("Activate Events Command :   java -jar EFI-jar-with-dependencies.jar EVENT_DATA");
                }
            } else {
                LOGGER.info("=========================================================================================");
                LOGGER.info("Please verify.");
                LOGGER.info("----------------------------------------------------------------------------------------");
                LOGGER.info("Lookup Generation Command : java -jar EFI-jar-with-dependencies.jar GENERATE_LOOKUP");
                LOGGER.info("Import Files Command :      java -jar EFI-jar-with-dependencies.jar ALL_FILES [EXT_RSC_L2/EXT_RSC_L3/EXT_RSC_L5/EXT_REV_L2/EXT_REV_L3/EXT_REV_L5] [.txt/.out]");
                LOGGER.info("Activate Events Command :   java -jar EFI-jar-with-dependencies.jar EVENT_DATA");
            }
        }
        catch (Exception exception) {
            LOGGER.error(exception.getMessage());
            exception.printStackTrace();
        }
        LOGGER.info("||--->> External File Import process Completed.");
    }

    public static void main(String[] args) {
        new StartExternalTableFileProcess().processAll(args);
    	/*String[] args1 = new String [] {"SINGLE_FILE", "EXT_RSC_L3", "C:\\Users\\SS5037966\\Desktop\\TestInputFiles\\RateQuoteIntl.txt"};
    	new StartExternalTableFileProcess().processAll(args1);*/
    }
}

