package com.contacts.querylayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.contacts.model.User;
import com.contacts.model.Mail;
import com.contacts.model.MobileNumber;
import com.contacts.utils.Database.TableInfo;
import com.contacts.utils.Database.UserEmail;
import com.contacts.utils.Database.UserMobileNumber;
import com.contacts.utils.Database.Users;

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

	public ArrayList<?> executeQuery(QueryBuilder query) throws SQLException, ClassNotFoundException {
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement(query.toString());
		ResultSet rs = ps.executeQuery();
		ArrayList<String> res = new ArrayList<String>();
		switch (query.table.getName()) {
		case TableInfo.USER: {
			ArrayList<User> users = new ArrayList<User>();
			while (rs.next()) {
				User user = new User();
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
			}
			return users;
		}
		case TableInfo.USEREMAIL: {
			ArrayList<Mail> mails = new ArrayList<Mail>();
			while (rs.next()) {
				Mail mail = new Mail();
				mail.setId(rs.getInt(UserEmail.ID.toString()));
				mail.setMail(rs.getString(UserEmail.EMAIL.toString()));
				mail.setPrimary(rs.getBoolean(UserEmail.ISPRIMARY.toString()));
				mails.add(mail);
			}
			return mails;
		}
		case TableInfo.USERMOBILENUMBER: {
			ArrayList<MobileNumber> mobiles = new ArrayList<MobileNumber>();
			while (rs.next()) {
				MobileNumber mobile = new MobileNumber();
				mobile.setId(rs.getInt(UserMobileNumber.ID.toString()));
				mobile.setMobileNumber(rs.getLong(UserMobileNumber.MOBILENUMBER.toString()));
				mobiles.add(mobile);
			}
			return mobiles;
		}
		case TableInfo.CONTACTS: {
			break;
		}
		case TableInfo.CONTACTMAIL: {
			break;
		}
		case TableInfo.CONTACTMOBILENUMBER: {
			break;
		}
		case TableInfo.GROUPINFO: {
			break;
		}
		case TableInfo.GROUPDETAILS: {
			break;
		}
		default:
			break;
		}
		closeConnection(con);
		return res;
	}

	private void closeConnection(Connection con) throws SQLException {
		con.close();
	}
}
