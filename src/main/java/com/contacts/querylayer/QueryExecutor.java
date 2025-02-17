package com.contacts.querylayer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.contacts.model.User;
import com.contacts.model.UserMail;
import com.contacts.model.UserMobile;
import com.contacts.connection.ConfigurationLoader;
import com.contacts.connection.ConnectionPool;
import com.contacts.dao.AuditDAO;
import com.contacts.model.Contact;
import com.contacts.model.ContactMail;
import com.contacts.model.ContactMobile;
import com.contacts.model.Group;
import com.contacts.model.Server;
import com.contacts.model.Session;
import com.contacts.utils.Database;
import com.contacts.utils.DatabaseImpl;
import com.contacts.utils.MyCustomJsonObject;

import java.sql.ResultSetMetaData;

public class QueryExecutor {
	private HashMap<Integer, Object> resultPojo = new HashMap<Integer, Object>();
	public int currentPojoId = 0;
	public Object currentPojo;
	public Object basePojo;
	public String currentTableName = "";
	private static boolean audit = true;

	static {
		audit = "true".equals(ConfigurationLoader.getProperty("audit"));
	}

	public PreparedStatement fillPreparedStatement(PreparedStatement ps, QueryBuilder qb) throws SQLException {
		for (int i = 0; i < qb.valuesList.size(); i++) {
			ps.setObject(i + 1, qb.valuesList.get(i));
		}
		return ps;
	}

	public Class<?> getModelClassForTable(String table) {
		switch (table) {
		case "User":
			return User.class;
		case "Contact":
			return Contact.class;
		case "contacts_mail_ids":
			return ContactMail.class;
		case "User_mail_ids":
			return UserMail.class;
		case "contacts_mobile_numbers":
			return ContactMobile.class;
		case "user_mobile_numbers":
			return UserMobile.class;
		case "Group_info":
		case "Group_details":
			return Group.class;
		case "Session":
			return Session.class;
		case "available_servers":
			return Server.class;
		default:
			return null;
		}
	}

