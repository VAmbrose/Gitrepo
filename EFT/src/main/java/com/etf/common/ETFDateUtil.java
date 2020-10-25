package com.etf.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ETFDateUtil {
    public static final String currentDateSubjectWithTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return sdf.format(cal.getTime());
    }
}

