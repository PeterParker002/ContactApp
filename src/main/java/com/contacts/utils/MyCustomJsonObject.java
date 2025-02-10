package com.contacts.utils;

import java.util.HashMap;
import java.util.Map;

public class MyCustomJsonObject<K, V> extends HashMap<K, V> {

	@Override
	public String toString() {
		StringBuilder jsonBuilder = new StringBuilder("{");
		for (Map.Entry<K, V> entry : this.entrySet()) {
			jsonBuilder.append("\"").append(entry.getKey()).append("\": ");
			if (entry.getValue() instanceof String) {
				jsonBuilder.append("\"").append(entry.getValue()).append("\"");
			} else {
				jsonBuilder.append(entry.getValue());
			}
			jsonBuilder.append(", ");
		}
		if (!this.isEmpty()) {
			jsonBuilder.setLength(jsonBuilder.length() - 2);
		}
		jsonBuilder.append("}");
		return jsonBuilder.toString();
	}
}