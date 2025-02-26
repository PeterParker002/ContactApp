package com.contacts.dao;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.contacts.model.Contact;
import com.contacts.model.Group;
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
import com.contacts.utils.JoinTypes;
import com.contacts.utils.Operators;
import com.contacts.utils.Order;

public class ContactDAO {

	public static boolean addContact(int user_id, Contact contact) throws ClassNotFoundException, SQLException {
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
		qb.insertValuesToColumns(new Column(Contacts.CREATEDAT, "", "", qb.table), contact.getCreatedAt());
		qb.insertValuesToColumns(new Column(Contacts.MODIFIEDAT, "", "", qb.table), contact.getModifiedAt());
		int result = qx.executeAndUpdateWithKeys(qb.build());
		if (result > 0 && contact.getEmail().size() > 0) {
			qb = new QueryBuilder();
			qb.insertTable(TableInfo.CONTACTMAIL);
			qb.insertValuesToColumns(new Column(ContactEmail.CONTACTID, "", "", qb.table), result);
			qb.insertValuesToColumns(new Column(ContactEmail.EMAIL, "", "", qb.table),
					contact.getEmail().get(0).getEmail());
			qb.insertValuesToColumns(new Column(ContactEmail.CREATEDAT, "", "", qb.table),
					contact.getEmail().get(0).getCreatedAt());
			qb.insertValuesToColumns(new Column(ContactEmail.MODIFIEDAT, "", "", qb.table),
					contact.getEmail().get(0).getModifiedAt());
			qx.executeAndUpdateWithKeys(qb.build());
		}
		if (result > 0 && contact.getMobileNumber().size() > 0) {
			qb = new QueryBuilder();
			qb.insertTable(TableInfo.CONTACTMOBILENUMBER);
			qb.insertValuesToColumns(new Column(ContactMobileNumber.CONTACTID, "", "", qb.table), result);
			qb.insertValuesToColumns(new Column(ContactMobileNumber.MOBILENUMBER, "", "", qb.table),
					contact.getMobileNumber().get(0).getMobileNumber());
			qb.insertValuesToColumns(new Column(ContactMobileNumber.CREATEDAT, "", "", qb.table),
					contact.getMobileNumber().get(0).getCreatedAt());
			qb.insertValuesToColumns(new Column(ContactMobileNumber.MODIFIEDAT, "", "", qb.table),
					contact.getMobileNumber().get(0).getModifiedAt());
			return qx.executeAndUpdateWithKeys(qb.build()) > 0;
		}
		return false;
	}

