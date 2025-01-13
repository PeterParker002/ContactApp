package com.contacts.dao;

import java.lang.reflect.InvocationTargetException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Statement;
import java.util.ArrayList;

import com.contacts.model.Contact;
//import com.contacts.model.ContactMobile;
import com.contacts.model.Group;
//import com.contacts.model.User;
import com.contacts.querylayer.Column;
import com.contacts.querylayer.QueryBuilder;
import com.contacts.querylayer.QueryExecutor;
import com.contacts.querylayer.Table;
import com.contacts.utils.Database.ContactEmail;
import com.contacts.utils.Database.ContactMobileNumber;
import com.contacts.utils.Database.Contacts;
import com.contacts.utils.Database.GroupDetails;
import com.contacts.utils.Database.GroupInfo;
import com.contacts.utils.Database.TableInfo;
//import com.contacts.utils.Database.UserEmail;
//import com.contacts.utils.Database.UserMobileNumber;
//import com.contacts.utils.Database.Users;
import com.contacts.utils.JoinTypes;
import com.contacts.utils.Operators;

public class ContactDAO {
//	private String username = "root";
//	private String password = "root";
//	private String db_name = "ContactsApp";
//
//	
//	private Connection getConnection() throws ClassNotFoundException, SQLException {
//		Connection con = null;
//		try {
//			Class.forName("com.mysql.cj.jdbc.Driver");
//			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + db_name, username, password);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return con;
//	}

