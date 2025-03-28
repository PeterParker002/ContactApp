package com.contacts.dao;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.mindrot.jbcrypt.BCrypt;
import com.contacts.cache.SessionCache;
import com.contacts.model.*;
import com.contacts.querylayer.Column;
import com.contacts.querylayer.QueryBuilder;
import com.contacts.querylayer.QueryExecutor;
import com.contacts.querylayer.Table;
import com.contacts.schedulers.SessionScheduler;
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
	private static int currentHashing = 2;
	public static String[] Columns = { "Username", "First Name", "Middle Name", "Last Name", "Gender", "Date Of Birth",
			"Notes", "Home Address", "Work Address" };

	/**
	 * Sign Up Method
	 * 
	 * @param user
	 * @return the user_id of the User Object, after the validation
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static int SignupUser(User user) throws SQLException, ClassNotFoundException {
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
		qb.insertValuesToColumns(new Column(Users.CREATEDAT, "", "", qb.table), user.getCreatedAt());
		qb.insertValuesToColumns(new Column(Users.MODIFIEDAT, "", "", qb.table), user.getModifiedAt());
		int result = qx.executeAndUpdateWithKeys(qb.build());

		if (result > 0) {
			QueryBuilder mail_qb = new QueryBuilder();
			mail_qb.insertTable(TableInfo.USEREMAIL);
			mail_qb.insertValuesToColumns(new Column(UserEmail.USERID, "", "", mail_qb.table), result);
			mail_qb.insertValuesToColumns(new Column(UserEmail.EMAIL, "", "", mail_qb.table),
					user.getEmail().get(0).getEmail());
			mail_qb.insertValuesToColumns(new Column(UserEmail.ISPRIMARY, "", "", mail_qb.table), 1);
			mail_qb.insertValuesToColumns(new Column(UserEmail.CREATEDAT, "", "", qb.table),
					user.getEmail().get(0).getCreatedAt());
			mail_qb.insertValuesToColumns(new Column(UserEmail.MODIFIEDAT, "", "", qb.table),
					user.getEmail().get(0).getModifiedAt());
			if (qx.executeAndUpdateWithKeys(mail_qb.build()) > 0) {
				QueryBuilder mobile_qb = new QueryBuilder();
				mobile_qb.insertTable(TableInfo.USERMOBILENUMBER);
				mobile_qb.insertValuesToColumns(new Column(UserMobileNumber.USERID, "", "", mail_qb.table), result);
				mobile_qb.insertValuesToColumns(new Column(UserMobileNumber.MOBILENUMBER, "", "", mail_qb.table),
						user.getMobileNumber().get(0).getMobileNumber());
				mobile_qb.insertValuesToColumns(new Column(UserMobileNumber.CREATEDAT, "", "", qb.table),
						user.getMobileNumber().get(0).getCreatedAt());
				mobile_qb.insertValuesToColumns(new Column(UserMobileNumber.MODIFIEDAT, "", "", qb.table),
						user.getMobileNumber().get(0).getModifiedAt());
				return qx.executeAndUpdateWithKeys(mobile_qb.build()) > 0 ? result : -1;
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
	@SuppressWarnings("unchecked")
	public static User LoginUser(String email, String password)
			throws ClassNotFoundException, SQLException, IllegalAccessException, InvocationTargetException,
			InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.selectTable(TableInfo.USER);
		Table emailTable = new Table(TableInfo.USEREMAIL);
		qb.joinTables(JoinTypes.inner, new Column(Users.USERID, "", "", qb.table),
				new Column(UserEmail.USERID, "", "", emailTable));
		qb.setCondition(new Column(UserEmail.EMAIL, "", "", emailTable), Operators.EQUAL, email);
		List<User> users = (List<User>) qx.executeJoinQuery(qb.build());
		System.out.println(users);
		if (users != null) {
			if (users.size() > 0) {
				User u = (User) users.get(0);
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
	public static boolean addEmails(int user_id, String[] emails) throws ClassNotFoundException, SQLException {
		QueryExecutor qx = new QueryExecutor();
		long now = System.currentTimeMillis();
		for (String email : emails) {
			QueryBuilder qb = new QueryBuilder();
			qb.insertTable(TableInfo.USEREMAIL);
			qb.insertValuesToColumns(new Column(UserEmail.USERID, "", "", qb.table), user_id);
			qb.insertValuesToColumns(new Column(UserEmail.EMAIL, "", "", qb.table), email);
			qb.insertValuesToColumns(new Column(UserEmail.ISPRIMARY, "", "", qb.table), false);
			qb.insertValuesToColumns(new Column(UserEmail.CREATEDAT, "", "", qb.table), now);
			qb.insertValuesToColumns(new Column(UserEmail.MODIFIEDAT, "", "", qb.table), now);
			if (qx.executeAndUpdateWithKeys(qb.build()) < 1) {
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
	public static boolean changePrimaryMail(int user_id, int mail_id) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		long now = System.currentTimeMillis();
		qb.updateTable(TableInfo.USEREMAIL);
		qb.updateColumn(new Column(UserEmail.ISPRIMARY, "", "", qb.table), false);
		qb.updateColumn(new Column(UserEmail.MODIFIEDAT, "", "", qb.table), now);
		qb.setCondition(new Column(UserEmail.ISPRIMARY, "", "", qb.table), Operators.EQUAL, true);
		qb.setCondition(new Column(UserEmail.USERID, "", "", qb.table), Operators.EQUAL, user_id);
		System.out.println("Primary Mail Changed");
		if (qx.executeAndUpdate(qb.build()) > 0) {
			qb = new QueryBuilder();
			qb.updateTable(TableInfo.USEREMAIL);
			qb.updateColumn(new Column(UserEmail.ISPRIMARY, "", "", qb.table), true);
			qb.updateColumn(new Column(UserEmail.MODIFIEDAT, "", "", qb.table), now);
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
	@SuppressWarnings("unchecked")
	public static boolean checkIsPrimaryMail(int mail_id)
			throws ClassNotFoundException, SQLException, IllegalAccessException, InvocationTargetException,
			InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.selectTable(TableInfo.USEREMAIL);
		qb.selectColumn(new Column(UserEmail.ISPRIMARY, "", "", qb.table));
		qb.setCondition(new Column(UserEmail.ID, "", "", qb.table), Operators.EQUAL, mail_id);
		List<UserMail> r = (List<UserMail>) qx.executeQuery(qb.build());
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
	public static boolean deleteMail(int mail_id) throws ClassNotFoundException, SQLException {
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
	public static boolean addMobileNumbers(int user_id, String[] mobileNumbers)
			throws ClassNotFoundException, SQLException {
		QueryExecutor qx = new QueryExecutor();
		long now = System.currentTimeMillis();
		for (String number : mobileNumbers) {
			QueryBuilder qb = new QueryBuilder();
			qb.insertTable(TableInfo.USERMOBILENUMBER);
			qb.insertValuesToColumns(new Column(UserMobileNumber.USERID, "", "", qb.table), user_id);
			qb.insertValuesToColumns(new Column(UserMobileNumber.MOBILENUMBER, "", "", qb.table), number);
			qb.insertValuesToColumns(new Column(UserMobileNumber.CREATEDAT, "", "", qb.table), now);
			qb.insertValuesToColumns(new Column(UserMobileNumber.MODIFIEDAT, "", "", qb.table), now);
			if (qx.executeAndUpdateWithKeys(qb.build()) < 1) {
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
	public static boolean deleteMobileNumber(int mobile_id) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.deleteTable(TableInfo.USERMOBILENUMBER);
		qb.setCondition(new Column(UserMobileNumber.ID, "", "", qb.table), Operators.EQUAL, mobile_id);
		return qx.executeAndUpdate(qb.build()) > 0;
	}

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
	@SuppressWarnings("unchecked")
	public static User getUserInfo(int user_id) {
		QueryBuilder qb = new QueryBuilder();
		qb.selectTable(TableInfo.USER);
		Table emailTable = new Table(TableInfo.USEREMAIL);
		Table mobileTable = new Table(TableInfo.USERMOBILENUMBER);
		qb.joinTables(JoinTypes.left, new Column(Users.USERID, "", "", qb.table),
				new Column(UserEmail.USERID, "", "", emailTable));
		qb.joinTables(JoinTypes.left, new Column(Users.USERID, "", "", qb.table),
				new Column(UserMobileNumber.USERID, "", "", mobileTable));
		qb.setCondition(new Column(Users.USERID, "", "", qb.table), Operators.EQUAL, user_id);
		QueryExecutor qx = new QueryExecutor();
		List<User> r;
		User user = null;
		try {
			r = (List<User>) qx.executeJoinQuery(qb.build());
			user = r.get(0);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
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
	@SuppressWarnings("unchecked")
	public static List<UserMail> getUserMail(int user_id)
			throws ClassNotFoundException, SQLException, IllegalAccessException, InvocationTargetException,
			InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException {
		QueryBuilder qb = new QueryBuilder();
		qb.selectTable(TableInfo.USEREMAIL);
		qb.setCondition(new Column(UserEmail.USERID, "", "", qb.table), Operators.EQUAL, user_id);
		QueryExecutor qx = new QueryExecutor();
		List<UserMail> r = (List<UserMail>) qx.executeQuery(qb.build());
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
	@SuppressWarnings("unchecked")
	public static List<UserMobile> getUserMobileNumber(int user_id)
			throws ClassNotFoundException, SQLException, IllegalAccessException, InvocationTargetException,
			InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException {
		QueryBuilder qb = new QueryBuilder();
		qb.selectTable(TableInfo.USERMOBILENUMBER);
		qb.setCondition(new Column(UserMobileNumber.USERID, "", "", qb.table), Operators.EQUAL, user_id);
		QueryExecutor qx = new QueryExecutor();
		List<UserMobile> r = (List<UserMobile>) qx.executeQuery(qb.build());
		return r;
	}

	// Update Statement
	public static boolean editUserInfo(int user_id, User user) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.updateTable(TableInfo.USER);
		if (user.getFirstName() != null) {
			qb.updateColumn(new Column(Users.FIRSTNAME, "", "", qb.table), user.getFirstName());
		}
		if (user.getMiddleName() != null) {
			qb.updateColumn(new Column(Users.MIDDLENAME, "", "", qb.table), user.getMiddleName());
		}
		if (user.getLastName() != null) {
			qb.updateColumn(new Column(Users.LASTNAME, "", "", qb.table), user.getLastName());
		}
		if (user.getGender() != null) {
			qb.updateColumn(new Column(Users.GENDER, "", "", qb.table), user.getGender());
		}
		if (user.getDateOfBirth() != null) {
			qb.updateColumn(new Column(Users.DATEOFBIRTH, "", "", qb.table), user.getDateOfBirth());
		}
		if (user.getNotes() != null) {
			qb.updateColumn(new Column(Users.NOTES, "", "", qb.table), user.getNotes());
		}
		if (user.getHomeAddress() != null) {
			qb.updateColumn(new Column(Users.HOMEADDRESS, "", "", qb.table), user.getHomeAddress());
		}
		if (user.getWorkAddress() != null) {
			qb.updateColumn(new Column(Users.WORKADDRESS, "", "", qb.table), user.getWorkAddress());
		}
		qb.updateColumn(new Column(Users.MODIFIEDAT, "", "", qb.table), user.getModifiedAt());
		qb.setCondition(new Column(Users.USERID, "", "", qb.table), Operators.EQUAL, user_id);
		int res = qx.executeAndUpdate(qb.build());
		return res > 0;
	}

	// Select Statement
	@SuppressWarnings("unchecked")
	public static List<Group> getGroups(int user_id)
			throws SQLException, ClassNotFoundException, IllegalAccessException, InvocationTargetException,
			InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.selectTable(TableInfo.GROUPDETAILS);
		qb.setCondition(new Column(Users.USERID, "", "", qb.table), Operators.EQUAL, user_id);
		List<Group> groupsData = (List<Group>) qx.executeQuery(qb.build());
		List<Group> groups = new ArrayList<Group>();
		for (Group group : groupsData) {
			group.setGroupId(group.getGroupId());
			group.setContactId(getContactsByGroup(group.getGroupId()));
			group.setGroupName(group.getGroupName());
			groups.add(group);
		}
		return groups;
	}

	@SuppressWarnings("unchecked")
	public static List<Contact> getContactsByGroup(int group_id)
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
		List<Contact> contacts = (List<Contact>) qx.executeQuery(qb.build());
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
	public static boolean addGroup(int user_id, String name, ArrayList<Integer> contact_ids)
			throws SQLException, ClassNotFoundException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		long now = System.currentTimeMillis();
		qb.insertTable(TableInfo.GROUPDETAILS);
		qb.insertValuesToColumns(new Column(GroupDetails.USERID, "", "", qb.table), user_id);
		qb.insertValuesToColumns(new Column(GroupDetails.GROUPNAME, "", "", qb.table), name);
		qb.insertValuesToColumns(new Column(GroupDetails.CREATEDAT, "", "", qb.table), now);
		qb.insertValuesToColumns(new Column(GroupDetails.MODIFIEDAT, "", "", qb.table), now);
		int result = qx.executeAndUpdateWithKeys(qb.build());
		if (result > 0) {
			return addGroupContact(result, contact_ids);
		}
		return true;
	}

	// Insert Statement
	public static boolean addGroupContact(int group_id, ArrayList<Integer> contact_ids)
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
	public static String hashPassword(String password, int type) {
		String hashed = "";
		switch (type) {
		case 1:
			hashed = BCrypt.hashpw(password, BCrypt.gensalt());
			break;
		case 2:
			int N = 32;
			int r = 20;
			int p = 2;
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
	public static boolean checkPassword(String password, String hashedPassword, int type) {
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

	@SuppressWarnings("unchecked")
	public static Session getUserSession(String sessionId) {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.selectTable(TableInfo.SESSION);
		qb.setCondition(new Column(Database.Session.SESSIONID, "", "", qb.table), Operators.EQUAL, sessionId);
		try {
			List<Session> session = (List<Session>) qx.executeQuery(qb.build());
			if (session.size() > 0) {
				return session.get(0);
			}
		} catch (IllegalAccessException | InvocationTargetException | InstantiationException | IllegalArgumentException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Session createSession(Session session) throws ClassNotFoundException, SQLException {
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

	public static int updateSession(String sessionId, long lastAccessedAt) {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.updateTable(TableInfo.SESSION);
		qb.updateColumn(new Column(Database.Session.LASTACCESSEDAT, "", "", qb.table), lastAccessedAt);
		qb.setCondition(new Column(Database.Session.SESSIONID, "", "", qb.table), Operators.EQUAL, sessionId);
		qb.setCondition(new Column(Database.Session.LASTACCESSEDAT, "", "", qb.table), Operators.LESSTHAN,
				lastAccessedAt);
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

	public static int clearSession(String sessionId) {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.deleteTable(TableInfo.SESSION);
		qb.setCondition(new Column(Database.Session.SESSIONID, "", "", qb.table), Operators.EQUAL, sessionId);
		int res = -1;
		try {
			res = qx.executeAndUpdate(qb.build());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static int clearExpiredSessions() {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		long expired_time = System.currentTimeMillis() - SessionCache.EXPIRATION_TIME;
		qb.deleteTable(TableInfo.SESSION);
		qb.setCondition(new Column(Database.Session.LASTACCESSEDAT, "", "", qb.table), Operators.LESSTHAN,
				expired_time);
		qb.setLimit(200);
		int res = -1;
		try {
			res = qx.executeAndUpdate(qb.build());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static String generateSessionId() {
		return UUID.randomUUID().toString();
	}

	public static String getSessionIdFromCookie(HttpServletRequest req, String cookieName) {
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equalsIgnoreCase(cookieName)) {
					return cookie.getValue();
				}
			}
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	public static List<Server> getAvailableServers() throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.selectTable(TableInfo.SERVERS);
		qb.setCondition(new Column(Servers.IP, "", "", qb.table), Operators.NOTEQUAL, SessionScheduler.server_ip);
		qb.setCondition(new Column(Servers.PORT, "", "", qb.table), Operators.NOTEQUAL, SessionScheduler.server_port);
		qb.changeConjuction("OR");
		List<Server> result = (List<Server>) qx.executeQuery(qb.build());
		return result;
	}

	public static int addEntryToAvailableServers(String ip, int port) throws ClassNotFoundException, SQLException {
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

	public static int deleteEntryFromAvailableServers(String ip, int port) throws ClassNotFoundException, SQLException {
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
}
