package com.contacts.utils;

public enum JoinTypes {
	inner("INNER"), left("LEFT"), right("RIGHT");

	private String type;

	private JoinTypes(String type) {
		this.type = type;
	}

	public String getValue() {
		return this.type;
	}
}
