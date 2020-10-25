package com.etf.common;

public class ETFEmailTemplate {
    public static String getEmailFormatProcessing(String body) {
        StringBuffer sb = new StringBuffer();
        sb.append("Hi All, \n");
        sb.append("\n");
        sb.append("Process started with:\n\n");
        sb.append(body + "\n\n");
        sb.append("||-->> Process started. Please wait for next completed status email.\n\n");
        sb.append("\n");
        sb.append("[NOTE: Please do not process the same file untill existing insert complete].\n\n");
        sb.append("\n");
        sb.append("Thanks and regards, \n");
        sb.append("GRT Shipment Status \n");
        sb.append("Phone:- (407)-492-9071 \n");
        sb.append("Email:- CLM-Support@corp.ds.fedex.com \n");
        return sb.toString();
    }

    public static String getEmailFormatProcessed(String body) {
        StringBuffer sb = new StringBuffer();
        sb.append("Hi All, \n");
        sb.append("\n");
        sb.append("Process completed with:\n\n");
        sb.append(body + "\n\n");
        sb.append("||-->> Process completed.\n\n");
        sb.append("\n");
        sb.append("Thanks and regards, \n");
        sb.append("GRT Shipment Status \n");
        sb.append("Phone:- (407)-492-9071 \n");
        sb.append("Email:- CLM-Support@corp.ds.fedex.com \n");
        return sb.toString();
    }

    public static String getEmailFormatException(String body) {
        StringBuffer sb = new StringBuffer();
        sb.append("Hi All, \n");
        sb.append("\n");
        sb.append("Exception raised on processing data:\n\n");
        sb.append("Please refer logs:\n\n");
        sb.append("/var/fedex/sqa/att/logs/tdw/rsc\n\n");
        sb.append("/var/fedex/sqa/att/logs/tdw/rev\n\n");
        sb.append(body + "\n\n");
        sb.append("\n");
        sb.append("Thanks and regards, \n");
        sb.append("GRT Shipment Status \n");
        sb.append("Phone:- (407)-492-9071 \n");
        sb.append("Email:- CLM-Support@corp.ds.fedex.com \n");
        return sb.toString();
    }
}

