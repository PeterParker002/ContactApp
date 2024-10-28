package com.contacts.querylayer;

public class Column {
	public String name;
	public String alias;
	public Table table;

	public Column(String name, Table table) {
		this.name = name;
		this.alias = "";
		this.table = table;
		init();
	}

	public Column(String name, String alias, Table table) {
		this.name = name;
		this.alias = alias;
		this.table = table;
		init();
	}

	protected void init() {
		this.name = !this.table.alias.equals("") ? this.table.alias + "." + this.name : this.name;
	}

	public String toString() {
		return this.alias.equals("") ? this.name : this.name + " as " + this.alias;
	}
}