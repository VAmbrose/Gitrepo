package com.etf.monitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.etf.common.ETFEmailProperties;
import com.etf.scheduler.AppAliveScheduler;


public class DirectoryWatch
extends Thread {
    private static final Logger LOGGER = Logger.getLogger(DirectoryWatch.class);
    private String threadId = null;
    private String folderKey = null;
    private String folderPath = null;
    private ETFEmailProperties emailProperties = null;

    public DirectoryWatch(String threadId, String folderKey, String folderPath, ETFEmailProperties emailProperties) {
        this.threadId = threadId;
        this.folderKey = folderKey;
        this.folderPath = folderPath;
        this.emailProperties = emailProperties;
        LOGGER.info("processAllMessage called for Thread : " + threadId + " On " + folderPath);
        DirectoryWatch.keepAlivePort(threadId + " || " + folderKey + " || " + folderPath + " || ");
        DirectoryWatch.keepDirectoryWatch(threadId, folderKey, folderPath, emailProperties);
    }

    private static void keepAlivePort(String threadId) {
        long interval = 1200000;
        AppAliveScheduler scheduler = new AppAliveScheduler(threadId);
        Timer timer = new Timer();
        timer.schedule((TimerTask)scheduler, new Date(), interval);
    }

    private static void keepDirectoryWatch(String threadId, String folderKey, String folderPath, ETFEmailProperties emailProperties) {
        long interval = 600000;
    }

    public void run() {
        LOGGER.info("run method started on : " + this.threadId + " On " + this.folderPath + "\n");
        try {
            boolean valid;
            WatchKey key;
            block4 : do {
                WatchService watcher = FileSystems.getDefault().newWatchService();
                Path dir = Paths.get(this.folderPath, new String[0]);
                dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
                key = null;
                try {
                    key = watcher.take();
                }
                catch (InterruptedException ex) {
                    return;
                }
                for (WatchEvent event : key.pollEvents()) {
                    WatchEvent.Kind kind = event.kind();
                    WatchEvent ev = event;
                    Path filePath = (Path)ev.context();
                    String fileName = filePath.toString();
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        LOGGER.info("||--->> File Created : " + this.folderPath + fileName);
                        LOGGER.info("||--->> File is still coping into destination directory...");
                        LOGGER.info("Creating file: " + filePath);
                        LOGGER.info("Finished creating file! : " + fileName);
                        continue block4;
                    }
                    if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        LOGGER.info("||--->> File Modified : " + this.folderPath + fileName);
                        continue block4;
                    }
                    if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                        LOGGER.info("File Deleted : " + this.folderPath);
                        LOGGER.info("File Name : " + fileName);
                        continue block4;
                    }
                    LOGGER.info("No event triggered");
                    if (kind != StandardWatchEventKinds.ENTRY_MODIFY || !fileName.toString().equals("DirectoryWatch.java")) continue;
                    LOGGER.info("My source file has changed!!!");
                }
            } while (valid = key.reset());
            LOGGER.info("!!! Key getting reset !!!");
        }
        catch (IOException ex) {
            LOGGER.error("Exception : " + ex.toString());
        }
    }

    private void createProcessingFiles(String folderPath, String fileName) {
        String pathname = folderPath + fileName + ".processing";
        try {
            File file = new File(pathname);
            FileUtils.touch(file);
            LOGGER.info(".processing files created : " + pathname);
        }
        catch (Exception exception) {
            LOGGER.error("Not anle to create .processing file : " + pathname);
        }
    }
}

