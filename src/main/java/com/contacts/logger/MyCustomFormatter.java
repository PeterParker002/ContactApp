package com.contacts.logger;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyCustomFormatter extends Formatter {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String format(LogRecord record) {
        String timestamp = sdf.format(new Date(record.getMillis()));
        String logLevel = record.getLevel().getName();
        String message = record.getMessage();

        return String.format("%s [%s] - %s%n", timestamp, logLevel, message);
    }
}