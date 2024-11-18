package com.contacts.querylayer;

import java.util.ArrayList;

public class GroupBy {
	public ArrayList<Column> columns = new ArrayList<Column>();
	
	public GroupBy(Column col) {
		this.columns.add(col);
	}
	
	public GroupBy(ArrayList<Column> cols) {
		this.columns = cols;
	}
	
	private String getColumns() {
		ArrayList<String> columnsList = new ArrayList<>();
		this.columns.forEach(col -> columnsList.add(col.toString()));
		String columnsString = String.join(", ", columnsList);
		return columnsString;
	}
	
	public String toString() {
		return "GROUP BY " + getColumns();
	}
}
