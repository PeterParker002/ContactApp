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

/**
 * Application Lifecycle Listener implementation class SessionScheduler
 *
 */
@WebListener
public class SessionScheduler implements ServletContextListener {

	ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	public int count = 0;
	public static String server_ip;
	public static int server_port;
	public static String domain = "";

	/**
	 * Default constructor.
	 */
	public SessionScheduler() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent sce) {
		UserDAO u = new UserDAO();
		try {
			u.deleteEntryFromAvailableServers(server_ip, server_port);
			System.out.println("Removed the Server's Entry from DB.");
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (scheduler != null && !scheduler.isShutdown()) {
			scheduler.shutdown();
			System.out.println("Database Scheduler Shut Down.");
		}
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent sce) {
		UserDAO u = new UserDAO();
		try {
			server_ip = InetAddress.getLocalHost().getHostAddress();
			ServletContext ctx = sce.getServletContext();
			server_port = Integer.parseInt(ctx.getInitParameter("server.port"));
			System.out.println("Server IP Address: " + server_ip);
			System.out.println("Server Port: " + server_port);
			domain = "http://" + server_ip + ":" + server_port;
			u.addEntryToAvailableServers(server_ip, server_port);
			SessionCache.updateAvailableServers();
			SessionCache.notifyAvailableServerUpdate();
		} catch (UnknownHostException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		Runnable fetchSession = () -> {
			Iterator<String> iterator = SessionCache.activeSessionObjects.keySet().iterator();
			while (iterator.hasNext()) {
				String sessionId = iterator.next();
				Session session = SessionCache.activeSessionObjects.get(sessionId);
				LocalDateTime lastAccessed = LocalDateTime.parse(session.getLastAccessedAt());
				if (Duration.between(lastAccessed, LocalDateTime.now()).toMinutes() > SessionCache.EXPIRATION_TIME) {
					u.clearSession(sessionId);
					System.out.println("Cleared Session from DB");
					SessionCache.activeSessionObjects.remove(sessionId);
					System.out.println("Cleared Session from Cache");
					SessionCache.checkAndUpdateUserCache(session);
				} else {
					u.updateSession(sessionId, lastAccessed);
				}
			}
			if (SessionCache.activeSessionObjects.size() > 0) {
				System.out.println("Session Cache: " + SessionCache.activeSessionObjects);
			}
			System.out.println("User Cache: " + SessionCache.userCache);
		};

		scheduler.scheduleAtFixedRate(fetchSession, 1, 3, TimeUnit.SECONDS);

		System.out.println("Database Scheduler Initialized.");
	}

}
