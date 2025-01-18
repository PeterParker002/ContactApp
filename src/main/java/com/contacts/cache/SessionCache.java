package com.contacts.cache;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.contacts.dao.UserDAO;
import com.contacts.model.Server;
import com.contacts.model.Session;
import com.contacts.model.User;
import com.contacts.notifier.Notifier;
import com.contacts.schedulers.SessionScheduler;
import com.google.gson.Gson;

//import java.time.LocalDateTime;

public class SessionCache {
//	public static ConcurrentHashMap<String, LocalDateTime> activeSessions = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<String, Session> activeSessionObjects = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<Integer, User> userCache = new ConcurrentHashMap<>();
	public static CopyOnWriteArrayList<Server> availableServers = new CopyOnWriteArrayList<Server>();
	public static int count = 0;
	public static final int EXPIRATION_TIME = 5;

//	public static void updateUserSession(String sessionId, LocalDateTime lastAccessedAt) {
//		activeSessions.put(sessionId, lastAccessedAt);
//	}

	public static void addUserToCache(int user_id, User user) {
		userCache.put(user_id, user);
	}

	public static void updateAvailableServers() {
		UserDAO u = new UserDAO();
		try {
			availableServers = new CopyOnWriteArrayList<Server>();
			ArrayList<Server> servers = u.getAvailableServers();
			for (Server s : servers) {
				availableServers.add(s);
			}
			System.out.println("Available Servers: " + availableServers);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	public static void notifySessionUpdate(Session session) {
		Gson gson = new Gson();
		String json = gson.toJson(session);
		availableServers.forEach(s -> {
			Notifier.notifyServer(s.getServerIp(), s.getServerPort(), "notifySessionChange", session.getSessionId(),
					json);
		});
	}

	public static void notifyUserUpdate(User user) {
		Gson gson = new Gson();
		String json = gson.toJson(user);
		availableServers.forEach(s -> {
			Notifier.notifyServer(s.getServerIp(), s.getServerPort(), "notifyUserUpdate", "", json);
		});
	}

	public static void notifySessionRemove(Session session) {
		Gson gson = new Gson();
		String json = gson.toJson(session);
		availableServers.forEach(s -> {
			Notifier.notifyServer(s.getServerIp(), s.getServerPort(), "notifySessionRemoval", session.getSessionId(),
					json);
		});
	}

	public static void notifyAvailableServerUpdate() {
		availableServers.forEach(s -> {
			System.out.println("Notifying Server: " + s.getServerIp() + " -> " + s.getServerPort());
			Notifier.notifyServer(s.getServerIp(), s.getServerPort(), "notifyAvailableServerUpdate", "", "");
		});
	}

	public static void checkAndUpdateUserCache(Session session) {
		SessionCache.activeSessionObjects.forEach((sId, s) -> {
			if (session.getUserId() == s.getUserId()) {
				count++;
			}
		});
		System.out.println(count);
		if (count == 0) {
			System.out.println("Clearing User from Cache");
			SessionCache.userCache.remove(session.getUserId());
		}
		count = 0;
	}
}