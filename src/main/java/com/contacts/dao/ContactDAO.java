package com.contacts.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.contacts.model.Contact;

public class ContactDAO {
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

	public boolean addContact(int user_id, Contact contact) throws ClassNotFoundException, SQLException {
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement(
				"insert into Contacts (user_id, first_name, middle_name, last_name, gender, date_of_birth, notes, home_address, work_address) values (?, ?, ?, ?, ?, ?, ?, ?, ?);",
				Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, user_id);
		ps.setString(2, contact.getFirstName());
		ps.setString(3, contact.getMiddleName());
		ps.setString(4, contact.getLastName());
		ps.setString(5, contact.getGender());
		ps.setString(6, contact.getDateOfBirth());
		ps.setString(7, contact.getNotes());
		ps.setString(8, contact.getHomeAddress());
		ps.setString(9, contact.getWorkAddress());
		int result = ps.executeUpdate();
		if (result > 0) {
			ResultSet r = ps.getGeneratedKeys();
			r.next();
			int contact_id = r.getInt(1);
			PreparedStatement mail_ps = con.prepareStatement("insert into contacts_mail_ids values (?, ?)");
			mail_ps.setInt(1, contact_id);
			mail_ps.setString(2, contact.getEmail());
			if (mail_ps.executeUpdate() > 0) {
				PreparedStatement mobile_ps = con.prepareStatement("insert into contacts_mobile_numbers values (?, ?)");
				mobile_ps.setInt(1, contact_id);
				mobile_ps.setLong(2, contact.getMobileNumber());
				return mobile_ps.executeUpdate() > 0;
			}
		}
		return false;
	}

	public ArrayList<Contact> getContacts(int user_id) throws ClassNotFoundException, SQLException {
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement(
				"select c.contact_id, c.first_name, c.middle_name, c.last_name, ma.email, mo.mobile_number from Contacts c inner join contacts_mail_ids ma on c.contact_id=ma.contact_id inner join contacts_mobile_numbers mo on c.contact_id=mo.contact_id where c.user_id=?;");
		ps.setInt(1, user_id);
		ArrayList<Contact> contacts = new ArrayList<>();
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Contact contact = new Contact();
			contact.setContact_id(rs.getInt(1));
			contact.setFirstName(rs.getString(2));
			contact.setMiddleName(rs.getString(3));
			contact.setLastName(rs.getString(4));
			contact.setEmail(rs.getString(5));
			contact.setMobileNumber(rs.getLong(6));
			contacts.add(contact);
		}
		return contacts;
	}

	public ResultSet getContactInfo(int contact_id) throws ClassNotFoundException, SQLException {
		Connection con = getConnection();
		// PreparedStatement ps = con.prepareStatement(
		// "select first_name, gender, notes, date_of_birth from Contacts where
		// contact_id=?;");
		PreparedStatement ps = con.prepareStatement(
				"select c.first_name, c.middle_name, c.last_name, c.gender, c.notes, c.date_of_birth, c.home_address, c.work_address, ma.email, mo.mobile_number from Contacts c inner join contacts_mail_ids ma on c.contact_id=ma.contact_id inner join contacts_mobile_numbers mo on c.contact_id=mo.contact_id where c.contact_id=?;");
		ps.setInt(1, contact_id);
		return ps.executeQuery();
	}

	public boolean deleteContact(int contact_id) throws ClassNotFoundException, SQLException {
		Connection con = getConnection();
		PreparedStatement mail_ps = con.prepareStatement("delete from contacts_mail_ids where contact_id=?;");
		mail_ps.setInt(1, contact_id);
		mail_ps.executeUpdate();
		PreparedStatement mobile_ps = con.prepareStatement("delete from contacts_mobile_numbers where contact_id=?;");
		mobile_ps.setInt(1, contact_id);
		mobile_ps.executeUpdate();
		deleteContactFromGroups(contact_id);
		PreparedStatement ps = con.prepareStatement("delete from Contacts where contact_id=?;");
		ps.setInt(1, contact_id);
		return ps.executeUpdate() > 0;
	}

	public String getGroupNameById(int group_id) throws SQLException, ClassNotFoundException {
		Connection con = getConnection();
		PreparedStatement mail_ps = con.prepareStatement("select group_name from Group_details where group_id=?;");
		mail_ps.setInt(1, group_id);
		ResultSet rs = mail_ps.executeQuery();
		if (rs.next()) {
			return rs.getString(1);
		}
		return "";
	}

	public ArrayList<Contact> getContactsByGroupId(int user_id, int group_id)
			throws ClassNotFoundException, SQLException {
		ArrayList<Contact> filteredContacts = new ArrayList<>();
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement("select * from Group_details where user_id=? and group_id=?;");
		ps.setInt(1, user_id);
		ps.setInt(2, group_id);
		ResultSet r = ps.executeQuery();
		if (r.next()) {

			PreparedStatement mail_ps = con.prepareStatement(
					"select contact_id, first_name, middle_name, last_name from Contacts where user_id=? and contact_id not in (select contact_id from Group_info where group_id=?);");
			mail_ps.setInt(1, user_id);
			mail_ps.setInt(2, group_id);
			ResultSet rs = mail_ps.executeQuery();
			while (rs.next()) {
				Contact contact = new Contact();
				contact.setContact_id(rs.getInt(1));
				contact.setFirstName(rs.getString(2));
				contact.setMiddleName(rs.getString(3));
				contact.setLastName(rs.getString(4));
				filteredContacts.add(contact);
			}
		}
		return filteredContacts;
	}

	public boolean deleteContactFromGroups(int contact_id) throws ClassNotFoundException, SQLException {
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement("delete from Group_info where contact_id=?;");
//        ps.setInt(1, group_id);
		ps.setInt(1, contact_id);
		return ps.executeUpdate() > 0;
	}

	public boolean deleteGroupContact(int group_id, int contact_id) throws ClassNotFoundException, SQLException {
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement("delete from Group_info where group_id=? and contact_id=?;");
		ps.setInt(1, group_id);
		ps.setInt(2, contact_id);
		return ps.executeUpdate() > 0;
	}

	public boolean deleteGroup(int group_id) throws ClassNotFoundException, SQLException {
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement("delete from Group_info where group_id=?;");
		ps.setInt(1, group_id);
		// if (ps.executeUpdate() >= 0) {
		ps.executeUpdate();
		PreparedStatement ps1 = con.prepareStatement("delete from Group_details where group_id=?;");
		ps1.setInt(1, group_id);
		return ps1.executeUpdate() > 0;
		// }
		// return false;
	}
}
