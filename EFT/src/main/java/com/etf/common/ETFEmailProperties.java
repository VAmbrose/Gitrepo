package com.etf.common;

public class ETFEmailProperties {
    private String smtpServer = null;
    private String smtpFrom = null;
    private String smtpTo = null;
    private String totalLineToRead = null;

    public String getSmtpServer() {
        return this.smtpServer;
    }

    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    public String getSmtpFrom() {
        return this.smtpFrom;
    }

    public void setSmtpFrom(String smtpFrom) {
        this.smtpFrom = smtpFrom;
    }

    public String getSmtpTo() {
        return this.smtpTo;
    }

    public void setSmtpTo(String smtpTo) {
        this.smtpTo = smtpTo;
    }

    public String getTotalLineToRead() {
        return this.totalLineToRead;
    }

    public void setTotalLineToRead(String totalLineToRead) {
        this.totalLineToRead = totalLineToRead;
    }
}

