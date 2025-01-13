package com.contacts.dao;

import java.lang.reflect.InvocationTargetException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.mindrot.jbcrypt.BCrypt;

import com.contacts.connection.ConnectionPool;
import com.contacts.model.Contact;
import com.contacts.model.Group;
import com.contacts.model.Mail;
import com.contacts.model.MobileNumber;
import com.contacts.model.User;
import com.contacts.model.UserMail;
import com.contacts.model.UserMobile;
import com.contacts.model.Session;
import com.contacts.querylayer.Column;
import com.contacts.querylayer.QueryBuilder;
import com.contacts.querylayer.QueryExecutor;
import com.contacts.querylayer.Table;
import com.contacts.utils.JoinTypes;
import com.contacts.utils.Operators;
import com.contacts.utils.Database;
import com.contacts.utils.Database.Contacts;
import com.contacts.utils.Database.GroupDetails;
import com.contacts.utils.Database.GroupInfo;
import com.contacts.utils.Database.Servers;
import com.contacts.utils.Database.TableInfo;
import com.contacts.utils.Database.UserEmail;
import com.contacts.utils.Database.UserMobileNumber;
import com.contacts.utils.Database.Users;
import com.lambdaworks.crypto.SCryptUtil;

public class UserDAO {
	private String username = "root";
	private String password = "root";
	private String db_name = "ContactsApp";
	// 1 -> Bcrypt, 2 -> Scrypt
	private int currentHashing = 2;
	public static String[] Columns = { "Username", "First Name", "Middle Name", "Last Name", "Gender", "Date Of Birth",
			"Notes", "Home Address", "Work Address" };

