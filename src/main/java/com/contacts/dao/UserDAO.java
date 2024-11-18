package com.contacts.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.mindrot.jbcrypt.BCrypt;
import com.contacts.model.Contact;
import com.contacts.model.Group;
import com.contacts.model.Mail;
import com.contacts.model.MobileNumber;
import com.contacts.model.User;
import com.lambdaworks.crypto.SCryptUtil;

public class UserDAO {
	private String username = "root";
	private String password = "root";
	private String db_name = "ContactsApp";
	// 1 -> Bcrypt, 2 -> Scrypt
	private int currentHashing = 2;
	public static String[] Columns = { "Username", "First Name", "Middle Name", "Last Name", "Gender", "Date Of Birth",
			"Notes", "Home Address", "Work Address" };

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

	public int SignupUser(User user) throws SQLException, ClassNotFoundException {
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement(
				"insert into User (username, password, first_name, middle_name, last_name, gender, date_of_birth, notes, home_address, work_address, isHashed) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
				Statement.RETURN_GENERATED_KEYS);
		ps.setString(1, user.getUsername());
		ps.setString(2, hashPassword(user.getPassword(), currentHashing));
		ps.setString(3, user.getFirstName());
		ps.setString(4, user.getMiddleName());
		ps.setString(5, user.getLastName());
		ps.setString(6, user.getGender());
		ps.setString(7, user.getDateOfBirth());
		ps.setString(8, user.getNotes());
		ps.setString(9, user.getHomeAddress());
		ps.setString(10, user.getWorkAddress());
		ps.setInt(11, currentHashing);
		int result = ps.executeUpdate();

		if (result > 0) {
			ResultSet rs = ps.getGeneratedKeys();
			rs.next();
			int user_id = rs.getInt(1);
			PreparedStatement mail_ps = con
					.prepareStatement("insert into User_mail_ids (user_id, email, isPrimary) values (?, ?, ?);");
			mail_ps.setInt(1, user_id);
			mail_ps.setString(2, user.getEmail().get(0).getMail());
			mail_ps.setBoolean(3, true);
			if (mail_ps.executeUpdate() > 0) {
				PreparedStatement mobile_ps = con
						.prepareStatement("insert into user_mobile_numbers (user_id, mobile_number) values (?, ?);");
				mobile_ps.setInt(1, user_id);
				mobile_ps.setLong(2, user.getMobileNumber().get(0).getMobileNumber());
				return mobile_ps.executeUpdate() > 0 ? user_id : -1;
			}
		}
		return -1;
	}

	public int LoginUser(String email, String password) throws ClassNotFoundException, SQLException {
		Connection c = getConnection();
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
	}

	public boolean addEmails(int user_id, String[] emails) throws ClassNotFoundException, SQLException {
		Connection con = getConnection();
		PreparedStatement ps = con
				.prepareStatement("insert into User_mail_ids (user_id, email, isPrimary) values (?, ?, ?);");
		for (String email : emails) {
			ps.setInt(1, user_id);
			ps.setString(2, email);
			ps.setBoolean(3, false);
			ps.addBatch();
		}
		int[] res = ps.executeBatch();
		for (int i = 0; i < res.length; i++) {
			if (res[i] < 1)
				return false;
		}
		return true;
	}

	public boolean changePrimaryMail(int user_id, int mail_id) throws ClassNotFoundException, SQLException {
		Connection con = getConnection();
		PreparedStatement ps = con
				.prepareStatement("update User_mail_ids set isPrimary=false where isPrimary=true and user_id=?;");
		ps.setInt(1, user_id);
		if (ps.executeUpdate() > 0) {
			PreparedStatement ps1 = con
					.prepareStatement("update User_mail_ids set isPrimary=true where id=? and user_id=?;");
			ps1.setInt(1, mail_id);
			ps1.setInt(2, user_id);
			return ps1.executeUpdate() > 0;
		}
		return false;
	}

	public boolean checkIsPrimaryMail(int mail_id) throws ClassNotFoundException, SQLException {
		Connection con = getConnection();
		PreparedStatement ps1 = con.prepareStatement("select isPrimary from User_mail_ids where id=?;");
		ps1.setInt(1, mail_id);
		ResultSet rs = ps1.executeQuery();
		rs.next();
		return rs.getBoolean(1);
	}

	public boolean deleteMail(int mail_id) throws ClassNotFoundException, SQLException {
		Connection con = getConnection();
		PreparedStatement ps1 = con.prepareStatement("delete from User_mail_ids where id=?;");
		ps1.setInt(1, mail_id);
		return ps1.executeUpdate() > 0;
	}

