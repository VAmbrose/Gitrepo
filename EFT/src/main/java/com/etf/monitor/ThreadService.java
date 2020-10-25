package com.etf.monitor;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ThreadService
extends Thread {
    private String path;
    private List<String> fileList = new ArrayList<String>();

    public ThreadService(String path) {
        this.path = path;
    }

    @Override
    public void run() {
        this.monitorDirectory(this.path);
    }

    public void monitorDirectory(String path) {
        try {
            boolean valid;
            WatchKey key;
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path dir = Paths.get(path, new String[0]);
            dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
            System.out.println("Watch Service registered for dir: " + dir.getFileName());
            do {
                try {
                    key = watcher.take();
                }
                catch (InterruptedException ex) {
                    return;
                }
                for (WatchEvent event : key.pollEvents()) {
                    WatchEvent.Kind kind = event.kind();
                    WatchEvent ev = event;
                    Path fileName = (Path)ev.context();
                    this.fileList.add(fileName.toString());
                    System.out.println(kind.name() + ": " + fileName);
                    if (kind != StandardWatchEventKinds.ENTRY_MODIFY || !fileName.toString().equals("DirectoryWatch.java")) continue;
                    System.out.println("My source file has changed!!!");
                }
            } while (valid = key.reset());
        }
        catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public List<String> getFileList() {
        return this.fileList;
    }
}

