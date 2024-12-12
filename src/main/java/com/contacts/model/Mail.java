package com.contacts.model;

public class Mail {
	private int id;
	private String mail;
	private boolean isPrimary;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return mail;
	}

	public void setEmail(String mail) {
		this.mail = mail;
	}

	public boolean getIsPrimary() {
		return isPrimary;
	}

	public void setIsPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	public String toString() {
		return this.id + " -> " + this.mail;
	}
}
