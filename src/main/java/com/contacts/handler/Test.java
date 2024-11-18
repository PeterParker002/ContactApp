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
import com.contacts.utils.Database.TableInfo;
import com.contacts.utils.Database.UserEmail;
import com.contacts.utils.Database.UserMobileNumber;
//import com.contacts.utils.Database.UserEmail;
//import com.contacts.utils.Database.UserMobileNumber;
import com.contacts.utils.Database.Users;
import com.contacts.utils.JoinTypes;
import com.contacts.utils.Operators;

/*
 * Connection c = getConnection();
		PreparedStatement ps = c.prepareStatement(
				"select * from User user join User_mail_ids mails on user.user_id=mails.user_id where mails.email=?;");
		ps.setString(1, email);
		ResultSet res = ps.executeQuery();
		if (res.next()) {
			int user_id = res.getInt("user_id");
			if (currentHashing != res.getInt("isHashed")) {
				if (checkPassword(password, res.getString("password"), res.getInt("isHashed"))) {
					String newHashedassword = hashPassword(password, currentHashing);
					PreparedStatement change_ps = c
							.prepareStatement("update User set password=?, isHashed=? where user_id=?;");
					change_ps.setString(1, newHashedassword);
					change_ps.setInt(2, currentHashing);
					change_ps.setInt(3, user_id);
					change_ps.executeUpdate();
					return user_id;
				} else {
					return -1;
				}
			} else {
				if (checkPassword(password, res.getString("password"), res.getInt("isHashed"))) {
					return user_id;
				} else {
					return -1;
				}
			}
		} else {
			return -1;
		}
 * */
public class Test {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IllegalAccessException, InvocationTargetException {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter your email: ");
		String email = sc.nextLine();
		System.out.println("Enter your password: ");
		String password = sc.nextLine();
		QueryBuilder qb = new QueryBuilder();
		qb.selectTable(TableInfo.USER);
		Table table2 = new Table(TableInfo.USEREMAIL);
		qb.joinTables(JoinTypes.inner, new Column(Users.USERID, "", "", qb.table),
				new Column(UserEmail.USERID, "", "", table2));
		qb.setCondition(new Column(UserEmail.EMAIL, "", "", table2), Operators.EQUAL, email);
		QueryExecutor qx = new QueryExecutor();
		try {
			ArrayList<User> user = (ArrayList<User>) qx.executeQuery(qb.build());
			for (User u : user) {
				UserDAO dao = new UserDAO();
				if (dao.checkPassword(password, u.getPassword(), u.getIsHashed())) {
					QueryBuilder mailQb = new QueryBuilder();
					mailQb.selectTable(TableInfo.USEREMAIL);
					mailQb.setCondition(new Column(UserEmail.USERID, "", "", mailQb.table), Operators.EQUAL,
							u.getUserId());
					ArrayList<Mail> mails = (ArrayList<Mail>) qx.executeQuery(mailQb.build());
					for (Mail mail : mails) {
						u.setEmail(mail);
					}
					QueryBuilder mobileQb = new QueryBuilder();
					mobileQb.selectTable(TableInfo.USERMOBILENUMBER);
					mobileQb.setCondition(new Column(UserMobileNumber.USERID, "", "", mobileQb.table), Operators.EQUAL,
							u.getUserId());
					ArrayList<MobileNumber> mobiles = (ArrayList<MobileNumber>) qx.executeQuery(mobileQb.build());
					for (MobileNumber mobile : mobiles) {
						u.setMobileNumber(mobile);
					}
					Method[] ms = u.getClass().getDeclaredMethods();
					for (Method m : ms) {
						if (m.getName().startsWith("get")) {
							System.out.println(m.getName() + " -> " + m.invoke(u));
						}
					}
				}
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
}
