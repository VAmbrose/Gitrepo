package com.etf.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;


public class DirectoryController {
    private Logger LOGGER = Logger.getLogger(DirectoryController.class.getName());
    private List<String> finalResultList = new ArrayList<String>();
    private String threadId = null;
    private String folderKey = null;
    private String folderPath = null;

    public DirectoryController(String threadId, String folderKey, String folderPath) {
        this.threadId = threadId;
        this.folderKey = folderKey;
        this.folderPath = folderPath;
    }

    public void processAllMessage() throws Exception {
        this.LOGGER.info("processAllMessage called for Thread : " + this.threadId);
        try {
            ExecutorService executorService = Executors.newCachedThreadPool();
            ArrayList<DirectoryThread> executorServiceResult = new ArrayList<DirectoryThread>();
            executorServiceResult.add(new DirectoryThread(this.threadId, this.folderKey, this.folderPath));
            List<Future<List<String>>> fileProcessResults = executorService.invokeAll(executorServiceResult);
            for (Future result : fileProcessResults) {
                this.finalResultList.addAll((List)result.get());
            }
        }
        catch (InterruptedException exception) {
            this.finalResultList.add(exception.toString());
        }
    }
}

