package com.contacts.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
//import java.util.Scanner;
import java.util.HashMap;

import com.contacts.model.User;
import com.contacts.dao.UserDAO;
import com.contacts.model.Contact;
import com.contacts.model.Group;
//import com.contacts.dao.UserDAO;
import com.contacts.model.Mail;
import com.contacts.model.MobileNumber;
//import com.contacts.model.MobileNumber;
//import com.contacts.model.Mail;
//import com.contacts.model.User;
import com.contacts.querylayer.Column;
import com.contacts.querylayer.QueryBuilder;
import com.contacts.querylayer.QueryExecutor;
import com.contacts.querylayer.Table;
import com.contacts.utils.Database.ContactMail;
import com.contacts.utils.Database.ContactMobileNumber;
import com.contacts.utils.Database.Contacts;
import com.contacts.utils.Database.GroupInfo;
import com.contacts.utils.Database.Sample;
import com.contacts.utils.Database.TableInfo;
import com.contacts.utils.Database.UserEmail;
import com.contacts.utils.Database.UserMobileNumber;
import com.contacts.utils.Database.Users;
import com.contacts.utils.JoinTypes;
import com.contacts.utils.Operators;
//import com.contacts.querylayer.Table;
//import com.contacts.utils.Database.Sample;
//import com.contacts.utils.Database.TableInfo;
//import com.contacts.utils.Database.UserEmail;
//import com.contacts.utils.Database.UserMobileNumber;
//import com.contacts.utils.Database.UserEmail;
//import com.contacts.utils.Database.UserMobileNumber;
//import com.contacts.utils.Database.Users;
//import com.contacts.utils.DatabaseImpl;
//import com.contacts.utils.JoinTypes;
//import com.contacts.utils.Operators;

// select*from User u join(select user_id,group_concat(id)id,group_concat(email)email from User_mail_ids group by user_id)as um on u.user_id=um.user_id join(select user_id,group_concat(id)id,group_concat(mobile_number)mobile_number from user_mobile_numbers group by user_id)as mob on u.user_id=mob.user_id where u.user_id=1

