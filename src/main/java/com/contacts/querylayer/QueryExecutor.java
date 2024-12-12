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

import com.contacts.model.User;
import com.contacts.model.Contact;
import com.contacts.model.Group;
import com.contacts.model.Mail;
import com.contacts.model.MobileNumber;
import com.contacts.utils.Database.ContactMail;
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

//	public HashMap<String, Object> executeJoinQuery(QueryBuilder query)
//			throws ClassNotFoundException, SQLException, IllegalAccessException, InvocationTargetException,
//			InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException {
//		Connection con = getConnection();
//		PreparedStatement ps = con.prepareStatement(query.toString());
//		ResultSet rs = ps.executeQuery();
//		HashMap<String, Object> data = new HashMap<String, Object>();
//		while (rs.next()) {
//			ResultSetMetaData metadata = rs.getMetaData();
//			int cursor = 1;
//			Object pojo = null;
//			while (cursor < metadata.getColumnCount()) {
//					/*& query.table.name.toString().equals(metadata.getTableName(cursor))) {*/
//				if (!data.containsKey(metadata.getTableName(cursor))) {					
//					Class<?> pojoClass = getModelClassForTable(metadata.getTableName(cursor));
//					pojo = pojoClass.getDeclaredConstructor().newInstance();
//				}
//				else {
//					pojo = data.get(metadata.getTableName(cursor));
//				}
//				Method[] ms = pojo.getClass().getDeclaredMethods();
//				for (Method m : ms) {
//					if (m.getName().startsWith("set")) {
//						if (m.getName().substring(3)
//								.equalsIgnoreCase(metadata.getColumnName(cursor).replace("_", ""))) {
//							switch (metadata.getColumnTypeName(cursor)) {
//							case "INT":
//								m.invoke(pojo, rs.getInt(metadata.getColumnName(cursor)));
//								break;
//							case "VARCHAR":
//							case "TEXT":
//							case "CHAR":
//							case "DATE":
//								m.invoke(pojo, rs.getString(metadata.getColumnName(cursor)));
//								break;
//							case "BIGINT":
//								m.invoke(pojo, rs.getLong(metadata.getColumnName(cursor)));
//								break;
//							case "BIT":
//								m.invoke(pojo, rs.getBoolean(metadata.getColumnName(cursor)));
//								break;
//							}
//							System.out.println(
//									metadata.getColumnName(cursor) + " -> " + metadata.getColumnTypeName(cursor));
//						}
//					}
//				}
//				data.put(metadata.getTableName(cursor), pojo);
//				cursor++;
//			}
//		}
//		return data;
//	}

	public HashMap<String, Object> executeJoinQuery(QueryBuilder query)
			throws ClassNotFoundException, SQLException, IllegalAccessException, InvocationTargetException,
			InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException {
		System.out.println(query);
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement(query.toString());
		ResultSet rs = ps.executeQuery();
		HashMap<String, Object> data = new HashMap<String, Object>();
		HashMap<String, ArrayList<String>> columns = new HashMap<String, ArrayList<String>>();
		HashMap<String, ArrayList<String>> colDetails = new HashMap<String, ArrayList<String>>();
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
			col.add(metadata.getTableName(i) + "." + metadata.getColumnName(i));
			col.add(metadata.getColumnTypeName(i));
			colDetails.put(metadata.getColumnName(i).toLowerCase().replace("_", ""), col);
		}
		System.out.println(columns);
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
//			System.out.println(data);
		}
		return data;
	}

	protected Class<?> getModelClassForTable(String table) {
		switch (table) {
		case "User":
			return User.class;
		case "Contacts":
			return Contact.class;
		case "contacts_mail_ids":
		case "User_mail_ids":
			return Mail.class;
		case "contacts_mobile_numbers":
		case "user_mobile_numbers":
			return MobileNumber.class;
		default:
			return Object.class;
		}
	}

	public ArrayList<?> executeQuery(QueryBuilder query)
			throws SQLException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement(query.toString());
		ResultSet rs = ps.executeQuery();
		ArrayList<String> res = new ArrayList<String>();
		switch (query.table.getName()) {
		case TableInfo.USER: {
			ArrayList<User> users = new ArrayList<User>();
			while (rs.next()) {
				ResultSetMetaData metadata = rs.getMetaData();
				User user = new User();
				if (query.columns.size() == 0) {
					user.setUserId(rs.getInt(Users.USERID.toString()));
					user.setUsername(rs.getString(Users.USERNAME.toString()));
					user.setPassword(rs.getString(Users.PASSWORD.toString()));
					user.setFirstName(rs.getString(Users.FIRSTNAME.toString()));
					user.setMiddleName(rs.getString(Users.MIDDLENAME.toString()));
					user.setLastName(rs.getString(Users.LASTNAME.toString()));
					user.setGender(rs.getString(Users.GENDER.toString()));
					user.setDateOfBirth(rs.getString(Users.DATEOFBIRTH.toString()));
					user.setNotes(rs.getString(Users.NOTES.toString()));
					user.setHomeAddress(rs.getString(Users.HOMEADDRESS.toString()));
					user.setWorkAddress(rs.getString(Users.WORKADDRESS.toString()));
					user.setIsHashed(rs.getInt(Users.ISHASHED.toString()));
					users.add(user);
				} else {
					int i = 1;
					for (Column col : query.columns) {
						Method[] ms = user.getClass().getDeclaredMethods();
						for (Method m : ms) {
							if (m.getName().startsWith("set")) {
								if (m.getName().substring(3).equalsIgnoreCase(col.toString().replace("_", ""))) {
									switch (metadata.getColumnTypeName(i)) {
									case "INT":
										m.invoke(user, rs.getInt(col.toString()));
										break;
									case "VARCHAR":
									case "TEXT":
									case "CHAR":
									case "DATE":
										m.invoke(user, rs.getString(col.toString()));
										break;
									case "BIGINT":
										m.invoke(user, rs.getLong(col.toString()));
										break;
									case "BIT":
										m.invoke(user, rs.getBoolean(col.toString()));
										break;
									}
									System.out.println(
											metadata.getColumnName(i) + " -> " + metadata.getColumnTypeName(i));
								}
							}
						}
						i++;
					}
					users.add(user);
				}
			}
			return users;
		}
		case TableInfo.USEREMAIL: {
			ArrayList<Mail> mails = new ArrayList<Mail>();
			while (rs.next()) {
				ResultSetMetaData metadata = rs.getMetaData();
				Mail mail = new Mail();
				if (query.columns.size() == 0) {
					mail.setId(rs.getInt(UserEmail.ID.toString()));
					mail.setEmail(rs.getString(UserEmail.EMAIL.toString()));
					mail.setIsPrimary(rs.getBoolean(UserEmail.ISPRIMARY.toString()));
				} else {
					int i = 1;
					for (Column col : query.columns) {
						Method[] ms = mail.getClass().getDeclaredMethods();
						for (Method m : ms) {
							if (m.getName().startsWith("set")) {
								if (m.getName().substring(3).equalsIgnoreCase(col.toString().replace("_", ""))) {
									switch (metadata.getColumnTypeName(i)) {
									case "INT":
										m.invoke(mail, rs.getInt(col.toString()));
										break;
									case "VARCHAR":
									case "TEXT":
									case "CHAR":
									case "DATE":
										m.invoke(mail, rs.getString(col.toString()));
										break;
									case "BIGINT":
										m.invoke(mail, rs.getLong(col.toString()));
										break;
									case "BIT":
										m.invoke(mail, rs.getBoolean(col.toString()));
										break;
									}
									System.out.println(
											metadata.getColumnName(i) + " -> " + metadata.getColumnTypeName(i));
								}
							}
						}
						i++;
					}
					mails.add(mail);
				}
			}
			return mails;
		}
		case TableInfo.USERMOBILENUMBER: {
			ArrayList<MobileNumber> mobiles = new ArrayList<MobileNumber>();
			while (rs.next()) {
				ResultSetMetaData metadata = rs.getMetaData();
				MobileNumber mobile = new MobileNumber();
				if (query.columns.size() == 0) {
					mobile.setId(rs.getInt(UserMobileNumber.ID.toString()));
					mobile.setMobileNumber(rs.getLong(UserMobileNumber.MOBILENUMBER.toString()));
				} else {
					int i = 1;
					for (Column col : query.columns) {
						Method[] ms = mobile.getClass().getDeclaredMethods();
						for (Method m : ms) {
							if (m.getName().startsWith("set")) {
								if (m.getName().substring(3).equalsIgnoreCase(col.toString().replace("_", ""))) {
									switch (metadata.getColumnTypeName(i)) {
									case "INT":
										m.invoke(mobile, rs.getInt(col.toString()));
										break;
									case "VARCHAR":
									case "TEXT":
									case "CHAR":
									case "DATE":
										m.invoke(mobile, rs.getString(col.toString()));
										break;
									case "BIGINT":
										m.invoke(mobile, rs.getLong(col.toString()));
										break;
									case "BIT":
										m.invoke(mobile, rs.getBoolean(col.toString()));
										break;
									}
								}
							}
						}
						i++;
					}
				}
				mobiles.add(mobile);
			}
			return mobiles;
		}
		case TableInfo.CONTACTS: {
			ArrayList<Contact> contacts = new ArrayList<Contact>();
			while (rs.next()) {
				ResultSetMetaData metadata = rs.getMetaData();
				Contact contact = new Contact();
				if (query.columns.size() == 0) {
					contact.setContact_id(rs.getInt(Contacts.CONTACTID.toString()));
					contact.setUser_id(rs.getInt(Contacts.USERID.toString()));
					contact.setFirstName(rs.getString(Contacts.FIRSTNAME.toString()));
					contact.setMiddleName(rs.getString(Contacts.MIDDLENAME.toString()));
					contact.setLastName(rs.getString(Contacts.LASTNAME.toString()));
					contact.setGender(rs.getString(Contacts.GENDER.toString()));
					contact.setDateOfBirth(rs.getString(Contacts.DATEOFBIRTH.toString()));
					contact.setNotes(rs.getString(Contacts.NOTES.toString()));
					contact.setHomeAddress(rs.getString(Contacts.HOMEADDRESS.toString()));
					contact.setWorkAddress(rs.getString(Contacts.WORKADDRESS.toString()));
				} else {
					int i = 1;
					for (Column col : query.columns) {
						Method[] ms = contact.getClass().getDeclaredMethods();
						for (Method m : ms) {
							if (m.getName().startsWith("set")) {
								if (m.getName().substring(3).equalsIgnoreCase(col.toString().replace("_", ""))) {
									switch (metadata.getColumnTypeName(i)) {
									case "INT":
										m.invoke(contact, rs.getInt(col.toString()));
										break;
									case "VARCHAR":
									case "TEXT":
									case "CHAR":
									case "DATE":
										m.invoke(contact, rs.getString(col.toString()));
										break;
									case "BIGINT":
										m.invoke(contact, rs.getLong(col.toString()));
										break;
									case "BIT":
										m.invoke(contact, rs.getBoolean(col.toString()));
										break;
									}
									System.out.println(
											metadata.getColumnName(i) + " -> " + metadata.getColumnTypeName(i));
								}
							}
						}
						i++;
					}
				}
				contacts.add(contact);
			}
			return contacts;
		}
		case TableInfo.CONTACTMAIL: {
			ArrayList<Mail> mails = new ArrayList<Mail>();
			while (rs.next()) {
				ResultSetMetaData metadata = rs.getMetaData();
				Mail mail = new Mail();
				if (query.columns.size() == 0) {
					mail.setId(rs.getInt(ContactMail.ID.toString()));
					mail.setEmail(rs.getString(ContactMail.EMAIL.toString()));
				} else {
					int i = 1;
					for (Column col : query.columns) {
						Method[] ms = mail.getClass().getDeclaredMethods();
						for (Method m : ms) {
							if (m.getName().startsWith("set")) {
								if (m.getName().substring(3).equalsIgnoreCase(col.toString().replace("_", ""))) {
									switch (metadata.getColumnTypeName(i)) {
									case "INT":
										m.invoke(mail, rs.getInt(col.toString()));
										break;
									case "VARCHAR":
									case "TEXT":
									case "CHAR":
									case "DATE":
										m.invoke(mail, rs.getString(col.toString()));
										break;
									case "BIGINT":
										m.invoke(mail, rs.getLong(col.toString()));
										break;
									case "BIT":
										m.invoke(mail, rs.getBoolean(col.toString()));
										break;
									}
									System.out.println(
											metadata.getColumnName(i) + " -> " + metadata.getColumnTypeName(i));
								}
							}
						}
						i++;
					}
				}
				mails.add(mail);
			}
			return mails;
		}
		case TableInfo.CONTACTMOBILENUMBER: {
			ArrayList<MobileNumber> mobiles = new ArrayList<MobileNumber>();
			while (rs.next()) {
				ResultSetMetaData metadata = rs.getMetaData();
				MobileNumber mobile = new MobileNumber();
				if (query.columns.size() == 0) {
					mobile.setId(rs.getInt(ContactMobileNumber.ID.toString()));
					mobile.setMobileNumber(rs.getLong(ContactMobileNumber.MOBILENUMBER.toString()));
				} else {
					int i = 1;
					for (Column col : query.columns) {
						Method[] ms = mobile.getClass().getDeclaredMethods();
						for (Method m : ms) {
							if (m.getName().startsWith("set")) {
								if (m.getName().substring(3).equalsIgnoreCase(col.toString().replace("_", ""))) {
									switch (metadata.getColumnTypeName(i)) {
									case "INT":
										m.invoke(mobile, rs.getInt(col.toString()));
										break;
									case "VARCHAR":
									case "TEXT":
									case "CHAR":
									case "DATE":
										m.invoke(mobile, rs.getString(col.toString()));
										break;
									case "BIGINT":
										m.invoke(mobile, rs.getLong(col.toString()));
										break;
									case "BIT":
										m.invoke(mobile, rs.getBoolean(col.toString()));
										break;
									}
									System.out.println(
											metadata.getColumnName(i) + " -> " + metadata.getColumnTypeName(i));
								}
							}
						}
						i++;
					}
				}
				mobiles.add(mobile);
			}
			return mobiles;
		}
		case TableInfo.GROUPINFO: {
			ArrayList<Group> groups = new ArrayList<Group>();
			while (rs.next()) {
				Group group = new Group();
				group.setGroupId(rs.getInt(GroupInfo.GROUPID.toString()));
				ArrayList<Contact> contacts = new ArrayList<Contact>();
				for (String contactId : rs.getString(GroupInfo.CONTACTID.toString()).split(",")) {
					Contact c = new Contact();
					c.setContact_id(Integer.parseInt(contactId));
					contacts.add(c);
				}
				group.setContact(contacts);
				groups.add(group);
			}
			return groups;
		}
		case TableInfo.GROUPDETAILS: {
			ArrayList<Group> groups = new ArrayList<Group>();
			while (rs.next()) {
				Group group = new Group();
				group.setGroupId(rs.getInt(GroupDetails.GROUPID.toString()));
				group.setUserId(rs.getInt(GroupDetails.USERID.toString()));
				group.setGroupName(rs.getString(GroupDetails.GROUPNAME.toString()));
				groups.add(group);
			}
			return groups;
		}
		default:
			break;
		}
		closeConnection(con);
		return res;
	}

	public int executeAndUpdateWithKeys(QueryBuilder query) throws ClassNotFoundException, SQLException {
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
		int result = ps.executeUpdate();
		if (result > 0) {
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				return rs.getInt(1);
			}
		}
		closeConnection(con);
		return -1;
	}

	public int executeAndUpdate(QueryBuilder query) throws ClassNotFoundException, SQLException {
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement(query.toString());
		int result = ps.executeUpdate();
		closeConnection(con);
		return result;
	}

	private void closeConnection(Connection con) throws SQLException {
		con.close();
	}
}
