package com.contacts.handler;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

@SuppressWarnings("unchecked")
public class Test {
	public static void main(String[] args) {
		String userInfo = "{ \"connections\": [ { \"names\": [ { \"displayName\": \"John Doe\" } ], \"phoneNumbers\": [ { \"value\": \"+1234567890\" } ], \"emailAddresses\": [ { \"value\": \"john@example.com\" } ], \"birthdays\": [ { \"date\": { \"year\": 1990, \"month\": 5, \"day\": 15 } } ] } ] }";

		Gson gson = new Gson();
		Map<String, Object> result = gson.fromJson(userInfo, Map.class);

		List<Map<String, Object>> connections = (List<Map<String, Object>>) result.get("connections");
		if (connections != null && !connections.isEmpty()) {
			for (Map<String, Object> contact : connections) {
				String name = extractField(contact, "names", "displayName");
				String phone = extractField(contact, "phoneNumbers", "value");
				String email = extractField(contact, "emailAddresses", "value");
				String birthday = extractDateField(contact, "birthdays");

				System.out.println("Name: " + name);
				System.out.println("Phone: " + phone);
				System.out.println("Email: " + email);
				System.out.println("Birthday: " + birthday);
				System.out.println("---------------------");
			}
		}
	}

	// Generic method to extract string fields from an array of objects
	private static String extractField(Map<String, Object> contact, String key, String subKey) {
		List<Map<String, Object>> list = (List<Map<String, Object>>) contact.get(key);
		if (list != null && !list.isEmpty()) {
			return (String) list.get(0).get(subKey);
		}
		return "N/A";
	}

	// Extract date fields for birthday
	private static String extractDateField(Map<String, Object> contact, String key) {
		List<Map<String, Object>> list = (List<Map<String, Object>>) contact.get(key);
		if (list != null && !list.isEmpty()) {
			Map<String, Object> dateMap = (Map<String, Object>) list.get(0).get("date");
			if (dateMap != null) {
				Double year = (Double) dateMap.get("year"); // Google API sometimes returns numbers as Double
				Double month = (Double) dateMap.get("month");
				Double day = (Double) dateMap.get("day");
				return (year != null ? year.intValue() : "YYYY") + "-" + (month != null ? month.intValue() : "MM") + "-"
						+ (day != null ? day.intValue() : "DD");
			}
		}
		return "N/A";
	}
}
