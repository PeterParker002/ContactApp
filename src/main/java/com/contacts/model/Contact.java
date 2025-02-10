package com.contacts.model;

import java.util.ArrayList;

import com.contacts.utils.Database.Contacts;
import com.contacts.utils.DatabaseImpl;

public class Contact {
	private int contact_id;
	private int user_id;
	private String firstName;
	private String middleName;
	private String lastName;
	private String gender;
	private String dateOfBirth;
	private String notes;
	private String homeAddress;
	private String workAddress;
	private ArrayList<ContactMail> Email = new ArrayList<ContactMail>();
	private ArrayList<ContactMobile> mobileNumber = new ArrayList<ContactMobile>();
	private long createdAt;
	private long modifiedAt;

	public ArrayList<ContactMail> getEmail() {
		return this.Email;
	}

	public void setEmail(String email) {
		ContactMail mail = new ContactMail();
		mail.setEmail(email);
		this.Email.add(mail);
	}

	public void setEmail(ContactMail mail) {
		this.Email.add(mail);
	}

	public ArrayList<ContactMobile> getMobileNumber() {
		return this.mobileNumber;
	}

	public void setMobileNumber(Long mobileNumber) {
		ContactMobile mobile = new ContactMobile();
		mobile.setMobileNumber(mobileNumber);
		this.mobileNumber.add(mobile);
	}

	public void setMobileNumber(ContactMobile mobile) {
		this.mobileNumber.add(mobile);
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}

	public String getWorkAddress() {
		return workAddress;
	}

	public void setWorkAddress(String workAddress) {
		this.workAddress = workAddress;
	}

	public int getContactId() {
		return contact_id;
	}

	public void setContactId(int contact_id) {
		this.contact_id = contact_id;
	}

	public int getUserId() {
		return user_id;
	}

	public void setUserId(int user_id) {
		this.user_id = user_id;
	}

	public String toString() {
		return this.contact_id + "";
	}

	public void update(ContactMail mail) {
		this.Email.add(mail);
	}

	public void update(ContactMobile mobile) {
		this.mobileNumber.add(mobile);
	}

	public int getUniqueID() {
		return this.contact_id;
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

	public DatabaseImpl getPrimaryKeyColumn() {
		return Contacts.CONTACTID;
	}

	public ArrayList<ContactMail> getData(ContactMail mail) {
		return this.Email;
	}

	public ArrayList<ContactMobile> getData(ContactMobile mobile) {
		return this.mobileNumber;
	}
}
