package com.contacts.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

@SuppressWarnings("serial")
public class Test {
	public static void main(String[] args) {
		List<String> s = new ArrayList<String>() {
			@Override
			public boolean add(String e) {
				System.out.println("Adding element: " + e);
				return super.add(e);
			}
		};
		s.add("sakthi");
	}
}
