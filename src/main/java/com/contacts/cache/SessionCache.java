package com.contacts.cache;

import java.util.concurrent.ConcurrentHashMap;

import com.contacts.model.Session;
import com.contacts.model.User;

//import java.time.LocalDateTime;

public class SessionCache {
//	public static ConcurrentHashMap<String, LocalDateTime> activeSessions = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<String, Session> activeSessionObjects = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<Integer, User> userCache = new ConcurrentHashMap<>();
	public static int count = 0;
	public static final int EXPIRATION_TIME = 2;

//	public static void updateUserSession(String sessionId, LocalDateTime lastAccessedAt) {
//		activeSessions.put(sessionId, lastAccessedAt);
//	}

	public static void addUserToCache(int user_id, User user) {
		userCache.put(user_id, user);
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