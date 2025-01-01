package com.contacts.model;

public class Mail {
	private int id;
	private String mail;

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

	public String toString() {
		return this.id + " -> " + this.mail;
	}
}
