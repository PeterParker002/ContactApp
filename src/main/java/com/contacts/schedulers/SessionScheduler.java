package com.contacts.schedulers;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.contacts.cache.SessionCache;
import com.contacts.connection.ConfigurationLoader;
import com.contacts.dao.UserDAO;
import com.contacts.model.Session;

@WebListener
public class SessionScheduler implements ServletContextListener {

	ScheduledExecutorService expiryCheckScheduler = Executors.newSingleThreadScheduledExecutor();
	public static String server_ip;
	public static int server_port;
	public static String domain = "";

	public SessionScheduler() {
	}

	public void contextDestroyed(ServletContextEvent sce) {
		try {
			UserDAO.deleteEntryFromAvailableServers(server_ip, server_port);
			System.out.println("Removed the Server's Entry from DB.");
			if (expiryCheckScheduler != null && !expiryCheckScheduler.isShutdown()) {
				expiryCheckScheduler.shutdown();
				System.out.println("Database Scheduler Shut Down.");
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void contextInitialized(ServletContextEvent sce) {
		try {
			ConfigurationLoader.loadConfig();
			server_ip = InetAddress.getLocalHost().getHostAddress();
			ServletContext ctx = sce.getServletContext();
			server_port = Integer.parseInt(ctx.getInitParameter("server.port"));
			System.out.println("Server IP Address: " + server_ip);
			System.out.println("Server Port: " + server_port);
			domain = "http://" + server_ip + ":" + server_port;
			UserDAO.addEntryToAvailableServers(server_ip, server_port);
			SessionCache.updateAvailableServers();
			SessionCache.notifyAvailableServerUpdate();
			Runnable fetchSession = () -> {
				Iterator<String> iterator = SessionCache.activeSessionObjects.keySet().iterator();
				while (iterator.hasNext()) {
					String sessionId = iterator.next();
					Session session = SessionCache.activeSessionObjects.get(sessionId);
					long lastAccessed = session.getLastAccessedAt();
					UserDAO.updateSession(sessionId, lastAccessed);
				}
				SessionCache.activeSessionObjects.clear();
				UserDAO.clearExpiredSessions();
				System.out.println("Clearing Session");
			};

			expiryCheckScheduler.scheduleAtFixedRate(fetchSession, 0, 1, TimeUnit.MINUTES);
			System.out.println("Database Scheduler Initialized.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
