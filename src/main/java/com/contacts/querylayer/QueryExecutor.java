package com.contacts.querylayer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.sql.DataSource;

import com.contacts.model.User;
import com.contacts.model.UserMail;
import com.contacts.model.UserMobile;
import com.contacts.connection.ConnectionPool;
import com.contacts.model.Contact;
import com.contacts.model.ContactMail;
import com.contacts.model.ContactMobile;
import com.contacts.model.Group;
import com.contacts.model.Mail;
import com.contacts.model.MobileNumber;
import com.contacts.model.Server;
import com.contacts.model.Session;
import com.contacts.utils.Database;
import com.contacts.utils.Database.ContactEmail;
import com.contacts.utils.Database.ContactMobileNumber;
import com.contacts.utils.Database.Contacts;
import com.contacts.utils.Database.GroupDetails;
import com.contacts.utils.Database.GroupInfo;
import com.contacts.utils.Database.TableInfo;
import com.contacts.utils.Database.UserEmail;
import com.contacts.utils.Database.UserMobileNumber;
import com.contacts.utils.Database.Users;
import com.mysql.cj.exceptions.NumberOutOfRange;

import java.sql.ResultSetMetaData;

/*
 * 
 * QueryBuilder -> toString() -> QueryString
 * 				-> Table -> getName() -> TableInfo
 * 
 * */

public class QueryExecutor {
	private String username = "root";
	private String password = "root";
	private String db_name = "ContactsApp";
	private HashMap<Integer, Object> resultPojo = new HashMap<Integer, Object>();
	public int currentPojoId = 0;
	public Object currentPojo;
	public Object basePojo;
	public String currentTableName = "";

	private Connection getConnection() throws ClassNotFoundException, SQLException {
		Connection con = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + db_name, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
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
				PreparedStatement ps = con.prepareStatement(query.toString());
				ResultSet rs = ps.executeQuery();) {
			ArrayList<Object> result = new ArrayList<Object>();
			ResultSetMetaData metadata = rs.getMetaData();
			while (rs.next()) {
				for (int i = 1; i <= metadata.getColumnCount(); i++) {
					if (!currentTableName.equals(metadata.getTableName(i))) {
						if (!currentTableName.equals("") & !currentTableName.equals(query.table.getName().toString())) {
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
					if (basePojo == null) {
						basePojo = currentPojo;
					} else if (!isBasePojoAlreadyExists(result, currentPojo)
							& currentTableName.equals(query.table.getName().toString())) {
						basePojo = currentPojo;
					}
				}
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
//		System.out.println(query);
		Class<?> pojoClass = getModelClassForTable(query.table.getName().toString());
		Object pojo = pojoClass.getDeclaredConstructor().newInstance();
		int i = 1;
		try (Connection con = ConnectionPool.getDataSource().getConnection();
				PreparedStatement ps = con.prepareStatement(query.toString());
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
//		System.out.println(resultPojo);
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
			switch (columnType) {
			case "INT":
				m = pojo.getClass().getMethod(methodName, int.class);
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
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return pojo;
	}

	public int executeAndUpdateWithKeys(QueryBuilder query) throws ClassNotFoundException, SQLException {
		try (Connection con = ConnectionPool.getDataSource().getConnection();
				PreparedStatement ps = con.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);) {
			int result = ps.executeUpdate();
			if (result > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
			return -1;
		} catch (Exception e) {
			System.out.println(e);
		}

		return -1;
	}

	public int executeAndUpdate(QueryBuilder query) throws ClassNotFoundException, SQLException {
		try (Connection con = ConnectionPool.getDataSource().getConnection();
				PreparedStatement ps = con.prepareStatement(query.toString());) {
			int result = ps.executeUpdate();
			return result;
		} catch (Exception e) {
			System.out.println(e);
		}

		return -1;
	}

}
