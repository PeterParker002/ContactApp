package com.contacts.model;

import java.util.ArrayList;

public class Group {
	private int groupId;
	private int userId;
	private ArrayList<Contact> contacts = new ArrayList<>();
	private String groupName;

	public int getGroupId() {
		return groupId;
	}

	public int getUniqueID() {
		return this.groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public ArrayList<Contact> getContact() {
		return this.contacts;
	}

	public void setContactId(int contact_id) {
		Contact c = new Contact();
		c.setContactId(contact_id);
		this.contacts.add(c);
	}

	public void setContactId(ArrayList<Contact> Contact) {
		this.contacts = Contact;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void update(Group g) {
		this.contacts.add(g.getContact().get(0));
	}

	public void update(Contact c) {
		this.contacts.add(c);
	}

	public ArrayList<Contact> getData(Contact contact) {
		return this.contacts;
	}

	public String toString() {
		return this.groupId + "";
	}
}
