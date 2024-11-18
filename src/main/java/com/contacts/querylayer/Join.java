package com.contacts.querylayer;

import com.contacts.utils.JoinTypes;

public class Join {
	public JoinTypes type;
	public Column column1;
	public Column column2;

	public Join(JoinTypes joinType, Column c1, Column c2) {
		this.type = joinType;
		this.column1 = c1;
		this.column2 = c2;
	}

	public String toString() {
		return this.type.getValue() + " JOIN " + this.column2.table + " ON " + this.column1.toString() + "="
				+ this.column2.toString();
	}
}
