package com.contacts.schedulers;

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
import com.contacts.dao.UserDAO;
import com.contacts.model.Session;

/**
 * Application Lifecycle Listener implementation class SessionScheduler
 *
 */
@WebListener
public class SessionScheduler implements ServletContextListener {

	ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

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
		Runnable fetchSession = () -> {
			Iterator<String> iterator = SessionCache.activeSessions.keySet().iterator();
			while (iterator.hasNext()) {
				String sessionId = iterator.next();
				LocalDateTime lastAccessed = SessionCache.activeSessions.get(sessionId);
				if (Duration.between(lastAccessed, LocalDateTime.now()).toMinutes() > SessionCache.EXPIRATION_TIME) {
					u.clearSession(sessionId);
					System.out.println("Cleared Session from DB");
					SessionCache.activeSessions.remove(sessionId);
					System.out.println("Cleared Session from Cache");
				} else {
					u.updateSession(sessionId, lastAccessed);
				}
			}
			if (SessionCache.activeSessions.size() > 0)
				System.out.println("Session Cache: " + SessionCache.activeSessions);
		};

		scheduler.scheduleAtFixedRate(fetchSession, 1, 3, TimeUnit.SECONDS);

		System.out.println("Database Scheduler Initialized.");
	}

}
