package com.contacts.model;

import java.util.ArrayList;

public class Group {
	private int groupId;
	private int userId;
	private ArrayList<Contact> Contact = new ArrayList<>();
	private String groupName;

	public int getGroupId() {
		return groupId;
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
		return this.Contact;
	}

	public void setContactId(int contact_id) {
		Contact c = new Contact();
		c.setContactId(contact_id);
		this.Contact.add(c);
	}

	public void setContactId(ArrayList<Contact> Contact) {
		this.Contact = Contact;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void update(Group g) {
		this.Contact.add(g.getContact().get(0));
	}
}
