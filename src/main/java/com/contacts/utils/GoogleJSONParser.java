package com.contacts.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

@SuppressWarnings("unchecked")
public class GoogleJSONParser {
	public static List<Map<String, Object>> parse(String userInfo) {
		Gson gson = new Gson();
		Map<String, Object> result = gson.fromJson(userInfo, Map.class);
		List<Map<String, Object>> fullData = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> connections = (List<Map<String, Object>>) result.get("connections");
		if (connections != null && !connections.isEmpty()) {
			for (Map<String, Object> contact : connections) {
				Map<String, Object> parsedData = new HashMap<String, Object>();
				String name = extractField(contact, "names", "displayName");
				String phone = extractField(contact, "phoneNumbers", "canonicalForm") == null
						? extractField(contact, "phoneNumbers", "value")
						: extractField(contact, "phoneNumbers", "canonicalForm");
				String email = extractField(contact, "emailAddresses", "value");
				String birthday = extractDateField(contact, "birthdays");

				parsedData.put("name", name);
				parsedData.put("mobile", phone);
				parsedData.put("email", email);
				parsedData.put("dateOfBirth", birthday);
				fullData.add(parsedData);
				System.out.println("Name: " + name);
				System.out.println("Phone: " + phone);
				System.out.println("Email: " + email);
				System.out.println("Birthday: " + birthday);
				System.out.println("---------------------");
			}
		}
		return fullData;
	}

	private static String extractField(Map<String, Object> contact, String key, String subKey) {
		List<Map<String, Object>> list = (List<Map<String, Object>>) contact.get(key);
		if (list != null && !list.isEmpty()) {
			return (String) list.get(0).get(subKey);
		}
		return null;
	}

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
		return null;
	}
}
