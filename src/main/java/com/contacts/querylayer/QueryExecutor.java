package com.contacts.querylayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
			ArrayList<Contact> contacts = new ArrayList<Contact>();
			while (rs.next()) {
				Contact contact = new Contact();
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
				contacts.add(contact);
			}
			return contacts;
		}
		case TableInfo.CONTACTMAIL: {
			ArrayList<Mail> mails = new ArrayList<Mail>();
			while (rs.next()) {
				Mail mail = new Mail();
				mail.setId(rs.getInt(ContactMail.ID.toString()));
				mail.setMail(rs.getString(ContactMail.EMAIL.toString()));
				mails.add(mail);
			}
			return mails;
		}
		case TableInfo.CONTACTMOBILENUMBER: {
			ArrayList<MobileNumber> mobiles = new ArrayList<MobileNumber>();
			while (rs.next()) {
				MobileNumber mobile = new MobileNumber();
				mobile.setId(rs.getInt(ContactMobileNumber.ID.toString()));
				mobile.setMobileNumber(rs.getLong(ContactMobileNumber.MOBILENUMBER.toString()));
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
		ResultSet rs = ps.getGeneratedKeys();
		if (rs.next()) {			
			return rs.getInt(1);
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
