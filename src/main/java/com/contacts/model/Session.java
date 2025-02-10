package com.contacts.model;

public class Session {
	private String sessionId;
	private int userId;
	private long createdAt;
	private long lastAccessedAt;

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

	public Long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public Long getLastAccessedAt() {
		return lastAccessedAt;
	}

	public void setLastAccessedAt(long lastAccessedAt) {
		this.lastAccessedAt = lastAccessedAt;
	}

	public String toString() {
		return this.sessionId + " -> " + this.lastAccessedAt;
	}

}
