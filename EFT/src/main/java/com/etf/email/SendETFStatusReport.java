package com.etf.email;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.etf.common.ETFDateUtil;
import com.etf.common.ETFEmailProperties;
import com.etf.common.ETFEmailTemplate;


public class SendETFStatusReport {
    private static final Logger LOG = Logger.getLogger(SendETFStatusReport.class);

    public void sendEmailProcessing(ETFEmailProperties emailProperties, String fileName, String body) {
        String to = emailProperties.getSmtpTo();
        String[] toArray = to.split(",");
        String from = emailProperties.getSmtpFrom();
        String host = emailProperties.getSmtpServer();
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        Session session = Session.getDefaultInstance(properties);
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            Address[] addressTo = new InternetAddress[toArray.length];
            for (int i2 = 0; i2 < toArray.length; ++i2) {
                addressTo[i2] = new InternetAddress(toArray[i2]);
            }
            message.addRecipients(Message.RecipientType.TO, addressTo);
            String currentDateString = ETFDateUtil.currentDateSubjectWithTime();
            message.setSubject(fileName + " Started external process on : " + currentDateString);
            String bodyFormat = ETFEmailTemplate.getEmailFormatProcessing(body);
            message.setText(null != bodyFormat ? bodyFormat : "Processing...");
            Transport.send(message);
            LOG.info("\n\n"+"********** SMTP MAIL > ETF Data Loading Process Started >>>>>>>>>>>>>>");
            LOG.info("\n"+"**********" + fileName + "************* \n");
        }
        catch (MessagingException mex) {
            LOG.error("Exception : " + mex);
        }
    }

    public void sendEmailProcessed(ETFEmailProperties emailProperties, String fileName, String body) {
        String to = emailProperties.getSmtpTo();
        String[] toArray = to.split(",");
        String from = emailProperties.getSmtpFrom();
        String host = emailProperties.getSmtpServer();
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        Session session = Session.getDefaultInstance(properties);
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            Address[] addressTo = new InternetAddress[toArray.length];
            for (int i2 = 0; i2 < toArray.length; ++i2) {
                addressTo[i2] = new InternetAddress(toArray[i2]);
            }
            message.addRecipients(Message.RecipientType.TO, addressTo);
            String currentDateString = ETFDateUtil.currentDateSubjectWithTime();
            message.setSubject(fileName + " file completed on : " + currentDateString);
            String bodyFormat = ETFEmailTemplate.getEmailFormatProcessed(body);
            message.setText(null != bodyFormat ? bodyFormat : "Completed ...");
            Transport.send(message);
            LOG.info("\n\n"+"********** SMTP MAIL > ETF Data Loading Process Completed *************");
            LOG.info("\n"+"**********" + fileName + "************* \n");
        }
        catch (MessagingException mex) {
            LOG.error("Exception : " + mex);
        }
    }

    public void sendEmailException(ETFEmailProperties emailProperties, String body) {
        String to = emailProperties.getSmtpTo();
        String[] toArray = to.split(",");
        String from = emailProperties.getSmtpFrom();
        String host = emailProperties.getSmtpServer();
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        Session session = Session.getDefaultInstance(properties);
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            Address[] addressTo = new InternetAddress[toArray.length];
            for (int i2 = 0; i2 < toArray.length; ++i2) {
                addressTo[i2] = new InternetAddress(toArray[i2]);
            }
            message.addRecipients(Message.RecipientType.TO, addressTo);
            String currentDateString = ETFDateUtil.currentDateSubjectWithTime();
            message.setSubject("*****EXCEPTION / ERROR***** : In data processing : " + currentDateString);
            String bodyFormat = ETFEmailTemplate.getEmailFormatException(body);
            message.setText(null != bodyFormat ? bodyFormat : "Exception...");
            Transport.send(message);
            LOG.info("\n\n"+"********** SMTP MAIL > ETF Data Loading ERROR ************* \n");
        }
        catch (MessagingException mex) {
            LOG.error("Exception : " + mex);
        }
    }
}

