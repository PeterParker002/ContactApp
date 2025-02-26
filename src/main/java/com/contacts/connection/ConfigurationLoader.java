package com.contacts.connection;

import java.io.FileInputStream;
import java.io.IOException;
//import java.io.InputStream;
import java.util.Properties;

import com.contacts.exception.InvalidConfigurationException;

public class ConfigurationLoader {
	static Properties prop = new Properties();
	private static final String filePath = System.getProperty("user.dir") + "/eclipse-workspace/FirstProject/src/main/resources/application.properties";

	public static void loadConfig() throws InvalidConfigurationException {
		try (FileInputStream fs = new FileInputStream(filePath)) {
			if (fs != null) {
				System.out.println("Configuration File Loaded Successfully!");
				prop.load(fs);
			}
		} catch (IOException e) {
			System.err.println("Error loading configuration: " + e.getMessage());
			throw new InvalidConfigurationException("Error while loading configuration", e);
		}
	}

	public static void reload() throws IOException, InvalidConfigurationException {
		loadConfig();
	}

	public static String getProperty(String key) {
		return prop.getProperty(key);
	}
}
