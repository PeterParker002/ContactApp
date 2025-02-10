package com.contacts.connection;

import java.io.IOException;

import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class ConnectionPool {
	public static HikariDataSource dataSource;
	public static String db;

	static {
		((Logger) LoggerFactory.getLogger("com.zaxxer.hikari")).setLevel(Level.OFF);
		HikariConfig hikariconfig = new HikariConfig();
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