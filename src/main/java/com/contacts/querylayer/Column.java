package com.contacts.querylayer;

import com.contacts.querylayer.QueryBuilder.statementFormat;
import com.contacts.utils.DatabaseImpl;

public class Column {
	public DatabaseImpl db_name;
	public String name;
	public String alias;
	public Table table;
	public String aggregateFunction;

	public Column(DatabaseImpl name, String alias, String aggregate, Table table) {
		this.db_name = name;
		this.name = name.toString();
		this.alias = alias;
		this.table = table;
		this.aggregateFunction = aggregate;
	}

	protected void init() {
		// Handle the table alias
		this.name = !this.table.alias.equals("") ? this.table.alias + "." + this.name : this.name;
		// Handle the aggregate function
		this.name = !this.aggregateFunction.equals("") ? this.aggregateFunction + "(" + this.name + ")" : this.name;
	}

	public String toString() {
		init();
		return this.alias.equals("") ? this.name : this.name + " AS " + this.alias;
	}

	public String toString(QueryBuilder qb) {
		if (qb.statementType.equals(statementFormat.SELECT.toString())) {
			return this.alias.equals("") ? this.name : this.name + " AS " + this.alias;
		} else {
			return this.name;
		}
	}
}