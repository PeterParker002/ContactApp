package com.contacts.querylayer;

import com.contacts.utils.Database.TableInfo;

public class Table {
	public TableInfo name;
	public String alias;
	public boolean defaultAlias = false;

	public Table(TableInfo name) {
		this.name = name;
		this.alias = "";
	}

	public Table(TableInfo name, String alias) {
		this.name = name;
		this.alias = alias;
	}

	public void setDefaultAlias() {
		this.defaultAlias = true;
		this.alias = this.name.toString();
	}

	public TableInfo getName() {
		return name;
	}

	public String toString() {
		if (this.defaultAlias) {
			return this.name.toString();
		} else {
			return this.alias.equals("") ? this.name.toString() : (this.name + " AS " + this.alias).trim();
		}
	}
}
