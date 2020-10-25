package com.etf.monitor;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

public class FileAlterationListenerImpl
implements FileAlterationListener {
    public void onStart(FileAlterationObserver observer) {
        System.out.println("The WindowsFileListener has started on " + observer.getDirectory().getAbsolutePath());
    }

    public void onDirectoryCreate(File directory) {
        System.out.println(directory.getAbsolutePath() + " was created.");
    }

    public void onDirectoryChange(File directory) {
        System.out.println(directory.getAbsolutePath() + " wa modified");
    }

    public void onDirectoryDelete(File directory) {
        System.out.println(directory.getAbsolutePath() + " was deleted.");
    }

    public void onFileCreate(File file) {
        System.out.println(file.getAbsoluteFile() + " was created.");
        System.out.println("----------> length: " + file.length());
        System.out.println("----------> last modified: " + new Date(file.lastModified()));
        System.out.println("----------> readable: " + file.canRead());
        System.out.println("----------> writable: " + file.canWrite());
        System.out.println("----------> executable: " + file.canExecute());
    }

    public void onFileChange(File file) {
        System.out.println(file.getAbsoluteFile() + " was modified.");
        System.out.println("----------> length: " + file.length());
        System.out.println("----------> last modified: " + new Date(file.lastModified()));
        System.out.println("----------> readable: " + file.canRead());
        System.out.println("----------> writable: " + file.canWrite());
        System.out.println("----------> executable: " + file.canExecute());
    }

    public void onFileDelete(File file) {
        System.out.println(file.getAbsoluteFile() + " was deleted.");
    }

    public void onStop(FileAlterationObserver observer) {
        System.out.println("The WindowsFileListener has stopped on " + observer.getDirectory().getAbsolutePath());
    }
}

