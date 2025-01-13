package com.contacts.model;

import java.util.ArrayList;

public class User {
	private int userId;
	private String username;
	private String password;
	private String firstName;
	private String middleName;
	private String lastName;
	private String gender;
	private String dateOfBirth;
	private String Notes;
	private String workAddress;
	private String homeAddress;
	private int isHashed;
	private ArrayList<UserMail> email = new ArrayList<>();
	private ArrayList<UserMobile> mobileNumber = new ArrayList<>();

	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public ArrayList<UserMail> getEmail() {
		return email;
	}

	public void setEmail(UserMail mail) {
		this.email.add(mail);
	}

	public void setEmail(ArrayList<UserMail> mail) {
		this.email = mail;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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
		return Notes;
	}

	public void setNotes(String notes) {
		Notes = notes;
	}

	public String getWorkAddress() {
		return workAddress;
	}

	public void setWorkAddress(String workAddress) {
		this.workAddress = workAddress;
	}

	public String getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}

	public ArrayList<UserMobile> getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(UserMobile mobileNumber) {
		this.mobileNumber.add(mobileNumber);
	}

	public void setMobileNumber(ArrayList<UserMobile> mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getIsHashed() {
		return isHashed;
	}

	public void setIsHashed(int isHashed) {
		this.isHashed = isHashed;
	}

	public void update(UserMail mail) {
		this.email.add(mail);
	}

	public void update(UserMobile mobile) {
		this.mobileNumber.add(mobile);
	}

	public int getUniqueID() {
		return this.userId;
	}

	public ArrayList<UserMail> getData(UserMail mail) {
		return this.email;
	}

	public ArrayList<UserMobile> getData(UserMobile mobile) {
		return this.mobileNumber;
	}

}
