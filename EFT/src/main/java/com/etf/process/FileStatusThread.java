package com.etf.process;

public class FileStatusThread
extends Thread {
    private String fileName;

    public FileStatusThread(String fileName) {
        this.fileName = fileName;
    }

    public void run() {
        System.out.println("File processing : " + this.fileName);
    }
}