	public static boolean editContactInfo(int user_id, Contact contact) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.updateTable(TableInfo.CONTACTS);
		if (contact.getFirstName() != null) {
			qb.updateColumn(new Column(Contacts.FIRSTNAME, "", "", qb.table), contact.getFirstName());
		}
		if (contact.getMiddleName() != null) {
			qb.updateColumn(new Column(Contacts.MIDDLENAME, "", "", qb.table), contact.getMiddleName());
		}
		if (contact.getLastName() != null) {
			qb.updateColumn(new Column(Contacts.LASTNAME, "", "", qb.table), contact.getLastName());
		}
		if (contact.getGender() != null) {
			qb.updateColumn(new Column(Contacts.GENDER, "", "", qb.table), contact.getGender());
		}
		if (contact.getDateOfBirth() != null) {
			qb.updateColumn(new Column(Contacts.DATEOFBIRTH, "", "", qb.table), contact.getDateOfBirth());
		}
		if (contact.getNotes() != null) {
			qb.updateColumn(new Column(Contacts.NOTES, "", "", qb.table), contact.getNotes());
		}
		if (contact.getHomeAddress() != null) {
			qb.updateColumn(new Column(Contacts.HOMEADDRESS, "", "", qb.table), contact.getHomeAddress());
		}
		if (contact.getWorkAddress() != null) {
			qb.updateColumn(new Column(Contacts.WORKADDRESS, "", "", qb.table), contact.getWorkAddress());
		}
		qb.updateColumn(new Column(Contacts.MODIFIEDAT, "", "", qb.table), contact.getModifiedAt());
		qb.setCondition(new Column(Contacts.USERID, "", "", qb.table), Operators.EQUAL, user_id);
		qb.setCondition(new Column(Contacts.CONTACTID, "", "", qb.table), Operators.EQUAL, contact.getContactId());
		int res = qx.executeAndUpdate(qb.build());
		return res > 0;
	}

	public static boolean addEmails(int contact_id, String[] emails) throws ClassNotFoundException, SQLException {
		QueryExecutor qx = new QueryExecutor();
		long now = System.currentTimeMillis();
		for (String email : emails) {
			QueryBuilder qb = new QueryBuilder();
			qb.insertTable(TableInfo.CONTACTMAIL);
			qb.insertValuesToColumns(new Column(ContactEmail.CONTACTID, "", "", qb.table), contact_id);
			qb.insertValuesToColumns(new Column(ContactEmail.EMAIL, "", "", qb.table), email);
			qb.insertValuesToColumns(new Column(ContactEmail.CREATEDAT, "", "", qb.table), now);
			qb.insertValuesToColumns(new Column(ContactEmail.MODIFIEDAT, "", "", qb.table), now);
			if (qx.executeAndUpdateWithKeys(qb.build()) < 1) {
				return false;
			}
		}
		return true;
	}

	public static boolean deleteMail(int mail_id) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.deleteTable(TableInfo.CONTACTMAIL);
		qb.setCondition(new Column(ContactEmail.ID, "", "", qb.table), Operators.EQUAL, mail_id);
		return qx.executeAndUpdate(qb.build()) > 0;
	}

	public static boolean addMobileNumbers(int contact_id, String[] mobileNumbers)
			throws ClassNotFoundException, SQLException {
		QueryExecutor qx = new QueryExecutor();
		long now = System.currentTimeMillis();
		for (String number : mobileNumbers) {
			QueryBuilder qb = new QueryBuilder();
			qb.insertTable(TableInfo.CONTACTMOBILENUMBER);
			qb.insertValuesToColumns(new Column(ContactMobileNumber.CONTACTID, "", "", qb.table), contact_id);
			qb.insertValuesToColumns(new Column(ContactMobileNumber.MOBILENUMBER, "", "", qb.table), number);
			qb.insertValuesToColumns(new Column(ContactMobileNumber.CREATEDAT, "", "", qb.table), now);
			qb.insertValuesToColumns(new Column(ContactMobileNumber.MODIFIEDAT, "", "", qb.table), now);
			if (qx.executeAndUpdateWithKeys(qb.build()) < 1) {
				return false;
			}
		}
		return true;
	}

	public static boolean deleteMobileNumber(int mobile_id) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.deleteTable(TableInfo.CONTACTMOBILENUMBER);
		qb.setCondition(new Column(ContactMobileNumber.ID, "", "", qb.table), Operators.EQUAL, mobile_id);
		return qx.executeAndUpdate(qb.build()) > 0;
	}

	@SuppressWarnings("unchecked")
	public static List<Contact> getContacts(int user_id) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();

		qb.selectTable(TableInfo.CONTACTS);
		qb.joinTables(JoinTypes.left, new Column(Contacts.CONTACTID, "", "", qb.table),
				new Column(ContactEmail.CONTACTID, "", "", new Table(TableInfo.CONTACTMAIL)));
		qb.joinTables(JoinTypes.left, new Column(Contacts.CONTACTID, "", "", qb.table),
				new Column(ContactMobileNumber.CONTACTID, "", "", new Table(TableInfo.CONTACTMOBILENUMBER)));
		qb.setCondition(new Column(Contacts.USERID, "", "", qb.table), Operators.EQUAL, user_id);
		List<Contact> contacts = new ArrayList<>();
		try {
			contacts = (List<Contact>) qx.executeJoinQuery(qb.build());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return contacts;
	}

	@SuppressWarnings("unchecked")
	public static List<Contact> getContactsInfo(int user_id, int pageOffset) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();

		qb.selectTable(TableInfo.CONTACTS);
		qb.setCondition(new Column(Contacts.USERID, "", "", qb.table), Operators.EQUAL, user_id);
		qb.setOrder(new Column(Contacts.CONTACTID, "", "", qb.table), Order.asc);
		qb.setLimit(10, pageOffset * 10);
		List<Contact> contacts = new ArrayList<>();
		try {
			contacts = (List<Contact>) qx.executeQuery(qb.build());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return contacts;
	}

	@SuppressWarnings("unchecked")
	public static Contact getContactInfo(int contact_id, int user_id) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();

		qb.selectTable(TableInfo.CONTACTS);
		qb.joinTables(JoinTypes.left, new Column(Contacts.CONTACTID, "", "", qb.table),
				new Column(ContactEmail.CONTACTID, "", "", new Table(TableInfo.CONTACTMAIL)));
		qb.joinTables(JoinTypes.left, new Column(Contacts.CONTACTID, "", "", qb.table),
				new Column(ContactMobileNumber.CONTACTID, "", "", new Table(TableInfo.CONTACTMOBILENUMBER)));
		qb.setCondition(new Column(Contacts.CONTACTID, "", "", qb.table), Operators.EQUAL, contact_id);
		qb.setCondition(new Column(Contacts.USERID, "", "", qb.table), Operators.EQUAL, user_id);
		List<Contact> contacts = new ArrayList<>();
		try {
			contacts = (List<Contact>) qx.executeJoinQuery(qb.build());
			return contacts.get(0);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean deleteContact(int contact_id) throws ClassNotFoundException, SQLException {
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

	@SuppressWarnings("unchecked")
	public static String getGroupNameById(int group_id) {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.selectTable(TableInfo.GROUPDETAILS);
		qb.setCondition(new Column(GroupDetails.GROUPID, "", "", qb.table), Operators.EQUAL, group_id);
		List<Group> g;
		try {
			g = (List<Group>) qx.executeQuery(qb.build());
			if (g.size() > 0) {
				return g.get(0).getGroupName();
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	public static List<Contact> getContactsByGroupId(int user_id, int group_id) {
		ArrayList<Contact> filteredContacts = new ArrayList<>();
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.selectTable(TableInfo.GROUPDETAILS);
		qb.setCondition(new Column(GroupDetails.USERID, "", "", qb.table), Operators.EQUAL, user_id);
		List<Group> r;
		try {
			r = (List<Group>) qx.executeQuery(qb.build());
			if (r.size() > 0) {
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
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return filteredContacts;
	}

	public static boolean deleteContactFromGroups(int contact_id) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.deleteTable(TableInfo.GROUPINFO);
		qb.setCondition(new Column(GroupInfo.CONTACTID, "", "", qb.table), Operators.EQUAL, contact_id);
		return qx.executeAndUpdate(qb.build()) > 0;
	}

	public static boolean deleteGroupContact(int group_id, int contact_id) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.deleteTable(TableInfo.GROUPINFO);
		qb.setCondition(new Column(GroupInfo.GROUPID, "", "", qb.table), Operators.EQUAL, group_id);
		qb.setCondition(new Column(GroupInfo.CONTACTID, "", "", qb.table), Operators.EQUAL, contact_id);
		return qx.executeAndUpdate(qb.build()) > 0;
	}

	public static boolean deleteGroup(int group_id) throws ClassNotFoundException, SQLException {
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
