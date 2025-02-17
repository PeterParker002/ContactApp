package com.contacts.model;

import com.contacts.utils.Database.UserMobileNumber;
import com.contacts.utils.DatabaseImpl;

public class MobileNumber {
	private int id;
	private String mobileNumber;
	private long createdAt;
	private long modifiedAt;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUniqueID() {
		return this.id;
	}

	public DatabaseImpl getPrimaryKeyColumn() {
		return UserMobileNumber.ID;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
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

	public String toString() {
		return this.id + " -> " + this.mobileNumber;
	}

}
