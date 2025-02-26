package com.contacts.logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class LoggerFactory {
	private static Logger logger;

	private static void configure() {
		try {
			FileHandler fileHandler = new FileHandler(
					System.getProperty("user.dir")
							+ "/eclipse-workspace/FirstProject/src/main/resources/logs/access_%g.log",
					100 * 1024, 2, true);
			fileHandler.setFormatter(new MyCustomFormatter());

			logger.addHandler(fileHandler);
			logger.setUseParentHandlers(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Logger getLogger() {
		if (logger != null) {
			return logger;
		}
		logger = Logger.getLogger("");
		configure();
		return logger;
	}
}
