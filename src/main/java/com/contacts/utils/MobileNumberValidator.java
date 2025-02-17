package com.contacts.utils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class MobileNumberValidator {
	private static final String regex = "^(?:\\+?[1-9]\\d{6,14}|\\d{1,6})$";
	public static boolean validate(String mobileNumber) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(mobileNumber);
		return matcher.matches();
	}
}
