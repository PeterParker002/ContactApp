package com.contacts.model;

public class MobileNumber {
	private int id;
	private Long mobileNumber;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Long getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(long mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String toString() {
		return this.id + " -> " + this.mobileNumber;
	}

}
