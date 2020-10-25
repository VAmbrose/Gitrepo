package com.etf.monitor;

import com.etf.files.FileProcessor;


import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DirectoryThread
implements Callable<List<String>> {
    private Logger LOGGER = Logger.getLogger(DirectoryThread.class.getName());
    private FileProcessor fileProcessor = null;
    private String threadId = null;
    private String folderKey = null;
    private String folderPath = null;

    public DirectoryThread(String threadId, String folderKey, String folderPath) {
        this.threadId = threadId;
        this.folderKey = folderKey;
        this.folderPath = folderPath;
        this.fileProcessor = new FileProcessor();
    }

    public List<String> call() throws Exception {
        this.LOGGER.info("call() is executed from THread " + this.threadId);
        ArrayList<String> resultList = new ArrayList<String>();
        try {
            do {
                boolean valid;
                WatchService watcher = FileSystems.getDefault().newWatchService();
                Path dir = Paths.get(this.folderPath, new String[0]);
                dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
                WatchKey key = null;
                try {
                    key = watcher.take();
                }
                catch (InterruptedException ex) {
                    return resultList;
                }
                for (WatchEvent event : key.pollEvents()) {
                    WatchEvent.Kind kind = event.kind();
                    WatchEvent ev = event;
                    Path filePath = (Path)ev.context();
                    String fileName = filePath.toString();
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE || event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        this.LOGGER.info("File Created : " + this.folderPath);
                        this.LOGGER.info("File Name : " + fileName);
                        break;
                    }
                    if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        this.LOGGER.info("File Modified : " + this.folderPath);
                        this.LOGGER.info("File Name : " + fileName);
                        break;
                    }
                    if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                        this.LOGGER.info("File Deleted : " + this.folderPath);
                        this.LOGGER.info("File Name : " + fileName);
                        break;
                    }
                    this.LOGGER.info("No event triggered");
                    if (kind != StandardWatchEventKinds.ENTRY_MODIFY || !fileName.toString().equals("DirectoryWatch.java")) continue;
                    System.out.println("My source file has changed!!!");
                }
                if (valid = key.reset()) {
                    key.cancel();
                    watcher.close();
                    continue;
                }
                break;
            } while (true);
        }
        catch (IOException ex) {
            this.LOGGER.error("Exception : " + ex.toString());
        }
        return resultList;
    }
}

