package com.contacts.model;

import com.contacts.utils.Database.UserEmail;
import com.contacts.utils.DatabaseImpl;

public class Mail {
	private int id;
	private String mail;
	private long createdAt;
	private long modifiedAt;

	public int getId() {
		return id;
	}
	
	public int getUniqueID() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public DatabaseImpl getPrimaryKeyColumn() {
		return UserEmail.ID;
	}

	public String getEmail() {
		return mail;
	}

	public void setEmail(String mail) {
		this.mail = mail;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public long getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(long modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public String toString() {
		return this.id + " -> " + this.mail;
	}
}
