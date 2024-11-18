package com.contacts.utils;

public enum Order {
	asc("ASC"), desc("DESC");

	private String val;

	private Order(String val) {
		this.val = val;
	}

	public String getValue() {
		return this.val;
	}
}