	private <T> T populateDataOverPojo(ResultSet rs, T pojo)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		try {
			ResultSetMetaData metadata = rs.getMetaData();
			for (int col = 1; col <= metadata.getColumnCount(); col++) {
				String columnName = metadata.getColumnName(col);
				String columnType = metadata.getColumnTypeName(col);
				String tableName = metadata.getTableName(col);
				String methodName = "set";
				for (String s : columnName.split("_")) {
					methodName += s.substring(0, 1).toUpperCase() + s.substring(1);
				}
				pojo = dataMapperForPojo(rs, pojo, methodName, tableName, columnName, columnType);
			}
		} catch (SQLException | SecurityException e) {
			e.printStackTrace();
		}
		return pojo;
	}

	public void updateBasePojo(Object pojo)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
		if (!isPojoAlreadyExists(pojo)) {
			Method m = basePojo.getClass().getMethod("update", pojo.getClass());
			m.invoke(basePojo, pojo);
		}
	}

	@SuppressWarnings("unchecked")
	public boolean isPojoAlreadyExists(Object pojo)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
		Method m = basePojo.getClass().getMethod("getData", pojo.getClass());
		ArrayList<Object> pojoList = (ArrayList<Object>) m.invoke(basePojo, pojo);
		int pojoId = (int) pojo.getClass().getMethod("getUniqueID").invoke(pojo);
		for (Object obj : pojoList) {
			if (pojoId == (int) obj.getClass().getMethod("getUniqueID").invoke(obj)) {
				return true;
			}
		}
		return false;
	}

	public boolean isBasePojoAlreadyExists(ArrayList<Object> pojoList, Object pojo)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
		int pojoId = (int) pojo.getClass().getMethod("getUniqueID").invoke(pojo);
		for (Object obj : pojoList) {
			if (pojoId == (int) obj.getClass().getMethod("getUniqueID").invoke(obj)) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<?> executeJoinQuery1(QueryBuilder query) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		try (Connection con = ConnectionPool.getDataSource().getConnection();
				PreparedStatement ps = fillPreparedStatement(con.prepareStatement(query.toString()), query);
				ResultSet rs = ps.executeQuery();) {
			ArrayList<Object> result = new ArrayList<Object>();
			ResultSetMetaData metadata = rs.getMetaData();
			while (rs.next()) {
				for (int i = 1; i <= metadata.getColumnCount(); i++) {
					if (!currentTableName.equals(metadata.getTableName(i))) {
						if (!currentTableName.equals("") & !currentTableName.equals(query.table.getName().toString())) {
							if (currentPojo != null)
								updateBasePojo(currentPojo);
						}
						currentTableName = metadata.getTableName(i);
						Class<?> pojoClass = getModelClassForTable(currentTableName);
						currentPojo = pojoClass.getDeclaredConstructor().newInstance();
					}
					String columnName = metadata.getColumnName(i);
					String columnType = metadata.getColumnTypeName(i);
					String tableName = metadata.getTableName(i);
					String methodName = "set";
					for (String s : columnName.split("_")) {
						methodName += s.substring(0, 1).toUpperCase() + s.substring(1);
					}
					currentPojo = dataMapperForPojo(rs, currentPojo, methodName, tableName, columnName, columnType);
					if (currentPojo == null)
						continue;
					if (basePojo == null) {
						basePojo = currentPojo;
					} else if (!isBasePojoAlreadyExists(result, currentPojo)
							& currentTableName.equals(query.table.getName().toString())) {
						basePojo = currentPojo;
					}
				}
				if (currentPojo != null)
					updateBasePojo(currentPojo);
				if (!isBasePojoAlreadyExists(result, basePojo)) {
					result.add(basePojo);
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public ArrayList<?> executeQuery(QueryBuilder query) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?> pojoClass = getModelClassForTable(query.table.getName().toString());
		Object pojo = pojoClass.getDeclaredConstructor().newInstance();
		int i = 1;
		try (Connection con = ConnectionPool.getDataSource().getConnection();
				PreparedStatement ps = fillPreparedStatement(con.prepareStatement(query.toString()), query);
				ResultSet rs = ps.executeQuery();) {
			while (rs.next()) {
				pojo = populateDataOverPojo(rs, pojo);
				if (currentPojoId == 0) {
					resultPojo.put(currentPojoId - i, pojo);
					i++;
				} else if (resultPojo.containsKey(currentPojoId)) {
					Object prevPojo = resultPojo.get(currentPojoId);
					prevPojo.getClass().getMethod("update", pojo.getClass()).invoke(prevPojo, pojo);
				} else {
					resultPojo.put(currentPojoId, pojo);
				}
				pojo = pojoClass.getDeclaredConstructor().newInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return new ArrayList<Object>(resultPojo.values());
	}

	private String getUniqueIdColumn(String tableName) {
		if (tableName.toLowerCase().startsWith("group")) {
			tableName = "group";
		}
		String columnName = tableName.toLowerCase() + "_id";
		return columnName;
	}

	private <T> T dataMapperForPojo(ResultSet rs, T pojo, String methodName, String tableName, String columnName,
			String columnType)
			throws SecurityException, IllegalAccessException, InvocationTargetException, SQLException {
		try {
			Method m;
			if (pojo != null) {
				switch (columnType) {
				case "INT":
					m = pojo.getClass().getMethod(methodName, int.class);
					if (rs.getInt(tableName + "." + columnName) == 0)
						return null;
					m.invoke(pojo, rs.getInt(tableName + "." + columnName));
					if (columnName.equalsIgnoreCase("id") | columnName.equalsIgnoreCase(getUniqueIdColumn(tableName))) {
						currentPojoId = rs.getInt(columnName);
					}
					break;
				case "VARCHAR":
				case "TEXT":
				case "CHAR":
				case "DATE":
					m = pojo.getClass().getMethod(methodName, String.class);
					m.invoke(pojo, rs.getString(tableName + "." + columnName));
					break;
				case "BIGINT":
					m = pojo.getClass().getMethod(methodName, long.class);
					m.invoke(pojo, rs.getLong(tableName + "." + columnName));
					break;
				case "BIT":
					m = pojo.getClass().getMethod(methodName, boolean.class);
					m.invoke(pojo, rs.getBoolean(tableName + "." + columnName));
					break;
				}
			}
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return pojo;
	}

	public int executeAndUpdateWithKeys(QueryBuilder query) throws ClassNotFoundException, SQLException {
		try (Connection con = ConnectionPool.getDataSource().getConnection();
				PreparedStatement ps = fillPreparedStatement(
						con.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS), query);) {
			MyCustomJsonObject<String, Object> oldValue = new MyCustomJsonObject<String, Object>();
			MyCustomJsonObject<String, Object> newValue = null;
			if (query.statementType.equals("INSERT") & Database.auditableTables.contains(query.table.name)) {
				oldValue = null;
				newValue = new MyCustomJsonObject<String, Object>();
			}
			int result = ps.executeUpdate();
			if (result > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) {
					if (audit) {
						triggerAudit(query, rs.getInt(1), oldValue, newValue);
					}
					return rs.getInt(1);
				}
			}
			return -1;
		} catch (Exception e) {
			System.out.println(e);
		}

		return -1;
	}

	@SuppressWarnings("unchecked")
	public int executeAndUpdate(QueryBuilder query) throws ClassNotFoundException, SQLException {
		try (Connection con = ConnectionPool.getDataSource().getConnection();
				PreparedStatement ps = fillPreparedStatement(con.prepareStatement(query.toString()), query);) {
			MyCustomJsonObject<String, Object> oldValue = new MyCustomJsonObject<String, Object>();
			MyCustomJsonObject<String, Object> newValue = null;
			if (query.statementType.equals("INSERT") & Database.auditableTables.contains(query.table.name)) {
				oldValue = null;
				newValue = new MyCustomJsonObject<String, Object>();
			}
			if (query.statementType.equals("UPDATE") & Database.auditableTables.contains(query.table.name)) {
				oldValue = retreiveOldData(query);
				newValue = (MyCustomJsonObject<String, Object>) oldValue.clone();
			}
			if (query.statementType.equals("DELETE") & Database.auditableTables.contains(query.table.name)) {
				oldValue = retreiveOldData(query);
				newValue = null;
			}
			int result = ps.executeUpdate();
			if (audit) {
				triggerAudit(query, -1, oldValue, newValue);
			}
			return result;
		} catch (Exception e) {
			System.out.println(e);
		}
		return -1;
	}

	private MyCustomJsonObject<String, Object> retreiveOldData(QueryBuilder query) {
		MyCustomJsonObject<String, Object> jsonData = null;
		QueryBuilder qb = new QueryBuilder();
		boolean isDeleteQuery = query.statementType.equals("DELETE");
		qb.selectTable(query.table.getName());
		try {
			Object clazz = getModelClassForTable(query.table.toString()).getDeclaredConstructor().newInstance();
			Method m = clazz.getClass().getMethod("getPrimaryKeyColumn");
			DatabaseImpl idColumnName = (DatabaseImpl) m.invoke(clazz);
			if (!isDeleteQuery) {
				qb.selectColumn(new Column(idColumnName, "", "", qb.table));
				query.values.forEach((col, v) -> {
					qb.selectColumn(col);
				});
			}
			query.condition.forEach((cond) -> {
				qb.setCondition(cond);
			});
			jsonData = dataMapperForJson(qb.build());
			System.out.println(jsonData);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return jsonData;
	}

	private MyCustomJsonObject<String, Object> dataMapperForJson(QueryBuilder query) {
		MyCustomJsonObject<String, Object> jsonData = new MyCustomJsonObject<String, Object>();
		try (Connection con = ConnectionPool.getDataSource().getConnection();
				PreparedStatement ps = fillPreparedStatement(con.prepareStatement(query.toString()), query);
				ResultSet rs = ps.executeQuery();) {
			ResultSetMetaData metadata = rs.getMetaData();
			while (rs.next()) {
				for (int i = 1; i <= metadata.getColumnCount(); i++) {
					jsonData.put(metadata.getColumnName(i), rs.getString(i));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jsonData;
	}

	private void triggerAudit(QueryBuilder query, int primaryKey, MyCustomJsonObject<String, Object> oldValue,
			MyCustomJsonObject<String, Object> newValue) {
		String tableName = query.table.toString();
		boolean isTableAuditable = Database.auditableTables.contains(query.table.name);
		boolean isDeleteQuery = query.statementType.equals("DELETE");
		if (isTableAuditable) {
			if (primaryKey > 0) {
				try {
					Object clazz = getModelClassForTable(tableName).getDeclaredConstructor().newInstance();
					Method m = clazz.getClass().getMethod("getPrimaryKeyColumn");
					DatabaseImpl idColumnName = (DatabaseImpl) m.invoke(clazz);
					newValue.put(idColumnName.toString(), primaryKey);
				} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			if (!isDeleteQuery) {
				for (Entry<Column, Value<?>> val : query.values.entrySet()) {
					newValue.put(val.getKey().toString(), val.getValue().value);
				}
			}
			System.out.println("Old Value: " + oldValue);
			System.out.println("New Value: " + newValue);
			AuditDAO.audit(tableName, oldValue, newValue, query.statementType, LocalDateTime.now());
		}
	}

}
