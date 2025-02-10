package com.contacts.schedulers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.Cookie;

import com.contacts.cache.SessionCache;
import com.contacts.connection.ConfigurationLoader;
import com.contacts.dao.UserDAO;
import com.contacts.model.Session;
import com.contacts.notifier.Notifier;
import com.google.gson.Gson;

@WebListener
public class SessionScheduler implements ServletContextListener {

	ScheduledExecutorService expiryCheckScheduler = Executors.newSingleThreadScheduledExecutor();
	public static String server_ip;
	public static int server_port;
	public static String domain = "";

	public SessionScheduler() {
	}

	public void contextDestroyed(ServletContextEvent sce) {
		UserDAO u = new UserDAO();
		try {
			u.deleteEntryFromAvailableServers(server_ip, server_port);
			System.out.println("Removed the Server's Entry from DB.");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		if (expiryCheckScheduler != null && !expiryCheckScheduler.isShutdown()) {
			expiryCheckScheduler.shutdown();
			System.out.println("Database Scheduler Shut Down.");
		}
	}

	public void contextInitialized(ServletContextEvent sce) {
		UserDAO u = new UserDAO();
		try {
			ConfigurationLoader.loadConfig();
			server_ip = InetAddress.getLocalHost().getHostAddress();
			ServletContext ctx = sce.getServletContext();
			server_port = Integer.parseInt(ctx.getInitParameter("server.port"));
			System.out.println("Server IP Address: " + server_ip);
			System.out.println("Server Port: " + server_port);
			domain = "http://" + server_ip + ":" + server_port;
			u.addEntryToAvailableServers(server_ip, server_port);
			SessionCache.updateAvailableServers();
			SessionCache.notifyAvailableServerUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Runnable fetchSession = () -> {
			Iterator<String> iterator = SessionCache.activeSessionObjects.keySet().iterator();
//			while (iterator.hasNext()) {
//				String sessionId = iterator.next();
//				Session session = SessionCache.activeSessionObjects.get(sessionId);
//				long lastAccessed = session.getLastAccessedAt();
//				System.out.println((System.currentTimeMillis() - lastAccessed) + " > " + SessionCache.EXPIRATION_TIME);
//				if ((System.currentTimeMillis() - lastAccessed) > SessionCache.EXPIRATION_TIME) {
//					u.clearSession(sessionId);
//					System.out.println("Cleared Session from DB");
//					SessionCache.checkAndUpdateUserCache(session);
//				} else {
//					System.out.println("Updating Session");
//					u.updateSession(sessionId, lastAccessed);
//				}
//			}
			while (iterator.hasNext()) {
				String sessionId = iterator.next();
				Session session = SessionCache.activeSessionObjects.get(sessionId);
				long lastAccessed = session.getLastAccessedAt();
				u.updateSession(sessionId, lastAccessed);
			}
			u.clearExpiredSessions();
			SessionCache.activeSessionObjects.clear();
			System.out.println("Clearing Session");
		};

		expiryCheckScheduler.scheduleAtFixedRate(fetchSession, 0, 1, TimeUnit.MINUTES);
		System.out.println("Database Scheduler Initialized.");
	}

}
