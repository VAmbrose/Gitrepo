package com.etf.process;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.scheduling.quartz.QuartzJobBean;



public class ETFCaptureProcessorJob
extends QuartzJobBean {
    private static final Logger LOG = Logger.getLogger(ETFCaptureProcessorJob.class);
    private MetaDataProcessor efiCaptureProcessorTask;

    public void setCaptureProcessorTask(MetaDataProcessor efiCaptureProcessorTask) {
        this.efiCaptureProcessorTask = efiCaptureProcessorTask;
    }

    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            new MetaDataProcessor().process();
        }
        catch (IOException e) {
            LOG.warn("Exception : " + e);
        }
        catch (CannotGetJdbcConnectionException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

