package com.contacts.model;

import java.util.ArrayList;
import java.util.List;

import com.contacts.utils.Database.GroupInfo;
import com.contacts.utils.DatabaseImpl;

public class Group {
	private int groupId;
	private int userId;
	private List<Contact> contacts = new ArrayList<>();
	private String groupName;
	private long createdAt;
	private long modifiedAt;

	public int getGroupId() {
		return groupId;
	}

	public int getUniqueID() {
		return this.groupId;
	}

	public DatabaseImpl getPrimaryKeyColumn() {
		return GroupInfo.GROUPID;
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

	public List<Contact> getContact() {
		return this.contacts;
	}

	public void setContactId(int contact_id) {
		Contact c = new Contact();
		c.setContactId(contact_id);
		this.contacts.add(c);
	}

	public void setContactId(List<Contact> Contact) {
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

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public long getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(long modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public List<Contact> getData(Contact contact) {
		return this.contacts;
	}

	public String toString() {
		return this.groupId + "";
	}
}