	/**
	 * Get Connection Method
	 * 
	 * @return the MySQL database connection Object for the specified database
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
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

	// Insert Statement
	/**
	 * Sign Up Method
	 * 
	 * @param user
	 * @return the user_id of the User Object, after the validation
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public int SignupUser(User user) throws SQLException, ClassNotFoundException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.insertTable(TableInfo.USER);
		qb.insertValuesToColumns(new Column(Users.USERNAME, "", "", qb.table), user.getUsername());
		qb.insertValuesToColumns(new Column(Users.PASSWORD, "", "", qb.table),
				hashPassword(user.getPassword(), currentHashing));
		qb.insertValuesToColumns(new Column(Users.FIRSTNAME, "", "", qb.table), user.getFirstName());
		qb.insertValuesToColumns(new Column(Users.MIDDLENAME, "", "", qb.table), user.getMiddleName());
		qb.insertValuesToColumns(new Column(Users.LASTNAME, "", "", qb.table), user.getLastName());
		qb.insertValuesToColumns(new Column(Users.GENDER, "", "", qb.table), user.getGender());
		qb.insertValuesToColumns(new Column(Users.DATEOFBIRTH, "", "", qb.table), user.getDateOfBirth());
		qb.insertValuesToColumns(new Column(Users.NOTES, "", "", qb.table), user.getNotes());
		qb.insertValuesToColumns(new Column(Users.HOMEADDRESS, "", "", qb.table), user.getHomeAddress());
		qb.insertValuesToColumns(new Column(Users.WORKADDRESS, "", "", qb.table), user.getWorkAddress());
		qb.insertValuesToColumns(new Column(Users.ISHASHED, "", "", qb.table), currentHashing);
		int result = qx.executeAndUpdateWithKeys(qb.build());

		if (result > 0) {
			QueryBuilder mail_qb = new QueryBuilder();
			mail_qb.insertTable(TableInfo.USEREMAIL);
			mail_qb.insertValuesToColumns(new Column(UserEmail.USERID, "", "", mail_qb.table), result);
			mail_qb.insertValuesToColumns(new Column(UserEmail.EMAIL, "", "", mail_qb.table),
					user.getEmail().get(0).getEmail());
			mail_qb.insertValuesToColumns(new Column(UserEmail.ISPRIMARY, "", "", mail_qb.table), 1);
			if (qx.executeAndUpdate(mail_qb.build()) > 0) {
				QueryBuilder mobile_qb = new QueryBuilder();
				mobile_qb.insertTable(TableInfo.USERMOBILENUMBER);
				mobile_qb.insertValuesToColumns(new Column(UserMobileNumber.USERID, "", "", mail_qb.table), result);
				mobile_qb.insertValuesToColumns(new Column(UserMobileNumber.MOBILENUMBER, "", "", mail_qb.table),
						user.getMobileNumber().get(0).getMobileNumber());
				return qx.executeAndUpdate(mobile_qb.build()) > 0 ? result : -1;
			}
		}
		return -1;
	}

	// Select Statement with Update Statement
	/**
	 * Login User Method
	 * 
	 * @param email
	 * @param password
	 * @return An integer representing whether the login process is success or not
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public User LoginUser(String email, String password)
			throws ClassNotFoundException, SQLException, IllegalAccessException, InvocationTargetException,
			InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.selectTable(TableInfo.USER);
		Table emailTable = new Table(TableInfo.USEREMAIL);
		qb.joinTables(JoinTypes.inner, new Column(Users.USERID, "", "", qb.table),
				new Column(UserEmail.USERID, "", "", emailTable));
		qb.setCondition(new Column(UserEmail.EMAIL, "", "", emailTable), Operators.EQUAL, email);
//		HashMap<String, Object> users = qx.executeJoinQuery(qb.build());
		ArrayList<User> users = (ArrayList<User>) qx.executeJoinQuery1(qb.build());
		System.out.println(users);
		if (users != null) {
			if (users.size() > 0) {
				User u = (User) users.get(0);
//			u.setEmail(this.getUserMail(u.getUserId()));
//			u.setMobileNumber(this.getUserMobileNumber(u.getUserId()));
				if (checkPassword(password, u.getPassword(), u.getIsHashed())) {
					if (currentHashing != u.getIsHashed()) {
						String newHashedPassword = hashPassword(password, currentHashing);
						QueryBuilder passwordChangeQb = new QueryBuilder();
						passwordChangeQb.updateTable(TableInfo.USER);
						passwordChangeQb.updateColumn(new Column(Users.PASSWORD, "", "", passwordChangeQb.table),
								newHashedPassword);
						passwordChangeQb.updateColumn(new Column(Users.ISHASHED, "", "", passwordChangeQb.table),
								currentHashing);
						passwordChangeQb.setCondition(new Column(Users.USERID, "", "", passwordChangeQb.table),
								Operators.EQUAL, u.getUserId());
						qx.executeAndUpdate(passwordChangeQb.build());
					}
					u = getUserInfo(u.getUserId());
					return u;
				}
			} else {
				return null;
			}
		}
		return null;
	}

	/**
	 * Add Emails Method
	 * 
	 * @param user_id
	 * @param emails
	 * @return boolean value representing whether the execution is successful or
	 *         not.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public boolean addEmails(int user_id, String[] emails) throws ClassNotFoundException, SQLException {
		QueryExecutor qx = new QueryExecutor();
		for (String email : emails) {
			QueryBuilder qb = new QueryBuilder();
			qb.insertTable(TableInfo.USEREMAIL);
			qb.insertValuesToColumns(new Column(UserEmail.USERID, "", "", qb.table), user_id);
			qb.insertValuesToColumns(new Column(UserEmail.EMAIL, "", "", qb.table), email);
			qb.insertValuesToColumns(new Column(UserEmail.ISPRIMARY, "", "", qb.table), false);
			if (qx.executeAndUpdate(qb.build()) < 1) {
				return false;
			}
		}
		return true;
	}

	// Update Statement
	/**
	 * Change Primary Mail Method
	 * 
	 * @param user_id
	 * @param mail_id
	 * @return boolean value representing whether the operation success or not
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public boolean changePrimaryMail(int user_id, int mail_id) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.updateTable(TableInfo.USEREMAIL);
		qb.updateColumn(new Column(UserEmail.ISPRIMARY, "", "", qb.table), false);
		qb.setCondition(new Column(UserEmail.ISPRIMARY, "", "", qb.table), Operators.EQUAL, true);
		qb.setCondition(new Column(UserEmail.USERID, "", "", qb.table), Operators.EQUAL, user_id);
		System.out.println("Primary Mail Changed");
		if (qx.executeAndUpdate(qb.build()) > 0) {
			qb = new QueryBuilder();
			qb.updateTable(TableInfo.USEREMAIL);
			qb.updateColumn(new Column(UserEmail.ISPRIMARY, "", "", qb.table), true);
			qb.setCondition(new Column(UserEmail.ID, "", "", qb.table), Operators.EQUAL, mail_id);
			qb.setCondition(new Column(UserEmail.USERID, "", "", qb.table), Operators.EQUAL, user_id);
			return qx.executeAndUpdate(qb.build()) > 0;
		}
		return false;
	}

	// Select Statement
	/**
	 * Check IsPrimary Mail Method
	 * 
	 * @param mail_id
	 * @return boolean value representing that the mail is primary or not
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 */
	public boolean checkIsPrimaryMail(int mail_id)
			throws ClassNotFoundException, SQLException, IllegalAccessException, InvocationTargetException,
			InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.selectTable(TableInfo.USEREMAIL);
		qb.selectColumn(new Column(UserEmail.ISPRIMARY, "", "", qb.table));
		qb.setCondition(new Column(UserEmail.ID, "", "", qb.table), Operators.EQUAL, mail_id);
		ArrayList<UserMail> r = (ArrayList<UserMail>) qx.executeQuery(qb.build());
		return r.get(0).getIsPrimary();
	}

	// Delete Statement
	/**
	 * Delete Mail Method
	 * 
	 * @param mail_id
	 * @return boolean value representing whether the operation success or not
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public boolean deleteMail(int mail_id) throws ClassNotFoundException, SQLException {
//		Connection con = getConnection();
//		PreparedStatement ps1 = con.prepareStatement("delete from User_mail_ids where id=?;");
//		ps1.setInt(1, mail_id);
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.deleteTable(TableInfo.USEREMAIL);
		qb.setCondition(new Column(UserEmail.ID, "", "", qb.table), Operators.EQUAL, mail_id);
		return qx.executeAndUpdate(qb.build()) > 0;
	}

	// Insert Statement
	/**
	 * Add Mobile Number Method
	 * 
	 * @param user_id
	 * @param mobileNumbers
	 * @return boolean value representing whether the operation is success or not.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public boolean addMobileNumbers(int user_id, Long[] mobileNumbers) throws ClassNotFoundException, SQLException {
		QueryExecutor qx = new QueryExecutor();
		for (Long number : mobileNumbers) {
			QueryBuilder qb = new QueryBuilder();
			qb.insertTable(TableInfo.USERMOBILENUMBER);
			qb.insertValuesToColumns(new Column(UserMobileNumber.USERID, "", "", qb.table), user_id);
			qb.insertValuesToColumns(new Column(UserMobileNumber.MOBILENUMBER, "", "", qb.table), number);
			if (qx.executeAndUpdate(qb.build()) < 1) {
				return false;
			}
		}
		return true;
	}

	// Delete Statement
	/**
	 * Delete Mobile Number Method
	 * 
	 * @param mobile_id
	 * @return boolean value representing whether the operation is success or not.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public boolean deleteMobileNumber(int mobile_id) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.deleteTable(TableInfo.USERMOBILENUMBER);
		qb.setCondition(new Column(UserMobileNumber.ID, "", "", qb.table), Operators.EQUAL, mobile_id);
		return qx.executeAndUpdate(qb.build()) > 0;
	}

	// select Statement
//	public String getUsername(int id) throws SQLException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {
//		QueryBuilder qb = new QueryBuilder();
//		QueryExecutor qx = new QueryExecutor();
//		qb.selectTable(TableInfo.USER);
//		qb.selectColumn(new Column(Users.USERNAME, "", "", qb.table));
//		qb.setCondition(new Column(Users.USERID, "", "", qb.table), Operators.EQUAL, id);
////		Connection c = getConnection();
////		PreparedStatement ps = c.prepareStatement("select username from User where user_id=?;");
////		ps.setInt(1, id);
//		ArrayList<User> res = (ArrayList<User>) qx.executeQuery(qb.build());
//		if (res.size() > 0) {
//			return res.get(0).getUsername();
//		} else {
//			return "";
//		}
//	}

//	public ResultSet getAllUsers() throws ClassNotFoundException, SQLException {
//		Connection con = getConnection();
//		PreparedStatement ps = con.prepareStatement(
//				"select * from User user inner join User_mail_ids mail on user.user_id=mail.user_id inner join user_mobile_numbers mobile on user.user_id=mobile.user_id;");
//		ResultSet r = ps.executeQuery();
//		return r;
//	}

	// Select Statement
	/**
	 * Get User Info Method
	 * 
	 * @param user_id
	 * @return User Object
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 */
	public User getUserInfo(int user_id) {
		QueryBuilder qb = new QueryBuilder();
		qb.selectTable(TableInfo.USER);
//		qb.selectColumn(new Column(Users.USERID, "", "", qb.table));
//		qb.selectColumn(new Column(Users.USERNAME, "", "", qb.table));
//		qb.selectColumn(new Column(Users.FIRSTNAME, "", "", qb.table));
//		qb.selectColumn(new Column(Users.MIDDLENAME, "", "", qb.table));
//		qb.selectColumn(new Column(Users.LASTNAME, "", "", qb.table));
//		qb.selectColumn(new Column(Users.DATEOFBIRTH, "", "", qb.table));
//		qb.selectColumn(new Column(Users.GENDER, "", "", qb.table));
//		qb.selectColumn(new Column(Users.NOTES, "", "", qb.table));
//		qb.selectColumn(new Column(Users.HOMEADDRESS, "", "", qb.table));
//		qb.selectColumn(new Column(Users.WORKADDRESS, "", "", qb.table));
//		qb.selectColumn(new Column(Users.ISHASHED, "", "", qb.table));
		Table emailTable = new Table(TableInfo.USEREMAIL);
		Table mobileTable = new Table(TableInfo.USERMOBILENUMBER);
		qb.joinTables(JoinTypes.inner, new Column(Users.USERID, "", "", qb.table),
				new Column(UserEmail.USERID, "", "", emailTable));
		qb.joinTables(JoinTypes.inner, new Column(Users.USERID, "", "", qb.table),
				new Column(UserMobileNumber.USERID, "", "", mobileTable));
		qb.setCondition(new Column(Users.USERID, "", "", qb.table), Operators.EQUAL, user_id);
		QueryExecutor qx = new QueryExecutor();
		ArrayList<User> r;
		User user = null;
		try {
			r = (ArrayList<User>) qx.executeJoinQuery1(qb.build());
			user = r.get(0);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		user.setEmail(getUserMail(user_id));
//		user.setMobileNumber(getUserMobileNumber(user_id));
		return user;
	}

	// Select Statement
	/**
	 * Get User Mail Method
	 * 
	 * @param user_id
	 * @return An arraylist of Mail Objects
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 */
	public ArrayList<UserMail> getUserMail(int user_id)
			throws ClassNotFoundException, SQLException, IllegalAccessException, InvocationTargetException,
			InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException {
		QueryBuilder qb = new QueryBuilder();
		qb.selectTable(TableInfo.USEREMAIL);
		qb.selectColumn(new Column(UserEmail.ID, "", "", qb.table));
		qb.selectColumn(new Column(UserEmail.EMAIL, "", "", qb.table));
		qb.selectColumn(new Column(UserEmail.ISPRIMARY, "", "", qb.table));
		qb.setCondition(new Column(UserEmail.USERID, "", "", qb.table), Operators.EQUAL, user_id);
		QueryExecutor qx = new QueryExecutor();
		ArrayList<UserMail> r = (ArrayList<UserMail>) qx.executeQuery(qb.build());
		return r;
	}

	// Select Statement
	/**
	 * Get User Mobile Number Method
	 * 
	 * @param user_id
	 * @return an ArrayList of MobileNumber Objects.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 */
	public ArrayList<UserMobile> getUserMobileNumber(int user_id)
			throws ClassNotFoundException, SQLException, IllegalAccessException, InvocationTargetException,
			InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException {
		QueryBuilder qb = new QueryBuilder();
		qb.selectTable(TableInfo.USERMOBILENUMBER);
		qb.selectColumn(new Column(UserMobileNumber.ID, "", "", qb.table));
		qb.selectColumn(new Column(UserMobileNumber.MOBILENUMBER, "", "", qb.table));
		qb.setCondition(new Column(UserMobileNumber.USERID, "", "", qb.table), Operators.EQUAL, user_id);
		QueryExecutor qx = new QueryExecutor();
		ArrayList<UserMobile> r = (ArrayList<UserMobile>) qx.executeQuery(qb.build());
		return r;
	}

	// Update Statement
	public boolean editUserInfo(int user_id, User user) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.updateTable(TableInfo.USER);
		qb.updateColumn(new Column(Users.FIRSTNAME, "", "", qb.table), user.getFirstName());
		qb.updateColumn(new Column(Users.MIDDLENAME, "", "", qb.table), user.getMiddleName());
		qb.updateColumn(new Column(Users.LASTNAME, "", "", qb.table), user.getLastName());
		qb.updateColumn(new Column(Users.GENDER, "", "", qb.table), user.getGender());
		qb.updateColumn(new Column(Users.DATEOFBIRTH, "", "", qb.table), user.getDateOfBirth());
		qb.updateColumn(new Column(Users.NOTES, "", "", qb.table), user.getNotes());
		qb.updateColumn(new Column(Users.HOMEADDRESS, "", "", qb.table), user.getHomeAddress());
		qb.updateColumn(new Column(Users.WORKADDRESS, "", "", qb.table), user.getWorkAddress());
		qb.setCondition(new Column(Users.USERID, "", "", qb.table), Operators.EQUAL, user_id);
		int res = qx.executeAndUpdate(qb.build());
		return res > 0;
	}

	// Select Statement
	public ArrayList<Group> getGroups(int user_id)
			throws SQLException, ClassNotFoundException, IllegalAccessException, InvocationTargetException,
			InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.selectTable(TableInfo.GROUPDETAILS);
		qb.setCondition(new Column(Users.USERID, "", "", qb.table), Operators.EQUAL, user_id);
		ArrayList<Group> groupsData = (ArrayList<Group>) qx.executeQuery(qb.build());
		ArrayList<Group> groups = new ArrayList<Group>();
		for (Group group : groupsData) {
			group.setGroupId(group.getGroupId());
			group.setContactId(getContactsByGroup(group.getGroupId()));
			group.setGroupName(group.getGroupName());
			groups.add(group);
		}
		return groups;
	}

	// Select Statement with Join
