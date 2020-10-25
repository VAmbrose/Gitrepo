package com.etf.scheduler;


import java.util.TimerTask;

import org.apache.log4j.Logger;



public class AppAliveScheduler
extends TimerTask {
    private static final Logger LOGGER = Logger.getLogger(AppAliveScheduler.class.getName());
    long initTime = System.currentTimeMillis();
    private String threadId;

    public AppAliveScheduler(String threadId) {
        this.threadId = threadId;
    }

    public void run() {
        LOGGER.info("||---> " + this.threadId + " : Active and Running External Table File Service..@ since " + (System.currentTimeMillis() - this.initTime) / 60000 + " minuts\n");
    }
}