	public boolean addContact(int user_id, Contact contact) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.insertTable(TableInfo.CONTACTS);
		qb.insertValuesToColumns(new Column(Contacts.USERID, "", "", qb.table), user_id);
		qb.insertValuesToColumns(new Column(Contacts.FIRSTNAME, "", "", qb.table), contact.getFirstName());
		qb.insertValuesToColumns(new Column(Contacts.MIDDLENAME, "", "", qb.table), contact.getMiddleName());
		qb.insertValuesToColumns(new Column(Contacts.LASTNAME, "", "", qb.table), contact.getLastName());
		qb.insertValuesToColumns(new Column(Contacts.GENDER, "", "", qb.table), contact.getGender());
		qb.insertValuesToColumns(new Column(Contacts.DATEOFBIRTH, "", "", qb.table), contact.getDateOfBirth());
		qb.insertValuesToColumns(new Column(Contacts.NOTES, "", "", qb.table), contact.getNotes());
		qb.insertValuesToColumns(new Column(Contacts.HOMEADDRESS, "", "", qb.table), contact.getHomeAddress());
		qb.insertValuesToColumns(new Column(Contacts.WORKADDRESS, "", "", qb.table), contact.getWorkAddress());
		int result = qx.executeAndUpdateWithKeys(qb.build());
		if (result > 0) {
			qb = new QueryBuilder();
			qb.insertTable(TableInfo.CONTACTMAIL);
			qb.insertValuesToColumns(new Column(ContactEmail.CONTACTID, "", "", qb.table), result);
			qb.insertValuesToColumns(new Column(ContactEmail.EMAIL, "", "", qb.table),
					contact.getEmail().get(0).getEmail());
			if (qx.executeAndUpdate(qb.build()) > 0) {
				qb = new QueryBuilder();
				qb.insertTable(TableInfo.CONTACTMOBILENUMBER);
				qb.insertValuesToColumns(new Column(ContactMobileNumber.CONTACTID, "", "", qb.table), result);
				qb.insertValuesToColumns(new Column(ContactMobileNumber.MOBILENUMBER, "", "", qb.table),
						contact.getMobileNumber().get(0).getMobileNumber());
				return qx.executeAndUpdate(qb.build()) > 0;
			}
		}
		return false;
	}

	public boolean editContactInfo(int user_id, Contact contact) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.updateTable(TableInfo.CONTACTS);
		qb.updateColumn(new Column(Contacts.FIRSTNAME, "", "", qb.table), contact.getFirstName());
		qb.updateColumn(new Column(Contacts.MIDDLENAME, "", "", qb.table), contact.getMiddleName());
		qb.updateColumn(new Column(Contacts.LASTNAME, "", "", qb.table), contact.getLastName());
		qb.updateColumn(new Column(Contacts.GENDER, "", "", qb.table), contact.getGender());
		qb.updateColumn(new Column(Contacts.DATEOFBIRTH, "", "", qb.table), contact.getDateOfBirth());
		qb.updateColumn(new Column(Contacts.NOTES, "", "", qb.table), contact.getNotes());
		qb.updateColumn(new Column(Contacts.HOMEADDRESS, "", "", qb.table), contact.getHomeAddress());
		qb.updateColumn(new Column(Contacts.WORKADDRESS, "", "", qb.table), contact.getWorkAddress());
		qb.setCondition(new Column(Contacts.USERID, "", "", qb.table), Operators.EQUAL, user_id);
		qb.setCondition(new Column(Contacts.CONTACTID, "", "", qb.table), Operators.EQUAL, contact.getContactId());
		int res = qx.executeAndUpdate(qb.build());
		return res > 0;
	}

	public boolean addEmails(int contact_id, String[] emails) throws ClassNotFoundException, SQLException {
		QueryExecutor qx = new QueryExecutor();
		for (String email : emails) {
			QueryBuilder qb = new QueryBuilder();
			qb.insertTable(TableInfo.CONTACTMAIL);
			qb.insertValuesToColumns(new Column(ContactEmail.CONTACTID, "", "", qb.table), contact_id);
			qb.insertValuesToColumns(new Column(ContactEmail.EMAIL, "", "", qb.table), email);
			if (qx.executeAndUpdate(qb.build()) < 1) {
				return false;
			}
		}
		return true;
	}

	public boolean deleteMail(int mail_id) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.deleteTable(TableInfo.CONTACTMAIL);
		qb.setCondition(new Column(ContactEmail.ID, "", "", qb.table), Operators.EQUAL, mail_id);
		return qx.executeAndUpdate(qb.build()) > 0;
	}

	public boolean addMobileNumbers(int contact_id, Long[] mobileNumbers) throws ClassNotFoundException, SQLException {
		QueryExecutor qx = new QueryExecutor();
		for (Long number : mobileNumbers) {
			QueryBuilder qb = new QueryBuilder();
			qb.insertTable(TableInfo.CONTACTMOBILENUMBER);
			qb.insertValuesToColumns(new Column(ContactMobileNumber.CONTACTID, "", "", qb.table), contact_id);
			qb.insertValuesToColumns(new Column(ContactMobileNumber.MOBILENUMBER, "", "", qb.table), number);
			if (qx.executeAndUpdate(qb.build()) < 1) {
				return false;
			}
		}
		return true;
	}

	public boolean deleteMobileNumber(int mobile_id) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.deleteTable(TableInfo.CONTACTMOBILENUMBER);
		qb.setCondition(new Column(ContactMobileNumber.ID, "", "", qb.table), Operators.EQUAL, mobile_id);
		return qx.executeAndUpdate(qb.build()) > 0;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Contact> getContacts(int user_id) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();

		qb.selectTable(TableInfo.CONTACTS);
		qb.joinTables(JoinTypes.inner, new Column(Contacts.CONTACTID, "", "", qb.table),
				new Column(ContactEmail.CONTACTID, "", "", new Table(TableInfo.CONTACTMAIL)));
		qb.joinTables(JoinTypes.inner, new Column(Contacts.CONTACTID, "", "", qb.table),
				new Column(ContactMobileNumber.CONTACTID, "", "", new Table(TableInfo.CONTACTMOBILENUMBER)));
		qb.setCondition(new Column(Contacts.USERID, "", "", qb.table), Operators.EQUAL, user_id);
		ArrayList<Contact> contacts = new ArrayList<>();
		try {
			contacts = (ArrayList<Contact>) qx.executeJoinQuery1(qb.build());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return contacts;
	}

	public Contact getContactInfo(int contact_id, int user_id) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();

		qb.selectTable(TableInfo.CONTACTS);
		qb.joinTables(JoinTypes.inner, new Column(Contacts.CONTACTID, "", "", qb.table),
				new Column(ContactEmail.CONTACTID, "", "", new Table(TableInfo.CONTACTMAIL)));
		qb.joinTables(JoinTypes.inner, new Column(Contacts.CONTACTID, "", "", qb.table),
				new Column(ContactMobileNumber.CONTACTID, "", "", new Table(TableInfo.CONTACTMOBILENUMBER)));
		qb.setCondition(new Column(Contacts.CONTACTID, "", "", qb.table), Operators.EQUAL, contact_id);
		qb.setCondition(new Column(Contacts.USERID, "", "", qb.table), Operators.EQUAL, user_id);
		ArrayList<Contact> contacts = new ArrayList<>();
		try {
			contacts = (ArrayList<Contact>) qx.executeJoinQuery1(qb.build());
			return contacts.get(0);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean deleteContact(int contact_id) throws ClassNotFoundException, SQLException {
		QueryExecutor qx = new QueryExecutor();
		QueryBuilder qb = new QueryBuilder();
		qb.deleteTable(TableInfo.CONTACTMAIL);
		qb.setCondition(new Column(ContactEmail.CONTACTID, "", "", qb.table), Operators.EQUAL, contact_id);
		qx.executeAndUpdate(qb.build());
		qb = new QueryBuilder();
		qb.deleteTable(TableInfo.CONTACTMOBILENUMBER);
		qb.setCondition(new Column(ContactMobileNumber.CONTACTID, "", "", qb.table), Operators.EQUAL, contact_id);
		qx.executeAndUpdate(qb.build());
		qb = new QueryBuilder();
		qb.deleteTable(TableInfo.GROUPINFO);
		qb.setCondition(new Column(GroupInfo.CONTACTID, "", "", qb.table), Operators.EQUAL, contact_id);
		qx.executeAndUpdate(qb.build());
		qb = new QueryBuilder();
		qb.deleteTable(TableInfo.CONTACTS);
		qb.setCondition(new Column(Contacts.CONTACTID, "", "", qb.table), Operators.EQUAL, contact_id);
		return qx.executeAndUpdate(qb.build()) > 0;
	}

	public String getGroupNameById(int group_id) {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.selectTable(TableInfo.GROUPDETAILS);
		qb.setCondition(new Column(GroupDetails.GROUPID, "", "", qb.table), Operators.EQUAL, group_id);
		ArrayList<Group> g;
		try {
			g = (ArrayList<Group>) qx.executeQuery(qb.build());
			if (g.size() > 0) {
				return g.get(0).getGroupName();
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return "";
	}

	public ArrayList<Contact> getContactsByGroupId(int user_id, int group_id) {
		ArrayList<Contact> filteredContacts = new ArrayList<>();
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.selectTable(TableInfo.GROUPDETAILS);
		qb.setCondition(new Column(GroupDetails.USERID, "", "", qb.table), Operators.EQUAL, user_id);
		ArrayList<Group> r;
		try {
			r = (ArrayList<Group>) qx.executeQuery(qb.build());
			if (r.size() > 0) {
				// Alternative Query -> SELECT c.contact_id, c.first_name, c.middle_name,
				// c.last_name FROM Contacts c LEFT JOIN Group_info g ON c.contact_id =
				// g.contact_id AND g.group_id = ? WHERE c.user_id = ? AND g.contact_id IS NULL;
				qb = new QueryBuilder();
				qx = new QueryExecutor();
				qb.selectTable(TableInfo.CONTACTS);
				qb.setCondition(new Column(Contacts.USERID, "", "", qb.table), Operators.EQUAL, user_id);
				QueryBuilder inner_qb = new QueryBuilder();
				inner_qb.selectTable(TableInfo.GROUPINFO);
				inner_qb.selectColumn(new Column(GroupInfo.CONTACTID, "", "", inner_qb.table));
				inner_qb.setCondition(new Column(GroupInfo.GROUPID, "", "", inner_qb.table), Operators.EQUAL, group_id);
				qb.setCondition(new Column(Contacts.CONTACTID, "", "", qb.table), Operators.NOTIN, inner_qb.build());
				filteredContacts = (ArrayList<Contact>) qx.executeQuery(qb.build());
//			}
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return filteredContacts;
	}

	public boolean deleteContactFromGroups(int contact_id) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.deleteTable(TableInfo.GROUPINFO);
		qb.setCondition(new Column(GroupInfo.CONTACTID, "", "", qb.table), Operators.EQUAL, contact_id);
		return qx.executeAndUpdate(qb.build()) > 0;
	}

	public boolean deleteGroupContact(int group_id, int contact_id) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.deleteTable(TableInfo.GROUPINFO);
		qb.setCondition(new Column(GroupInfo.GROUPID, "", "", qb.table), Operators.EQUAL, group_id);
		qb.setCondition(new Column(GroupInfo.CONTACTID, "", "", qb.table), Operators.EQUAL, contact_id);
		return qx.executeAndUpdate(qb.build()) > 0;
	}

	public boolean deleteGroup(int group_id) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.deleteTable(TableInfo.GROUPINFO);
		qb.setCondition(new Column(GroupInfo.GROUPID, "", "", qb.table), Operators.EQUAL, group_id);
		qx.executeAndUpdate(qb.build());
		qb = new QueryBuilder();
		qb.deleteTable(TableInfo.GROUPDETAILS);
		qb.setCondition(new Column(GroupDetails.GROUPID, "", "", qb.table), Operators.EQUAL, group_id);
		return qx.executeAndUpdate(qb.build()) > 0;
	}
}