public class Test {
//	@SuppressWarnings("unchecked")
	public static void main(String[] args)
			throws IllegalAccessException, InvocationTargetException, InstantiationException, IllegalArgumentException,
			NoSuchMethodException, SecurityException, ClassNotFoundException, SQLException {
//		String password = "";
//		String email = "";
//		QueryBuilder qb = new QueryBuilder();
//		qb.selectTable(TableInfo.USER);
//		Table table2 = new Table(TableInfo.USEREMAIL);
//		qb.joinTables(JoinTypes.inner, new Column(Users.USERID, "", "", qb.table),
//				new Column(UserEmail.USERID, "", "", table2));
//		qb.setCondition(new Column(UserEmail.EMAIL, "", "", table2), Operators.EQUAL, email);
//		QueryExecutor qx = new QueryExecutor();
//		try {
//			ArrayList<User> user = (ArrayList<User>) qx.executeQuery(qb.build());
//			for (User u : user) {
//				UserDAO dao = new UserDAO();
//				if (dao.checkPassword(password, u.getPassword(), u.getIsHashed())) {
//					QueryBuilder mailQb = new QueryBuilder();
//					mailQb.selectTable(TableInfo.USEREMAIL);
//					mailQb.setCondition(new Column(UserEmail.USERID, "", "", mailQb.table), Operators.EQUAL,
//							u.getUserId());
//					ArrayList<Mail> mails = (ArrayList<Mail>) qx.executeQuery(mailQb.build());
//					for (Mail mail : mails) {
//						u.setEmail(mail);
//					}
//					QueryBuilder mobileQb = new QueryBuilder();
//					mobileQb.selectTable(TableInfo.USERMOBILENUMBER);
//					mobileQb.setCondition(new Column(UserMobileNumber.USERID, "", "", mobileQb.table), Operators.EQUAL,
//							u.getUserId());
//					ArrayList<MobileNumber> mobiles = (ArrayList<MobileNumber>) qx.executeQuery(mobileQb.build());
//					for (MobileNumber mobile : mobiles) {
//						u.setMobileNumber(mobile);
//					}
//					Method[] ms = u.getClass().getDeclaredMethods();
//					for (Method m : ms) {
//						if (m.getName().startsWith("get")) {
//							System.out.println(m.getName() + " -> " + m.invoke(u));
//						}
//					}
//				} else {
//					System.out.println("User login failed!");
//				}
//			}
//		} catch (ClassNotFoundException | SQLException e) {
//			e.printStackTrace();
//		}

//		QueryBuilder qb = new QueryBuilder();
//		QueryExecutor qx = new QueryExecutor();

//		select contact_id, first_name, middle_name, last_name from Contacts where user_id=? and contact_id not in (select contact_id from Group_info where group_id=?);

		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.selectTable(TableInfo.CONTACTS);
		qb.setCondition(new Column(Contacts.USERID, "", "", qb.table), Operators.EQUAL, 9);
		QueryBuilder inner_qb = new QueryBuilder();
		inner_qb.selectTable(TableInfo.GROUPINFO);
		inner_qb.selectColumn(new Column(GroupInfo.CONTACTID, "", "", inner_qb.table));
		inner_qb.setCondition(new Column(GroupInfo.GROUPID, "", "", inner_qb.table), Operators.EQUAL, 45);
		qb.setCondition(new Column(Contacts.CONTACTID, "", "", qb.table), Operators.NOTIN, inner_qb.build());
		ArrayList<Contact> c = (ArrayList<Contact>) qx.executeQuery(qb.build());
		System.out.println(c);
//		HashMap<String, Object> groupsData = qx.executeJoinQuery(qb.build());
//		System.out.println(groupsData);
//		ArrayList<Group> groups = (ArrayList<Group>) groupsData.get(groupInfoTable.name.toString());
//		groupsData.forEach((k, v) -> {
//			System.out.println(k + " -> " + v);
//		});
//		System.out.println();
//		ArrayList<Contact> contacts = (ArrayList<Contact>) groupsData.get(TableInfo.CONTACTS.toString());
		for (Contact g : c) {
//			System.out.println(g);
			Method[] ms = g.getClass().getDeclaredMethods();
			for (Method m : ms) {
				if (m.getName().startsWith("get")) {
					System.out.println(m.getName() + " -> " + m.invoke(g));
				}
			}
//			System.out.println();
		}
//		
//		qb.selectTable(TableInfo.USEREMAIL);
//		qb.selectColumn(new Column(UserEmail.ISPRIMARY, "", "", qb.table));
//		qb.setCondition(new Column(UserEmail.ID, "", "", qb.table), Operators.EQUAL, 1);
//		ArrayList<Mail> r = (ArrayList<Mail>) qx.executeQuery(qb.build());
////		Mail m = (Mail) r.get(qb.table.name.toString());
//		System.out.println(r);
//
////	 select c.contact_id, c.first_name, c.middle_name, c.last_name, ma.email, mo.mobile_number from Contacts c inner join contacts_mail_ids ma on c.contact_id=ma.contact_id inner join contacts_mobile_numbers mo on c.contact_id=mo.contact_id where c.user_id=?;
//		qb.selectTable(TableInfo.CONTACTS, "c");
//		Table c_mail = new Table(TableInfo.CONTACTMAIL, "ma");
//		Table c_mob = new Table(TableInfo.CONTACTMOBILENUMBER, "mo");
//		qb.selectColumn(new Column(Contacts.CONTACTID, "", "", qb.table));
//		qb.selectColumn(new Column(Contacts.FIRSTNAME, "", "", qb.table));
//		qb.selectColumn(new Column(Contacts.MIDDLENAME, "", "", qb.table));
//		qb.selectColumn(new Column(Contacts.LASTNAME, "", "", qb.table));
//		qb.selectColumn(new Column(ContactMail.EMAIL, "", "", c_mail));
//		qb.selectColumn(new Column(ContactMobileNumber.MOBILENUMBER, "", "", c_mob));
//		qb.joinTables(JoinTypes.inner, new Column(Contacts.CONTACTID, "", "", qb.table), new Column(ContactMail.CONTACTID, "", "", c_mail));
//		qb.joinTables(JoinTypes.inner, new Column(Contacts.CONTACTID, "", "", qb.table), new Column(ContactMobileNumber.CONTACTID, "", "", c_mob));
//		qb.setCondition(new Column(Users.USERID, "", "", qb.table), Operators.EQUAL, true);
//
//		System.out.println(qb.build().toString());

//		UserDAO userdao = new UserDAO();

//		QueryBuilder qb = new QueryBuilder();
//		QueryExecutor qx = new QueryExecutor();
//		select * from User user join User_mail_ids mails on user.user_id=mails.user_id where mails.email=?;
//		qb.selectTable(TableInfo.USER);
//		Table emailTable = new Table(TableInfo.USEREMAIL);
//		Table mobileTable = new Table(TableInfo.USERMOBILENUMBER);
//		qb.joinTables(JoinTypes.inner, new Column(Users.USERID, "", "", qb.table),
//				new Column(UserEmail.USERID, "", "", emailTable));
//		qb.joinTables(JoinTypes.inner, new Column(Users.USERID, "", "", qb.table),
//				new Column(UserMobileNumber.USERID, "", "", mobileTable));
//		qb.setCondition(new Column(UserEmail.EMAIL, "", "", emailTable), Operators.EQUAL, email);
//		qb.setCondition(new Column(Users.USERID, "", "", qb.table), Operators.EQUAL, 1);
//		System.out.println(qb.build());
//		HashMap<String, Object> userWithMail = qx.executeJoinQuery(qb.build());
//		userWithMail.forEach((k, v) -> {
//			System.out.println(k + " -> " + v);
//		});
//		System.out.println(userWithMail);
//		User user = (User) userWithMail.get(qb.table.name.toString());
//		User user = new User();
//		ArrayList<Mail> mails = (ArrayList<Mail>) userWithMail.get(emailTable.name.toString());
//		for (Mail mail : mails)
//			user.setEmail(mail);

//		ArrayList<MobileNumber> mobiles = (ArrayList<MobileNumber>) userWithMail.get(mobileTable.name.toString());
//		for (MobileNumber mobile: mobiles)
//			user.setMobileNumber(mobile);

		// User u = userdao.login("abc@gmail.com", "1233546");
//		Method[] ms = user.getClass().getDeclaredMethods();
//		for (Method m : ms) {
//			if (m.getName().startsWith("get")) {
//				System.out.println(m.getName() + " -> " + m.invoke(user));
//			}
//		}

//		QueryBuilder qb = new QueryBuilder();
//		QueryExecutor qx = new QueryExecutor();

//		qb.selectTable(TableInfo.USER);
////		qb.selectColumn(new Column(UserEmail.EMAIL, "", "", new Table(TableInfo.USEREMAIL)));
//		qb.setCondition(new Column(Users.USERID, "", "", qb.table), Operators.EQUAL, 1);
//
//		try {
//			qb = qb.build();
//			System.out.println(qb.QueryString);
//			ArrayList<User> users = (ArrayList<User>) qx.executeQuery(qb);
//			for (User u: users) {
//				Method[] ms = u.getClass().getDeclaredMethods();
//				for (Method m : ms) {
//					if (m.getName().startsWith("get")) {
//						System.out.println(m.getName() + " -> " + m.invoke(u));
//					}
//				}
//			}
//			System.out.println(users);
//		} catch (ClassNotFoundException | SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//		public ArrayList<?> executeQueryNew(QueryBuilder query)
//				throws ClassNotFoundException, SQLException, IllegalAccessException, InvocationTargetException {
//			Connection con = getConnection();
//			PreparedStatement ps = con.prepareStatement(query.toString());
//			ResultSet rs = ps.executeQuery();
//			while (rs.next()) {
//				ResultSetMetaData metadata = rs.getMetaData();
//				for (int i = 1; i <= metadata.getColumnCount(); i++) {
//					Method[] ms = metadata.getClass().getDeclaredMethods();
//					for (Method m : ms) {
//						if (m.getName().startsWith("get")) {
//							if (m.canAccess(metadata)) {
//								if (m.getParameterCount() == 0) {
//									System.out.println(m.getName() + " -> " + m.invoke(metadata));
//								} else {
//									System.out.println(m.getName() + " -> " + m.invoke(metadata, i));
//								}
//							}
//						}
//					}
//					System.out.println("\n");
//				}
//			}
//			ArrayList<String> a = new ArrayList<String>();
//			return a;
//		}

		/* Insert Query */

//		qb.insertTable(TableInfo.USER);
//		qb.insertValuesToColumns(new Column(Users.USERNAME, "", "", qb.table), "sample");
//		qb.insertValuesToColumns(new Column(Users.PASSWORD, "", "", qb.table), "sample");
//		qb.insertValuesToColumns(new Column(Users.FIRSTNAME, "", "", qb.table), "sample");
//		qb.insertValuesToColumns(new Column(Users.MIDDLENAME, "", "", qb.table), "");
//		qb.insertValuesToColumns(new Column(Users.LASTNAME, "", "", qb.table), "");
//		qb.insertValuesToColumns(new Column(Users.DATEOFBIRTH, "", "", qb.table), "2004-01-01");
//		qb.insertValuesToColumns(new Column(Users.GENDER, "", "", qb.table), "Male");
//		qb.insertValuesToColumns(new Column(Users.HOMEADDRESS, "", "", qb.table), "sample");
//		qb.insertValuesToColumns(new Column(Users.WORKADDRESS, "", "", qb.table), "sample");
//		qb.insertValuesToColumns(new Column(Users.NOTES, "", "", qb.table), "sample");
//		qb.insertValuesToColumns(new Column(Users.ISHASHED, "", "", qb.table), 1);

//		qb.updateTable(TableInfo.SAMPLE);
//		qb.updateColumn(new Column(Sample.AGE, "", "", qb.table), 20);
//		qb.setCondition(new Column(Sample.NAME, "", "", qb.table), Operators.EQUAL, "abc");
//		
//		if (qx.executeAndUpdate(qb.build()) > 0) {
//			System.out.println("Row Updated Successfully");
//		}
//		else {
//			System.err.println("Error while updating row");
//		}

		/* Update Query */

//		qb.updateTable(TableInfo.USER);
//		qb.updateColumn(new Column(Users.PASSWORD, "", "", qb.table), "sample12122321");
//		qb.setCondition(new Column(Users.USERNAME, "", "", qb.table), Operators.EQUAL, "sample");

		/* Delete Query */

//		qb.deleteTable(TableInfo.USER);
//		qb.setCondition(new Column(Users.USERID, "" ,"" ,qb.table), Operators.EQUAL, 28);

//		QueryExecutor qx = new QueryExecutor();
//		try {
//			int result = qx.executeAndUpdate(qb.build());
//			if (result > 0) {
////				System.out.println("New Row Inserted Successfully!");
////				System.out.println("Row Updated Successfully!");
//				System.out.println("Row deleted Successfully!");
//			} else {
//				System.out.println("Something went wrong!");
//			}
//		} catch (ClassNotFoundException | SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
