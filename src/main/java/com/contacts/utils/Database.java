package com.contacts.utils;

public class Database {

	public enum TableInfo implements DatabaseImpl {
		USER("User"), USEREMAIL("User_mail_ids"), USERMOBILENUMBER("user_mobile_numbers"), CONTACTS("Contacts"),
		CONTACTMAIL("contacts_mail_ids"), CONTACTMOBILENUMBER("contacts_mobile_numbers"), GROUPINFO("Group_info"),
		GROUPDETAILS("Group_details"), SAMPLE("Sample");

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
		NOTES("notes"), HOMEADDRESS("home_address"), WORKADDRESS("work_address"), ISHASHED("isHashed");

		private String col;

		private Users(String col) {
			this.col = col;
		}

		public String toString() {
			return this.col;
		}
	}

	public enum UserEmail implements DatabaseImpl {
		USERID("user_id"), EMAIL("email"), ISPRIMARY("isPrimary"), ID("id");

		private String col;

		private UserEmail(String col) {
			this.col = col;
		}

		public String toString() {
			return this.col;
		}
	}

	public enum UserMobileNumber implements DatabaseImpl {
		USERID("user_id"), MOBILENUMBER("mobile_number"), ID("id");

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
		HOMEADDRESS("home_address"), WORKADDRESS("work_address");

		private String col;

		private Contacts(String col) {
			this.col = col;
		}

		public String toString() {
			return this.col;
		}
	}

	public enum ContactMail implements DatabaseImpl {
		ID("id"), CONTACTID("contact_id"), EMAIL("email");

		private String col;

		private ContactMail(String col) {
			this.col = col;
		}

		public String toString() {
			return this.col;
		}
	}

	public enum ContactMobileNumber implements DatabaseImpl {
		ID("id"), CONTACTID("contact_id"), MOBILENUMBER("mobile_number");

		private String col;

		private ContactMobileNumber(String col) {
			this.col = col;
		}

		public String toString() {
			return this.col;
		}
	}

	public enum GroupInfo implements DatabaseImpl {
		GROUPID("group_id"), CONTACTID("contact_id");

		private String col;

		private GroupInfo(String col) {
			this.col = col;
		}

		public String toString() {
			return this.col;
		}
	}

	public enum GroupDetails implements DatabaseImpl {
		GROUPID("group_id"), USERID("user_id"), GROUPNAME("group_name");

		private String col;

		private GroupDetails(String col) {
			this.col = col;
		}

		public String toString() {
			return this.col;
		}
	}

	public enum Sample implements DatabaseImpl {
		AGE("age"), NAME("name");

		private String col;

		private Sample(String col) {
			this.col = col;
		}

		public String toString() {
			return this.col;
		}
	}
}
