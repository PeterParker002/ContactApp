package com.contacts.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.contacts.dao.UserDAO;
import com.contacts.model.Server;
import com.contacts.model.Session;
import com.contacts.model.User;
import com.contacts.notifier.Notifier;
import com.google.gson.Gson;

public class SessionCache {
	public static Map<String, Session> activeSessions = new ConcurrentHashMap<>();
	public static Map<Integer, User> userCache = new ConcurrentHashMap<>();
	public static List<Server> availableServers = new CopyOnWriteArrayList<Server>();
	public static int count = 0;
	public static final int EXPIRATION_TIME = 15 * 1000 * 60;

	public static void addUserToCache(int user_id, User user) {
		userCache.put(user_id, user);
	}

	public static void updateAvailableServers() {
		try {
			availableServers = new CopyOnWriteArrayList<Server>();
			List<Server> servers = UserDAO.getAvailableServers();
			for (Server s : servers) {
				availableServers.add(s);
			}
			System.out.println("Available Servers: " + availableServers);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public static User getUserCache(int userId) {
		if (userCache.containsKey(userId)) {
			return userCache.get(userId);
		}
		User user = UserDAO.getUserInfo(userId);
		addUserToCache(userId, user);
		return user;
	}

	public static void checkAndUpdateUserCache(Session session) {
		SessionCache.activeSessions.forEach((sId, s) -> {
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

	public static void addSessionCache(Session session) {
		activeSessions.put(session.getSessionId(), session);
	}

	public static Session updateSessionCache(String sessionId) {
		Session session = activeSessions.get(sessionId);
		session.setLastAccessedAt(System.currentTimeMillis());
		return session;
	}
}