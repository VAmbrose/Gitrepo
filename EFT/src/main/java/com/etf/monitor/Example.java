package com.etf.monitor;

import java.io.File;
import java.io.PrintStream;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

public class Example
implements FileAlterationListener {
    public void prepare(String path) throws Exception {
        File directory = new File(path);
        FileAlterationObserver observer = new FileAlterationObserver(directory);
        observer.addListener(this);
        final FileAlterationMonitor monitor = new FileAlterationMonitor(10);
        monitor.addObserver(observer);
        monitor.start();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){

            public void run() {
                try {
                    System.out.println("Stopping monitor.");
                    monitor.stop();
                }
                catch (Exception ignored) {
                    // empty catch block
                }
            }
        }));
    }

    public void onDirectoryChange(File arg0) {
    }

    public void onDirectoryCreate(File arg0) {
    }

    public void onDirectoryDelete(File arg0) {
    }

    public void onFileChange(File arg0) {
    }

    public void onFileCreate(File arg0) {
    }

    public void onFileDelete(File arg0) {
    }

    public void onStart(FileAlterationObserver arg0) {
    }

    public void onStop(FileAlterationObserver arg0) {
    }

}

