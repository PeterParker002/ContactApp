package com.contacts.querylayer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import com.contacts.utils.JoinTypes;
import com.contacts.utils.Operators;
import com.contacts.utils.Order;
import com.contacts.utils.Database.TableInfo;

/*
 * 
 * Select Statement Syntax:
 *  - SELECT [ ALL | DISTINCT ] column1, column2, ..., columnN | * | agg_func(column) AS alias FROM Table [ AS table_alias ] [ INNER | LEFT | RIGHT ]
 *    JOIN another_table ON join_condition ] [ WHERE condition ] [ GROUP BY column1, column2, ..., columnN ] [ HAVING condition ]
 *    [ ORDER BY column [ ASC | DESC ], ... ] [ LIMIT n [ OFFSET value ] ] [ FOR UPDATE | LOCK IN SHARE MODE ]; 
 *  
 *  - Simplified Version -> SELECT %n_columns %column FROM %table %join %condition %group_by %order_by %limit;
 *   											- column AS alias | agg_func(column) AS alias
 *    
 *  - Aggregate Functions
 *    COUNT()
 *    SUM()
 *    AVG()
 *    MIN()
 *    MAX()
 *    GROUP_CONCAT()
 *    STD() / STDDEV()
 *    STDDEV_POP()
 *    STDDEV_SAMP()
 *    VARIANCE()
 *    VAR_POP()
 *    VAR_SAMP()
 *    
 * Insert Statement Syntax:
 *  - INSERT INTO Table (column1, column2, ..., columnN) VALUES (value1, value2, ..., valueN);
 *  - INSERT INTO %table %columns VALUES %values;
 *    
 * Update Statement Syntax:
 *  - UPDATE table SET column1=value1, column2=value2, ..., columnN=valueN [ WHERE condition ];
 *  - UPDATE %table SET %columns %conditions;
 *  
 * Delete Statement Syntax:
 *  - DELETE FROM Table WHERE condition;
 *  - DELETE FROM %table %condition;
 * 
 * */

public class QueryBuilder {
	public String QueryString = "";
	public String statementType = "SELECT";
	public Table table;
	public resultModifierOptions resultModifier;
	public String ALL_COLUMNS = "*";
	public ArrayList<Join> join = new ArrayList<Join>();
	public GroupBy group_by;
	public OrderBy order_by;
	public LinkedHashMap<Column, Value<?>> values = new LinkedHashMap<Column, Value<?>>();
	public ArrayList<Object> valuesList = new ArrayList<Object>();
	public String limit = "";
	public String conjuction = "AND";
//	public boolean singleTable = true;

	public enum resultModifierOptions {
		ALL, DISTINCT
	}

	public ArrayList<Condition<?>> condition = new ArrayList<>();
	public ArrayList<Column> columns = new ArrayList<>();

	public enum statementFormat {
		SELECT("SELECT %n_columns %columns FROM %table %join %condition %group_by %order_by %limit"),
		INSERT("INSERT INTO %table %columns VALUES %values;"), UPDATE("UPDATE %table SET %columns %conditions;"),
		DELETE("DELETE FROM %table %conditions;");

		private String formatString;

		private statementFormat(String formatString) {
			this.formatString = formatString;
		}

		public String getFormatString() {
			return this.formatString;
		}

	}

	public void changeConjuction(String con) {
		this.conjuction = con;
	}

	public String getColumns() {
		if (this.columns.size() > 0) {
			ArrayList<String> columnsList = new ArrayList<>();
			this.columns.forEach(col -> columnsList.add(col.toString()));
			String columnsString = String.join(", ", columnsList);
			return columnsString;
		}
		if (join.size() > 0) {
			String AllColumnString = this.table.alias + "." + ALL_COLUMNS;
			for (Join j : this.join) {
				if (j.column1.table == this.table)
					AllColumnString += ", " + j.column2.table.alias + "." + ALL_COLUMNS;
				else
					AllColumnString += ", " + j.column1.table.alias + "." + ALL_COLUMNS;
			}
			return AllColumnString;
		}
		return ALL_COLUMNS;
	}

	public String getConditions() {
		if (this.condition.size() > 0) {
			ArrayList<String> conditionList = new ArrayList<>();
			this.condition.forEach(cnd -> {
				conditionList.add(cnd.toString());
				if (cnd.value instanceof QueryBuilder) {	
					QueryBuilder qb = (QueryBuilder) cnd.value;
					valuesList.addAll(qb.valuesList);
				}
			});
			String conditionString = String.join(" " + conjuction + " ", conditionList);
			return "WHERE " + conditionString;
		}
		return "";
	}

	public String getJoinString() {
		if (this.join.size() > 0) {
			ArrayList<String> joinList = new ArrayList<String>();
			this.join.forEach(j -> joinList.add(j.toString()));
			return String.join(" ", joinList);
		}
		return "";
	}

	public String getInsertColumns() {
		if (this.values.size() > 0) {
			ArrayList<String> colList = new ArrayList<String>();
			this.values.forEach((k, v) -> {
				colList.add(k.toString(this));
			});
			return "(" + String.join(", ", colList) + ")";
		}
		return "";
	}

	public String getValues() {
		if (this.values.size() > 0) {
			ArrayList<String> valList = new ArrayList<String>();
			this.values.forEach((k, v) -> {
//				if (v.value == null) {
//					valList.add(null);
//				} else if (v.value instanceof String) {
//					valList.add("\'" + v.value + "\'");
//				} else {
//					valList.add(v.value.toString());
//				}
				valuesList.add(v.value);
				valList.add(" ? ");
			});
			return "(" + String.join(", ", valList) + ")";
		}
		return "";
	}

