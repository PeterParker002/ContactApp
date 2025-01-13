package com.contacts.connection;

import java.io.FileInputStream;
import java.io.IOException;
//import java.io.InputStream;
import java.util.Properties;

public class ConfigurationLoader {
	static Properties prop = new Properties();
	private static final String filePath = "/home/karthik-tt0479/eclipse-workspace/FirstProject/src/main/resources/application.properties";

	public static void loadConfig() throws IOException {
		try (FileInputStream fs = new FileInputStream(filePath)) {
			if (fs != null) {
				System.out.println("Configuration File Loaded Successfully!");
				prop.load(fs);
			}
		} catch (Exception e) {
			System.err.println("Error loading configuration: " + e.getMessage());
			throw e;
		}
	}

	public static void reload() throws IOException {
		loadConfig();
	}

	public static String getProperty(String key) {
		return prop.getProperty(key);
	}
}
