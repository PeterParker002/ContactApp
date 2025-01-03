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
	private int currentPojoId = 0;

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

	public HashMap<String, Object> executeJoinQuery(QueryBuilder query)
			throws ClassNotFoundException, SQLException, IllegalAccessException, InvocationTargetException,
			InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException {

		try (Connection con = ConnectionPool.getDataSource().getConnection();
				PreparedStatement ps = con.prepareStatement(query.toString());
				ResultSet rs = ps.executeQuery();) {
			HashMap<String, Object> data = new HashMap<String, Object>();
			HashMap<String, ArrayList<String>> columns = new HashMap<String, ArrayList<String>>();
			HashMap<String, ArrayList<String>> colDetails = new HashMap<String, ArrayList<String>>();
			HashMap<String, String> tableAliases = new HashMap<String, String>();
			for (Join j : query.join) {
				tableAliases.putIfAbsent(j.column1.table.name.toString(), j.column1.table.alias);
				tableAliases.putIfAbsent(j.column2.table.name.toString(), j.column2.table.alias);
			}
			ArrayList<String> tables = new ArrayList<String>();
			ResultSetMetaData metadata = rs.getMetaData();
			for (int i = 1; i <= metadata.getColumnCount(); i++) {
				if (!tables.contains(metadata.getTableName(i))) {
					tables.add(metadata.getTableName(i));
					ArrayList<String> c = new ArrayList<String>();
					c.add(metadata.getColumnName(i).toLowerCase().replace("_", ""));
					columns.put(metadata.getTableName(i), c);
				} else {
					columns.get(metadata.getTableName(i)).add(metadata.getColumnName(i).toLowerCase().replace("_", ""));
				}
				ArrayList<String> col = new ArrayList<String>();
				col.add(tableAliases.get(metadata.getTableName(i)) + "." + metadata.getColumnName(i));
				col.add(metadata.getColumnTypeName(i));
				colDetails.put(metadata.getColumnName(i).toLowerCase().replace("_", ""), col);
			}
			Class<?> pojoClass = null;
			Object pojo;
			while (rs.next()) {
				for (String name : tables) {
					if (name.equalsIgnoreCase(query.table.name.toString()) & data.containsKey(name)) {
						continue;
					}
					if (name.equalsIgnoreCase(query.table.name.toString())) {
						pojoClass = getModelClassForTable(name);
						pojo = pojoClass.getDeclaredConstructor().newInstance();
						Method[] ms = pojoClass.getDeclaredMethods();
						for (Method m : ms) {
							if (m.getName().startsWith("set")
									& columns.get(name).contains(m.getName().replace("set", "").toLowerCase())) {
								ArrayList<String> col = colDetails.get(m.getName().replace("set", "").toLowerCase());
//							System.out.println(col.get(0) + " -> " + col.get(1));
								switch (col.get(1)) {
								case "INT":
									m.invoke(pojo, rs.getInt(col.get(0)));
									break;
								case "VARCHAR":
								case "TEXT":
								case "CHAR":
								case "DATE":
									m.invoke(pojo, rs.getString(col.get(0)));
									break;
								case "BIGINT":
									m.invoke(pojo, rs.getLong(col.get(0)));
									break;
								case "BIT":
									m.invoke(pojo, rs.getBoolean(col.get(0)));
									break;
								}
							}
						}
						data.put(name, pojo);
					} else {
						pojoClass = getModelClassForTable(name);
						pojo = pojoClass.getDeclaredConstructor().newInstance();
						Method[] ms = pojoClass.getDeclaredMethods();
						for (Method m : ms) {
							if (m.getName().startsWith("set")
									& columns.get(name).contains(m.getName().replace("set", "").toLowerCase())) {
								ArrayList<String> col = colDetails.get(m.getName().replace("set", "").toLowerCase());
								switch (col.get(1)) {
								case "INT":
									m.invoke(pojo, rs.getInt(col.get(0)));
									break;
								case "VARCHAR":
								case "TEXT":
								case "CHAR":
								case "DATE":
									m.invoke(pojo, rs.getString(col.get(0)));
									break;
								case "BIGINT":
									m.invoke(pojo, rs.getLong(col.get(0)));
									break;
								case "BIT":
									m.invoke(pojo, rs.getBoolean(col.get(0)));
									break;
								}
							}
						}
						if (!data.containsKey(name)) {
							ArrayList<Object> pojoList = new ArrayList<Object>();
							pojoList.add(pojo);
							data.put(name, pojoList);
						} else {
							ArrayList<Object> pojoList = (ArrayList<Object>) data.get(name);
							pojoList.add(pojo);
						}
					}
				}
			}
//			closeConnection(con);
			return data;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	protected Class<?> getModelClassForTable(String table) {
		switch (table) {
		case "User":
			return User.class;
		case "Contacts":
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

	public ArrayList<?> executeJoinQuery1(QueryBuilder query) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?> pojoClass = getModelClassForTable(query.table.getName().toString());
		Object pojo = pojoClass.getDeclaredConstructor().newInstance();
		try (Connection con = ConnectionPool.getDataSource().getConnection();
				PreparedStatement ps = con.prepareStatement(query.toString());
				ResultSet rs = ps.executeQuery();) {
			
			while (rs.next()) {
				pojo = populateDataOverPojo(rs, pojo);
				if (resultPojo.containsKey(currentPojoId)) {
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
		System.out.println(resultPojo);
		return new ArrayList<Object>(resultPojo.values());
	}

	public ArrayList<?> executeQuery(QueryBuilder query) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?> pojoClass = getModelClassForTable(query.table.getName().toString());
		Object pojo = pojoClass.getDeclaredConstructor().newInstance();
		try (Connection con = ConnectionPool.getDataSource().getConnection();
				PreparedStatement ps = con.prepareStatement(query.toString());
				ResultSet rs = ps.executeQuery();) {
			while (rs.next()) {
				pojo = populateDataOverPojo(rs, pojo);
				if (resultPojo.containsKey(currentPojoId)) {
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
		System.out.println(resultPojo);
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
				m.invoke(pojo, rs.getInt(columnName));
				if (columnName.equalsIgnoreCase("id") | columnName.equalsIgnoreCase(getUniqueIdColumn(tableName))) {
					currentPojoId = rs.getInt(columnName);
				}
				break;
			case "VARCHAR":
			case "TEXT":
			case "CHAR":
			case "DATE":
				m = pojo.getClass().getMethod(methodName, String.class);
				m.invoke(pojo, rs.getString(columnName));
				break;
			case "BIGINT":
				m = pojo.getClass().getMethod(methodName, long.class);
				m.invoke(pojo, rs.getLong(columnName));
				break;
			case "BIT":
				m = pojo.getClass().getMethod(methodName, boolean.class);
				m.invoke(pojo, rs.getBoolean(columnName));
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

//	private void closeConnection(Connection con) throws SQLException {
//		con.close();
//	}
}