	public String getUpdateColumns() {
		if (this.values.size() > 0) {
			ArrayList<String> valList = new ArrayList<String>();
			this.values.forEach((k, v) -> {
				String val = "";
				if (v.value == null) {
					val = null;
				} else if (v.value instanceof String) {
					val = "\"" + v.value + "\"";
				} else if (v.value instanceof Condition) {
					val = "(" + v.value.toString() + ")";
				} else {
					val = v.value.toString();
				}
				if (val != null)
					valList.add(k + "=" + val);
			});
			return String.join(", ", valList);
		}
		return "";
	}

	public QueryBuilder build() {
		switch (this.statementType) {
		case "SELECT": {
			statementFormat st = statementFormat.SELECT;
			this.QueryString = st.getFormatString();
			this.QueryString = this.QueryString.replace("%table", this.table.toString());
			this.QueryString = this.QueryString.replace("%n_columns",
					(this.resultModifier == null ? "" : this.resultModifier.toString()));
			this.QueryString = this.QueryString.replace("%columns", this.getColumns());
			this.QueryString = this.QueryString.replace("%condition", this.getConditions());
			this.QueryString = this.QueryString.replace("%join", this.getJoinString());
			this.QueryString = this.QueryString.replace("%group_by",
					this.group_by != null ? this.group_by.toString() : "");
			this.QueryString = this.QueryString.replace("%order_by",
					this.order_by != null ? this.order_by.toString() : "");
			this.QueryString = this.QueryString.replace("%limit", this.limit);
			this.QueryString = this.QueryString.trim() + ";";
			break;
		}
		case "INSERT": {
			statementFormat st = statementFormat.INSERT;
			this.QueryString = st.getFormatString();
			this.QueryString = this.QueryString.replace("%table", this.table.toString());
			this.QueryString = this.QueryString.replace("%columns", this.getInsertColumns());
			this.QueryString = this.QueryString.replace("%values", this.getValues());
			break;
		}
		case "UPDATE": {
			statementFormat st = statementFormat.UPDATE;
			this.QueryString = st.getFormatString();
			this.QueryString = this.QueryString.replace("%table", this.table.toString());
			this.QueryString = this.QueryString.replace("%columns", this.getUpdateColumns());
			this.QueryString = this.QueryString.replace("%conditions", this.getConditions());
			break;
		}
		case "DELETE": {
			statementFormat st = statementFormat.DELETE;
			this.QueryString = st.getFormatString();
			this.QueryString = this.QueryString.replace("%table", this.table.toString());
			this.QueryString = this.QueryString.replace("%conditions", this.getConditions());
			break;
		}
		}
		this.QueryString = this.QueryString.replaceAll("  ", " ");
		return this;
	}

	public void setStatementType(String statementType) {
		this.statementType = statementType;
	}

	public void selectTable(TableInfo name, String alias) {
		Table table = new Table(name, alias);
		this.table = table;
		this.statementType = statementFormat.SELECT.toString();
	}

	public void selectTable(TableInfo name) {
		Table table = new Table(name);
		this.table = table;
		this.statementType = statementFormat.SELECT.toString();
	}

	public void insertTable(TableInfo name) {
		Table table = new Table(name);
		this.table = table;
		this.statementType = statementFormat.INSERT.toString();
	}

	public void updateTable(TableInfo name) {
		Table table = new Table(name);
		this.table = table;
		this.statementType = statementFormat.UPDATE.toString();
	}

	public void deleteTable(TableInfo name) {
		Table table = new Table(name);
		this.table = table;
		this.statementType = statementFormat.DELETE.toString();
	}

	public void selectColumn(Column col) {
		this.columns.add(col);
	}

	public void selectColumns(ArrayList<Column> col) {
		if (this.columns.size() > 0) {
			col.forEach(c -> this.columns.add(c));
		} else {
			this.columns = col;
		}
	}

	public <T> void setCondition(Column col, Operators op, T val) {
		Condition<T> cnd = new Condition<>(col, op.toString(), val);
		this.condition.add(cnd);
	}

	public void setCondition(Condition<?> cnd) {
		this.condition.add(cnd);
	}

	public void setOrder(Column col) {
		this.order_by = new OrderBy(col, Order.asc);
	}

	public void setOrder(Column col, Order ord) {
		this.order_by = new OrderBy(col, ord);
	}

	public void setGroup(Column col) {
		this.group_by = new GroupBy(col);
	}

	public void setGroup(ArrayList<Column> cols) {
		this.group_by = new GroupBy(cols);
	}

	public void joinTables(JoinTypes joinType, Column col1, Column col2) {
		if (col1.table.alias == "") {
			col1.table.setDefaultAlias();
		}
		if (col2.table.alias == "") {
			col2.table.setDefaultAlias();
		}
		this.join.add(new Join(joinType, col1, col2));
	}

	public void setLimit(int limit) {
		this.limit = "LIMIT " + limit;
	}

	public void setLimit(int limit, int offset) {
		this.limit = "LIMIT " + limit + " OFFSET " + offset;
	}

	public <T> void insertValuesToColumns(Column col, T value) {
		Value<T> v = new Value<>(value);
		this.values.put(col, v);
	}

	public <T> void updateColumn(Column col, T value) {
		Value<T> v = new Value<>(value);
		this.values.put(col, v);
	}

	public String toString() {
		return this.QueryString;
	}
}
