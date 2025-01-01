package com.contacts.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;

public class SessionCache {
	public static ConcurrentHashMap<String, LocalDateTime> activeSessions = new ConcurrentHashMap<String, LocalDateTime>();
	public static final int EXPIRATION_TIME = 2;

	public static void updateUserSession(String sessionId, LocalDateTime lastAccessedAt) {
		activeSessions.put(sessionId, lastAccessedAt);
	}
}