	public boolean addMobileNumbers(int user_id, Long[] mobileNumbers) throws ClassNotFoundException, SQLException {
		Connection con = getConnection();
		PreparedStatement ps = con
				.prepareStatement("insert into user_mobile_numbers (user_id, mobile_number) values (?, ?);");
		for (Long number : mobileNumbers) {
			ps.setInt(1, user_id);
			ps.setLong(2, number);
			ps.addBatch();
		}
		int[] res = ps.executeBatch();
		for (int i = 0; i < res.length; i++) {
			if (res[i] < 1)
				return false;
		}
		return true;
	}

	public boolean deleteMobileNumber(int mobile_id) throws ClassNotFoundException, SQLException {
		Connection con = getConnection();
		PreparedStatement ps1 = con.prepareStatement("delete from user_mobile_numbers where id=?;");
		ps1.setInt(1, mobile_id);
		return ps1.executeUpdate() > 0;
	}

	public String getUsername(int id) throws SQLException, ClassNotFoundException {
		Connection c = getConnection();
		PreparedStatement ps = c.prepareStatement("select username from User where user_id=?;");
		ps.setInt(1, id);
		ResultSet res = ps.executeQuery();
		if (res.next()) {
			return res.getString(1);
		} else {
			return "";
		}
	}

	public ResultSet getAllUsers() throws ClassNotFoundException, SQLException {
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement(
				"select * from User user inner join User_mail_ids mail on user.user_id=mail.user_id inner join user_mobile_numbers mobile on user.user_id=mobile.user_id;");
		ResultSet r = ps.executeQuery();
		return r;
	}

	public ResultSet getUserInfo(int user_id) throws ClassNotFoundException, SQLException {
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement(
				"select username, first_name, middle_name, last_name, gender, date_of_birth, notes, home_address, work_address  from User where user_id=?;");
		ps.setInt(1, user_id);
		ResultSet r = ps.executeQuery();
		return r;
	}

	public User customGetUserInfo(int user_id) throws ClassNotFoundException, SQLException {
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement(
				"select username, first_name, middle_name, last_name, gender, date_of_birth, notes, home_address, work_address from User where user_id=?;");
		ps.setInt(1, user_id);
		ResultSet r = ps.executeQuery();
		r.next();
		User user = new User();
		user.setUsername(r.getString("username"));
		user.setFirstName(r.getString("first_name"));
		user.setMiddleName(r.getString("middle_name"));
		user.setLastName(r.getString("last_name"));
		user.setGender(r.getString("gender"));
		user.setDateOfBirth(r.getString("date_of_birth"));
		user.setNotes(r.getString("notes"));
		user.setHomeAddress(r.getString("home_address"));
		user.setWorkAddress(r.getString("work_address"));
		ResultSet mail_rs = getUserMail(user_id);
		while (mail_rs.next()) {
			Mail mail = new Mail();
			mail.setId(mail_rs.getInt(1));
			mail.setMail(mail_rs.getString(2));
			mail.setPrimary(mail_rs.getBoolean(3));
			user.setEmail(mail);
		}
		ResultSet mobile_rs = getUserMobileNumber(user_id);
		while (mobile_rs.next()) {
			MobileNumber mobile = new MobileNumber();
			mobile.setId(mobile_rs.getInt(1));
			mobile.setMobileNumber(mobile_rs.getLong(2));
			user.setMobileNumber(mobile);
		}
		return user;
	}

	public ResultSet getUserMail(int user_id) throws ClassNotFoundException, SQLException {
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement("select id, email, isPrimary  from User_mail_ids where user_id=?;");
		ps.setInt(1, user_id);
		ResultSet r = ps.executeQuery();
		return r;
	}

	public ResultSet getUserMobileNumber(int user_id) throws ClassNotFoundException, SQLException {
		Connection con = getConnection();
		PreparedStatement ps = con
				.prepareStatement("select id, mobile_number  from user_mobile_numbers where user_id=?;");
		ps.setInt(1, user_id);
		ResultSet r = ps.executeQuery();
		return r;
	}

	public boolean EditUserInfo(int user_id, User user) throws ClassNotFoundException, SQLException {
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement(
				"update User set first_name=?, middle_name=?, last_name=?, gender=?, date_of_birth=?, notes=?, work_address=?, home_address=? where user_id=?;");
		ps.setString(1, user.getFirstName());
		ps.setString(2, user.getMiddleName());
		ps.setString(3, user.getLastName());
		ps.setString(4, user.getGender());
		ps.setString(5, user.getDateOfBirth());
		ps.setString(6, user.getNotes());
		ps.setString(7, user.getWorkAddress());
		ps.setString(8, user.getHomeAddress());
		ps.setInt(9, user_id);
		int res = ps.executeUpdate();
		return res > 0;
	}