//	public ArrayList<Contact> getContactsByGroup(int group_id)
//			throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException,
//			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
////		Connection con = getConnection();
////		PreparedStatement ps = con.prepareStatement(
////				"select g.contact_id, c.first_name, c.middle_name, c.last_name from Group_info g join Contact c on g.contact_id=c.contact_id where group_id=?;");
////		ps.setInt(1, group_id);
////		ResultSet rs = ps.executeQuery();
////		ArrayList<Contact> contacts = new ArrayList<>();
////		while (rs.next()) {
////			Contact c = new Contact();
////			c.setContactId(rs.getInt(1));
////			c.setFirstName(rs.getString(2));
////			c.setMiddleName(rs.getString(3));
////			c.setLastName(rs.getString(4));
////			contacts.add(c);
////		}
//		QueryBuilder qb = new QueryBuilder();
//		QueryExecutor qx = new QueryExecutor();
//		ArrayList<Contact> contacts = new ArrayList<Contact>();
//		qb.selectTable(TableInfo.GROUPINFO);
//		Table contactTable = new Table(TableInfo.CONTACTS);
//		qb.joinTables(JoinTypes.inner, new Column(GroupInfo.CONTACTID, "", "", qb.table),
//				new Column(Contacts.CONTACTID, "", "", contactTable));
//		qb.setCondition(new Column(GroupInfo.GROUPID, "", "", qb.table), Operators.EQUAL, group_id);
//		ArrayList<Group> groups = (ArrayList<Group>) qx.executeJoinQuery1(qb.build());
//		if (groups.size() > 0) {
//			contacts = groups.get(0).getContact();
//		}
//		return contacts;
//	}

	public ArrayList<Contact> getContactsByGroup(int group_id)
			throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.selectTable(TableInfo.CONTACTS);
		QueryBuilder inner_qb = new QueryBuilder();
		inner_qb.selectTable(TableInfo.GROUPINFO);
		inner_qb.selectColumn(new Column(GroupInfo.CONTACTID, "", "", inner_qb.table));
		inner_qb.setCondition(new Column(GroupInfo.GROUPID, "", "", inner_qb.table), Operators.EQUAL, group_id);
		qb.setCondition(new Column(Contacts.CONTACTID, "", "", qb.table), Operators.IN, inner_qb.build());
		ArrayList<Contact> contacts = (ArrayList<Contact>) qx.executeQuery(qb.build());
		return contacts;
	}

	// Insert Statement
	/**
	 * Add Group Method
	 * 
	 * @param user_id
	 * @param name
	 * @param contact_ids
	 * @return boolean value representing that whether the operation is success or
	 *         not.
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public boolean addGroup(int user_id, String name, ArrayList<Integer> contact_ids)
			throws SQLException, ClassNotFoundException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.insertTable(TableInfo.GROUPDETAILS);
		qb.insertValuesToColumns(new Column(GroupDetails.USERID, "", "", qb.table), user_id);
		qb.insertValuesToColumns(new Column(GroupDetails.GROUPNAME, "", "", qb.table), name);
		int result = qx.executeAndUpdateWithKeys(qb.build());
		if (result > 0) {
			return addGroupContact(result, contact_ids);
		}
		return true;
	}

	// Insert Statement
	public boolean addGroupContact(int group_id, ArrayList<Integer> contact_ids)
			throws SQLException, ClassNotFoundException {
		for (int contact : contact_ids) {
			QueryBuilder qb = new QueryBuilder();
			QueryExecutor qx = new QueryExecutor();
			qb.insertTable(TableInfo.GROUPINFO);
			qb.insertValuesToColumns(new Column(GroupInfo.GROUPID, "", "", qb.table), group_id);
			qb.insertValuesToColumns(new Column(GroupInfo.CONTACTID, "", "", qb.table), contact);
			if (qx.executeAndUpdate(qb.build()) < 1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Custom Password Hashing Method
	 * 
	 * @param password
	 * @param type
	 * @return returns the hashed password for the respective hashing algorithm
	 *         mentioned
	 */
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

	/**
	 * Custom Password Checking Method
	 * 
	 * @param password
	 * @param hashedPassword
	 * @param type
	 * @return checks whether the password is correct or not, based on the
	 *         respective algorithm
	 */
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

	public Session getUserSession(String sessionId) {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.selectTable(TableInfo.SESSION);
		qb.setCondition(new Column(Database.Session.SESSIONID, "", "", qb.table), Operators.EQUAL, sessionId);
		try {
			ArrayList<Session> session = (ArrayList<Session>) qx.executeQuery(qb.build());
			if (session.size() > 0) {
				return session.get(0);
			} else {
				return null;
			}
		} catch (IllegalAccessException | InvocationTargetException | InstantiationException | IllegalArgumentException
				| NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Session createSession(Session session) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.insertTable(TableInfo.SESSION);
		qb.insertValuesToColumns(new Column(Database.Session.SESSIONID, "", "", qb.table), session.getSessionId());
		qb.insertValuesToColumns(new Column(Database.Session.USERID, "", "", qb.table), session.getUserId());
		qb.insertValuesToColumns(new Column(Database.Session.CREATEDAT, "", "", qb.table), session.getCreatedAt());
		qb.insertValuesToColumns(new Column(Database.Session.LASTACCESSEDAT, "", "", qb.table),
				session.getLastAccessedAt());
		if (qx.executeAndUpdate(qb.build()) > 0) {
			return session;
		}
		return null;
	}

	public int updateSession(String sessionId, LocalDateTime lastAccessedAt) {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.updateTable(TableInfo.SESSION);
		qb.updateColumn(new Column(Database.Session.LASTACCESSEDAT, "", "", qb.table), lastAccessedAt.toString());
		qb.setCondition(new Column(Database.Session.SESSIONID, "", "", qb.table), Operators.EQUAL, sessionId);
		int res;
		try {
			res = qx.executeAndUpdate(qb.build());
			if (res > 0) {
				return res;
			}
		} catch (ClassNotFoundException | SQLException e) {

		}
		return -1;
	}

	public int clearSession(String sessionId) {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.deleteTable(TableInfo.SESSION);
		qb.setCondition(new Column(Database.Session.SESSIONID, "", "", qb.table), Operators.EQUAL, sessionId);
		int res = -1;
		try {
			res = qx.executeAndUpdate(qb.build());
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public String generateSessionId() {
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		ArrayList<Character> charList = new ArrayList<>();
		for (char c : characters.toCharArray()) {
			charList.add(c);
		}

		// Shuffle using SecureRandom
		SecureRandom secureRandom = new SecureRandom();
		for (int i = charList.size() - 1; i > 0; i--) {
			int j = secureRandom.nextInt(i + 1);
			Collections.swap(charList, i, j);
		}

		// Generate the random string with unique characters
		StringBuilder randomString = new StringBuilder();
		for (int i = 0; i < 16; i++) {
			randomString.append(charList.get(i));
		}
		return randomString.toString();
	}

	public String getSessionIdFromCookie(HttpServletRequest req, String cookieName) {
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equalsIgnoreCase(cookieName)) {
//					System.out.println(cookie.getValue());
					return cookie.getValue();
				}
			}
		}
		return "";
	}

	public int addEntryToAvailableServers(String ip, String port) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.insertTable(TableInfo.SERVERS);
		qb.insertValuesToColumns(new Column(Servers.IP, "", "", qb.table), ip);
		qb.insertValuesToColumns(new Column(Servers.PORT, "", "", qb.table), port);
		if (qx.executeAndUpdate(qb.build()) > 0) {
			return 1;
		}
		return -1;
	}

	public int deleteEntryFromAvailableServers(String ip, String port) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.deleteTable(TableInfo.SERVERS);
		qb.setCondition(new Column(Servers.IP, "", "", qb.table), Operators.EQUAL, ip);
		qb.setCondition(new Column(Servers.PORT, "", "", qb.table), Operators.EQUAL, port);
		if (qx.executeAndUpdate(qb.build()) > 0) {
			return 1;
		}
		return -1;
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
