package com.contacts.utils;

import java.util.ArrayList;

public class Database {

	public static ArrayList<TableInfo> auditableTables = new ArrayList<>() {
		{
			add(TableInfo.USER);
			add(TableInfo.USEREMAIL);
			add(TableInfo.USERMOBILENUMBER);
			add(TableInfo.CONTACTS);
			add(TableInfo.CONTACTMAIL);
			add(TableInfo.CONTACTMOBILENUMBER);
			add(TableInfo.GROUPINFO);
			add(TableInfo.GROUPDETAILS);
			add(TableInfo.SAMPLE);
		}
	};

	public enum TableInfo implements DatabaseImpl {
		USER("User"), USEREMAIL("User_mail_ids"), USERMOBILENUMBER("user_mobile_numbers"), CONTACTS("Contact"),
		CONTACTMAIL("contacts_mail_ids"), CONTACTMOBILENUMBER("contacts_mobile_numbers"), GROUPINFO("Group_info"),
		GROUPDETAILS("Group_details"), SAMPLE("Sample"), SESSION("Session"), SERVERS("available_servers"),
		AUDIT("audit_log");

		private String table;

		private TableInfo(String name) {
			this.table = name;
		}

		public String toString() {
			return this.table;
		}
	}

	public enum Users implements DatabaseImpl {
		USERID("user_id"), USERNAME("username"), PASSWORD("password"), FIRSTNAME("first_name"),
		MIDDLENAME("middle_name"), LASTNAME("last_name"), GENDER("gender"), DATEOFBIRTH("date_of_birth"),
		NOTES("notes"), HOMEADDRESS("home_address"), WORKADDRESS("work_address"), ISHASHED("isHashed"), CREATEDAT("created_at"), MODIFIEDAT("modified_at"), POJO("User");

		private String col;

		private Users(String col) {
			this.col = col;
		}

		public String toString() {
			return this.col;
		}
	}

	public enum UserEmail implements DatabaseImpl {
		USERID("user_id"), EMAIL("email"), ISPRIMARY("isPrimary"), ID("id"), CREATEDAT("created_at"), MODIFIEDAT("modified_at"), POJO("Mail");

		private String col;

		private UserEmail(String col) {
			this.col = col;
		}

		public String toString() {
			return this.col;
		}
	}

	public enum UserMobileNumber implements DatabaseImpl {
		USERID("user_id"), MOBILENUMBER("mobile_number"), ID("id"), CREATEDAT("created_at"), MODIFIEDAT("modified_at"), POJO("MobileNumber");

		private String col;

		private UserMobileNumber(String col) {
			this.col = col;
		}

		public String toString() {
			return this.col;
		}
	}

	public enum Contacts implements DatabaseImpl {
		CONTACTID("contact_id"), USERID("user_id"), FIRSTNAME("first_name"), MIDDLENAME("middle_name"),
		LASTNAME("last_name"), GENDER("gender"), DATEOFBIRTH("date_of_birth"), NOTES("notes"),
		HOMEADDRESS("home_address"), WORKADDRESS("work_address"), CREATEDAT("created_at"), MODIFIEDAT("modified_at"), POJO("Contact");

		private String col;

		private Contacts(String col) {
			this.col = col;
		}

		public String toString() {
			return this.col;
		}
	}

	public enum ContactEmail implements DatabaseImpl {
		ID("id"), CONTACTID("contact_id"), EMAIL("email"), CREATEDAT("created_at"), MODIFIEDAT("modified_at"), POJO("Mail");

		private String col;

		private ContactEmail(String col) {
			this.col = col;
		}

		public String toString() {
			return this.col;
		}
	}

	public enum ContactMobileNumber implements DatabaseImpl {
		ID("id"), CONTACTID("contact_id"), MOBILENUMBER("mobile_number"), CREATEDAT("created_at"), MODIFIEDAT("modified_at"), POJO("MobileNumber");

		private String col;

		private ContactMobileNumber(String col) {
			this.col = col;
		}

		public String toString() {
			return this.col;
		}
	}

	public enum GroupInfo implements DatabaseImpl {
		GROUPID("group_id"), CONTACTID("contact_id"), POJO("Group");

		private String col;

		private GroupInfo(String col) {
			this.col = col;
		}

		public String toString() {
			return this.col;
		}
	}

	public enum GroupDetails implements DatabaseImpl {
		GROUPID("group_id"), USERID("user_id"), GROUPNAME("group_name"), CREATEDAT("created_at"), MODIFIEDAT("modified_at"), POJO("Group");

		private String col;

		private GroupDetails(String col) {
			this.col = col;
		}

		public String toString() {
			return this.col;
		}
	}

	public enum Sample implements DatabaseImpl {
		AGE("age"), NAME("name"), ISPRIME("isPrime");

		private String col;

		private Sample(String col) {
			this.col = col;
		}

		public String toString() {
			return this.col;
		}
	}

	public enum Session implements DatabaseImpl {
		SESSIONID("session_id"), USERID("user_id"), CREATEDAT("created_at"), LASTACCESSEDAT("last_accessed_at");

		private String col;

		private Session(String col) {
			this.col = col;
		}

		public String toString() {
			return this.col;
		}
	}

	public enum Servers implements DatabaseImpl {
		IP("server_ip"), PORT("server_port");

		private String col;

		private Servers(String col) {
			this.col = col;
		}

		public String toString() {
			return this.col;
		}
	}

	public enum Audit implements DatabaseImpl {
		AUDITID("audit_id"), TABLENAME("table_name"), OLDVALUE("old_value"), NEWVALUE("new_value"), ACTION("action"),
		CREATEDAT("created_at");

		private String col;

		private Audit(String col) {
			this.col = col;
		}

		public String toString() {
			return this.col;
		}
	}
}
