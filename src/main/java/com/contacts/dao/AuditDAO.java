package com.contacts.dao;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;

import com.contacts.querylayer.Column;
import com.contacts.querylayer.QueryBuilder;
import com.contacts.querylayer.QueryExecutor;
import com.contacts.utils.Database.Audit;
import com.contacts.utils.Database.TableInfo;
import com.contacts.utils.MyCustomJsonObject;

public class AuditDAO {
	public static void audit(String tableName, MyCustomJsonObject<String, Object> oldValue, MyCustomJsonObject<String, Object> newValue,
			String action, LocalDateTime createdAt) {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.insertTable(TableInfo.AUDIT);
		qb.insertValuesToColumns(new Column(Audit.TABLENAME, "", "", qb.table), tableName);
		if (oldValue != null)
			qb.insertValuesToColumns(new Column(Audit.OLDVALUE, "", "", qb.table), oldValue.toString());
		if (newValue != null)
			qb.insertValuesToColumns(new Column(Audit.NEWVALUE, "", "", qb.table), newValue.toString());
		qb.insertValuesToColumns(new Column(Audit.ACTION, "", "", qb.table), action);
		qb.insertValuesToColumns(new Column(Audit.CREATEDAT, "", "", qb.table), createdAt.toString());
		try {
			qx.executeAndUpdate(qb.build());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
}
