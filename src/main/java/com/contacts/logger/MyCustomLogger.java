package com.contacts.logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyCustomLogger {
	public enum LogLevel {
		INFO, DEBUG, WARN, ERROR
	}

	private PrintWriter writer;

	private String template = "%s - [%s] [%s] \"%s %s %s\" %s";

	public MyCustomLogger(String logFilePath, LogLevel logLevel) {
		try {
			writer = new PrintWriter(new FileWriter(logFilePath, true), true);
		} catch (IOException e) {
			System.err.println("Failed to initialize logger: " + e.getMessage());
		}
	}

	public synchronized void log(LogLevel level, String requestType, String ip, String uri, String message) {
		String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String logMessage = String.format(template, ip, level, timestamp, requestType, uri, "HTTP/1.1", message);
		System.out.println(logMessage);
		if (writer != null) {
			writer.println(logMessage);
		}
	}

	public void info(String requestType, String ip, String uri, String message) {
		log(LogLevel.INFO, requestType, ip, uri, message);
	}

	public void debug(String requestType, String ip, String uri, String message) {
		log(LogLevel.DEBUG, requestType, ip, uri, message);
	}

	public void warn(String requestType, String ip, String uri, String message) {
		log(LogLevel.WARN, requestType, ip, uri, message);
	}

	public void error(String requestType, String ip, String uri, String message) {
		log(LogLevel.ERROR, requestType, ip, uri, message);
	}

	public void close() {
		if (writer != null) {
			writer.close();
		}
	}
}
