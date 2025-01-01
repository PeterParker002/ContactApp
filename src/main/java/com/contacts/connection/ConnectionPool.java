package com.contacts.connection;

import java.io.IOException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionPool {
	public static HikariDataSource dataSource;
	public static String db;

	static {
		HikariConfig hikariconfig = new HikariConfig();

		try {
			ConfigurationLoader.loadConfig();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String dbUrl = ConfigurationLoader.getProperty("dbUrl");
		String dbUsername = ConfigurationLoader.getProperty("dbUsername");
		String dbPassword = ConfigurationLoader.getProperty("dbPassword");

		hikariconfig.setJdbcUrl(dbUrl);
		hikariconfig.setUsername(dbUsername);
		hikariconfig.setPassword(dbPassword);
		hikariconfig.setDriverClassName("com.mysql.cj.jdbc.Driver");

		hikariconfig.setMaximumPoolSize(10);
		hikariconfig.setConnectionTimeout(30000);
		hikariconfig.setIdleTimeout(600000);
		hikariconfig.setMaxLifetime(1800000);

		dataSource = new HikariDataSource(hikariconfig);

	}

	public static HikariDataSource getDataSource() {
		return dataSource;
	}
}