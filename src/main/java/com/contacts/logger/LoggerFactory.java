package com.contacts.logger;

import com.contacts.logger.MyCustomLogger.LogLevel;

public class LoggerFactory {
	private static MyCustomLogger instance;
	private static String logFile = "";
	public static MyCustomLogger getLogger(String filePath) {
		if (instance != null) {
			if (logFile.equals(filePath)) {				
				return instance;
			}
		}
		return new MyCustomLogger(filePath, LogLevel.INFO);
	}
}
