package com.contacts.handler;

import java.util.ArrayList;

import com.contacts.querylayer.Column;
import com.contacts.querylayer.QueryBuilder;

public class Test {
	public static void main(String[] args) {
		QueryBuilder ql = new QueryBuilder();
		ql.selectTable("User_email_ids");
		ArrayList<Column> cols = new ArrayList<>();
		ql.selectColumn("a", "as");
		System.out.println(ql.build());
	}
}
