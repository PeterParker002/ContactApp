package com.contacts.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import com.contacts.dao.UserDAO;
import com.contacts.model.Mail;
import com.contacts.model.MobileNumber;
//import com.contacts.model.Mail;
import com.contacts.model.User;
import com.contacts.querylayer.Column;
import com.contacts.querylayer.QueryBuilder;
import com.contacts.querylayer.QueryExecutor;
import com.contacts.querylayer.Table;
import com.contacts.utils.Database.Sample;
import com.contacts.utils.Database.TableInfo;
import com.contacts.utils.Database.UserEmail;
import com.contacts.utils.Database.UserMobileNumber;
//import com.contacts.utils.Database.UserEmail;
//import com.contacts.utils.Database.UserMobileNumber;
import com.contacts.utils.Database.Users;
import com.contacts.utils.DatabaseImpl;
import com.contacts.utils.JoinTypes;
import com.contacts.utils.Operators;

// select*from User u join(select user_id,group_concat(id)id,group_concat(email)email from User_mail_ids group by user_id)as um on u.user_id=um.user_id join(select user_id,group_concat(id)id,group_concat(mobile_number)mobile_number from user_mobile_numbers group by user_id)as mob on u.user_id=mob.user_id where u.user_id=1

public class Test {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IllegalAccessException, InvocationTargetException {
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

		QueryBuilder qb = new QueryBuilder();

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
		
		/* Update Query */
		
//		qb.updateTable(TableInfo.USER);
//		qb.updateColumn(new Column(Users.PASSWORD, "", "", qb.table), "sample12122321");
//		qb.setCondition(new Column(Users.USERNAME, "", "", qb.table), Operators.EQUAL, "sample");
		
		/* Delete Query */
		
//		qb.deleteTable(TableInfo.USER);
//		qb.setCondition(new Column(Users.USERID, "" ,"" ,qb.table), Operators.EQUAL, 28);
		
		QueryExecutor qx = new QueryExecutor();
		try {
			int result = qx.executeAndUpdate(qb.build());
			if (result > 0) {
//				System.out.println("New Row Inserted Successfully!");
//				System.out.println("Row Updated Successfully!");
				System.out.println("Row deleted Successfully!");
			} else {
				System.out.println("Something went wrong!");
			}
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
