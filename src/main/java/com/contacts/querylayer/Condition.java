package com.contacts.querylayer;

public class Condition<T> {
	public Column column;
	public String operator;
	public T value;

	public Condition(Column column, String operator, T value) {
		this.column = column;
		this.operator = operator;
		this.value = value;
	}

	public String toString() {
		if (this.value instanceof QueryBuilder) {
			return this.column.toString() + this.operator + "(" + this.value.toString().replace(";", "") + ")";
		}
		if (this.value instanceof String) {
			return this.column.toString() + this.operator + "\"" + this.value + "\"";
		} else {
			return this.column.toString() + this.operator + this.value;
		}
	}
}
