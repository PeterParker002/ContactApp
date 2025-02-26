package com.contacts.utils;

public enum LoggerType {
	ACCESS("access"), APPLICATION("application");
	private String type;
	private LoggerType(String type) {
		this.type = type;
	}
	
	public String toString() {
		return this.type;
	}
}
