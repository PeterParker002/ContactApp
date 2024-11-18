package com.contacts.querylayer;

import com.contacts.utils.Order;

public class OrderBy {
	public Column column;
	public Order order;

	public OrderBy(Column col, Order ord) {
		this.column = col;
		this.order = ord;
	}

	public String toString() {
		return "ORDER BY " + this.column.name + " " + this.order.getValue();
	}

}
