package com.contacts.querylayer;

public class Table {
	public String name;
	public String alias;

	public Table(String name) {
		this.name = name;
		this.alias = "";
	}

	public Table(String name, String alias) {
		this.name = name;
		this.alias = alias;
	}

	public String toString() {
		return (this.name + " " + this.alias).trim();
	}
}