	public ArrayList<Group> getGroups(int user_id) throws SQLException, ClassNotFoundException {
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement("select * from Group_details where user_id=?;");
		ps.setInt(1, user_id);
		ResultSet rs = ps.executeQuery();
		ArrayList<Group> groups = new ArrayList<>();
		while (rs.next()) {
			Group group = new Group();
			group.setGroupId(rs.getInt("group_id"));
			group.setContact(getContactsByGroup(rs.getInt("group_id")));
			group.setGroupName(rs.getString("group_name"));
			groups.add(group);
		}
		return groups;
	}

	public ArrayList<Contact> getContactsByGroup(int group_id) throws SQLException, ClassNotFoundException {
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement(
				"select g.contact_id, c.first_name, c.middle_name, c.last_name from Group_info g join Contacts c on g.contact_id=c.contact_id where group_id=?;");
		ps.setInt(1, group_id);
		ResultSet rs = ps.executeQuery();
		ArrayList<Contact> contacts = new ArrayList<>();
		while (rs.next()) {
			Contact c = new Contact();
			c.setContact_id(rs.getInt(1));
			c.setFirstName(rs.getString(2));
			c.setMiddleName(rs.getString(3));
			c.setLastName(rs.getString(4));
			contacts.add(c);
		}
		return contacts;
	}

	public boolean addGroup(int user_id, String name, ArrayList<Integer> contact_ids)
			throws SQLException, ClassNotFoundException {
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement("insert into Group_details (user_id, group_name) values (?, ?);",
				Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, user_id);
		ps.setString(2, name);
		int result = ps.executeUpdate();
		if (result > 0) {
			ResultSet rs = ps.getGeneratedKeys();
			rs.next();
			int group_id = rs.getInt(1);
			PreparedStatement con_ps = con.prepareStatement("insert into Group_info values (?, ?);");
			for (int contact : contact_ids) {
				con_ps.setInt(1, group_id);
				con_ps.setInt(2, contact);
				con_ps.addBatch();
				System.out.println(group_id + ", " + contact);
			}
			int[] res = con_ps.executeBatch();
			for (int i = 0; i < res.length; i++) {
				if (res[i] < 1)
					return false;
			}
			return true;
		}
		return false;
	}

	public boolean addGroupContact(int group_id, ArrayList<Integer> contact_ids)
			throws SQLException, ClassNotFoundException {
		Connection con = getConnection();
		PreparedStatement con_ps = con.prepareStatement("insert into Group_info values (?, ?);");
		for (int contact : contact_ids) {
			con_ps.setInt(1, group_id);
			con_ps.setInt(2, contact);
			con_ps.addBatch();
		}
		int[] res = con_ps.executeBatch();
		for (int i = 0; i < res.length; i++) {
			if (res[i] < 1)
				return false;
		}
		return true;
	}

	public String hashPassword(String password, int type) {
		String hashed = "";
		switch (type) {
		case 1:
			hashed = BCrypt.hashpw(password, BCrypt.gensalt());
			break;
		case 2:
			int N = 32; // CPU cost
			int r = 20; // Memory Cost
			int p = 2; // Parallelization
			hashed = SCryptUtil.scrypt(password, N, r, p);
			break;
		default:
			break;
		}
		return hashed;
	}

	public boolean checkPassword(String password, String hashedPassword, int type) {
		boolean check = false;
		switch (type) {
		case 1:
			check = BCrypt.checkpw(password, hashedPassword);
			break;
		case 2:
			check = SCryptUtil.check(password, hashedPassword);
			break;
		default:
			break;
		}
		return check;
	}

//	public boolean migratePasswords() throws SQLException, ClassNotFoundException {
//		Connection con = getConnection();
//		PreparedStatement ps = con.prepareStatement("select * from User where isHashed=false;");
////        ps.setInt(1, noOfRows);
//		ResultSet rs = ps.executeQuery();
//		while (rs.next()) {
//			int user_id = rs.getInt("user_id");
//			String password = rs.getString("password");
//			String hashed = hashPasswor(password, user_id);
//			PreparedStatement change_ps = con
//					.prepareStatement("update User set password=?, isHashed=? where user_id=?;");
//			change_ps.setString(1, hashed);
//			change_ps.setInt(2, currentHashing);
//			change_ps.setInt(3, user_id);
//			int res = change_ps.executeUpdate();
//			if (res < 0)
//				return false;
//		}
//		return true;
//	}
}
