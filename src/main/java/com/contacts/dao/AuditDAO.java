package com.contacts.dao;

import java.sql.SQLException;
import java.time.LocalDateTime;

import com.contacts.querylayer.Column;
import com.contacts.querylayer.QueryBuilder;
import com.contacts.querylayer.QueryExecutor;
import com.contacts.utils.Database.Audit;
import com.contacts.utils.Database.TableInfo;
import com.contacts.utils.MyCustomJsonObject;

public class AuditDAO {
	public static void audit(String tableName, MyCustomJsonObject<String, Object> oldValue,
			MyCustomJsonObject<String, Object> newValue, String action, LocalDateTime createdAt) {
		boolean isOldValueEmpty = oldValue != null && !oldValue.isEmpty();
		boolean isNewValueEmpty = newValue != null && !newValue.isEmpty();
		if (!isOldValueEmpty && !isNewValueEmpty) {
			return;
		}
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.insertTable(TableInfo.AUDIT);
		qb.insertValuesToColumns(new Column(Audit.TABLENAME, "", "", qb.table), tableName);
		if (isOldValueEmpty)
			qb.insertValuesToColumns(new Column(Audit.OLDVALUE, "", "", qb.table), oldValue.toString());
		if (isNewValueEmpty)
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
