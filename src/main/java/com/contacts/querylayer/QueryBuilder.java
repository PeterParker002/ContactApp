package com.contacts.querylayer;

import java.util.ArrayList;

public class QueryBuilder {
		public String QueryString = "";
		public String statementType = "SELECT";
		public Table table;
		public String ALL_COLUMNS = "*";
		public ArrayList<Condition> condition = new ArrayList<>();
		public ArrayList<Column> columns = new ArrayList<>();

		public enum statementFormat {
			SELECT("SELECT %columns FROM %table %condition;"), INSERT("INSERT INTO %table %columns (%values);"),
			UPDATE("UPDATE %table set %column=%value %condition;"), DELETE("delete from %table %condition;");

			private String formatString;

			private statementFormat(String formatString) {
				this.formatString = formatString;
			}

			public String getFormatString() {
				return this.formatString;
			}

		}

		public String getColumns() {
			if (this.columns.size() > 0) {
				ArrayList<String> columnsList = new ArrayList<>();
				this.columns.forEach(col -> columnsList.add(col.toString()));
				String columnsString = String.join(", ", columnsList);
				return columnsString;
			}
			return ALL_COLUMNS;
		}

		public String getConditons() {
			if (this.condition.size() > 0) {
				ArrayList<String> conditionList = new ArrayList<>();
				this.condition.forEach(cnd -> conditionList.add(cnd.toString()));
				String conditionString = String.join(" and ", conditionList);
				return "WHERE " + conditionString;
			}
			return "";
		}

		public String build() {
			if (this.statementType.equalsIgnoreCase("select")) {
				statementFormat st = statementFormat.SELECT;
				this.QueryString = st.getFormatString();
				this.QueryString = this.QueryString.replace("%table", this.table.toString());
				this.QueryString = this.QueryString.replace("%columns", this.getColumns());
				this.QueryString = this.QueryString.replace("%condition", this.getConditons());
			}
			return this.QueryString;
		}

		public void setStatementType(String statementType) {
			this.statementType = statementType;
		}

		public void selectTable(String name, String alias) {
			Table table = new Table(name, alias);
			this.table = table;
		}

		public void selectTable(String name) {
			Table table = new Table(name);
			this.table = table;
		}

		public void selectColumn(String columnName, String alias) {
			Column col = new Column(columnName, alias, this.table);
			this.columns.add(col);
		}

		public void selectColumn(String columnName) {
			Column col = new Column(columnName, this.table);
			this.columns.add(col);
		}

		public void selectColumns(ArrayList<Column> col) {
			if (this.columns.size() > 0) {
				col.forEach(c -> this.columns.add(c));
			} else {
				this.columns = col;
			}
		}

		public void setCondition(Column col, String op, String val) {
			Condition cnd = new Condition(col, op, val);
			this.condition.add(cnd);
		}
}
