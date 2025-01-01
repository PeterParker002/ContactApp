package com.contacts.model;

public class Session {
	private String sessionId;
	private int userId;
	private String createdAt;
	private String lastAccessedAt;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getLastAccessedAt() {
		return lastAccessedAt;
	}

	public void setLastAccessedAt(String lastAccessedAt) {
		this.lastAccessedAt = lastAccessedAt;
	}

}
