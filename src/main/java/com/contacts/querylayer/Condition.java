package com.contacts.querylayer;

public class Condition {
	public Column column;
	public String operator;
	public String value;

	public Condition(Column column, String operator, String value) {
		this.column = column;
		this.operator = operator;
		this.value = value;
	}

	public String toString() {
		return this.column.name + this.operator + "\"" + this.value + "\"";
	}
}
