package com.contacts.utils;

public enum Operators {
	EQUAL("="), NOTEQUAL("!="), GREATERTHAN(">"), LESSTHAN("<"), GREATERTHANEQUAL(">="), LESSTHANEQUAL("<="), LIKE(" LIKE ");

	private String op;

	private Operators(String op) {
		this.op = op;
	}

	public String toString() {
		return this.op;
	}
}