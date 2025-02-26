package com.contacts.schedulers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.contacts.cache.SessionCache;
import com.contacts.connection.ConfigurationLoader;
import com.contacts.dao.UserDAO;
import com.contacts.exception.InvalidConfigurationException;
import com.contacts.logger.MyCustomFormatter;
import com.contacts.model.Session;

@WebListener
public class SessionScheduler implements ServletContextListener {

	ScheduledExecutorService expiryCheckScheduler = Executors.newSingleThreadScheduledExecutor();
	private static Logger applicationLogger = Logger.getLogger(SessionCache.class.getName());
	static {
		try {
			FileHandler fileHandler = new FileHandler(
					System.getProperty("user.dir")
							+ "/eclipse-workspace/FirstProject/src/main/resources/logs/application_%g.log",
					100 * 1024, 2, true);
			fileHandler.setFormatter(new SimpleFormatter());

			applicationLogger.addHandler(fileHandler);
			applicationLogger.setUseParentHandlers(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static String server_ip;
	public static int server_port;

	public SessionScheduler() {
	}

	public void contextDestroyed(ServletContextEvent sce) {
		try {
			UserDAO.deleteEntryFromAvailableServers(server_ip, server_port);
			System.out.println("Removed the Server's Entry from DB.");
			SessionCache.notifyAvailableServerUpdate();
			if (expiryCheckScheduler != null && !expiryCheckScheduler.isShutdown()) {
				expiryCheckScheduler.shutdown();
				System.out.println("Database Scheduler Shut Down.");
			}
		} catch (ClassNotFoundException | SQLException e) {
			applicationLogger.log(Level.SEVERE, "", e);
		}
	}

	private void initServerStartup(ServletContextEvent sce)
			throws InvalidConfigurationException, ClassNotFoundException, SQLException, UnknownHostException {
		ConfigurationLoader.loadConfig();
		server_ip = InetAddress.getLocalHost().getHostAddress();
		ServletContext ctx = sce.getServletContext();
		server_port = Integer.parseInt(ctx.getInitParameter("server.port"));
		System.out.println("Server IP Address: " + server_ip);
		System.out.println("Server Port: " + server_port);
		UserDAO.addEntryToAvailableServers(server_ip, server_port);
		SessionCache.updateAvailableServers();
		SessionCache.notifyAvailableServerUpdate();
	}

	public void contextInitialized(ServletContextEvent sce) {
		try {
			initServerStartup(sce);
			Runnable fetchSession = () -> {
				Iterator<String> iterator = SessionCache.activeSessions.keySet().iterator();
				while (iterator.hasNext()) {
					String sessionId = iterator.next();
					Session session = SessionCache.activeSessions.get(sessionId);
					long lastAccessed = session.getLastAccessedAt();
					UserDAO.updateSession(sessionId, lastAccessed);
				}
				SessionCache.activeSessions.clear();
				UserDAO.clearExpiredSessions();
				System.out.println("Clearing Session");
			};

			expiryCheckScheduler.scheduleAtFixedRate(fetchSession, 0, 1, TimeUnit.MINUTES);
			System.out.println("Database Scheduler Initialized.");
		} catch (InvalidConfigurationException | UnknownHostException | ClassNotFoundException | SQLException e) {
			applicationLogger.log(Level.SEVERE, "", e);
		}
	}

}
