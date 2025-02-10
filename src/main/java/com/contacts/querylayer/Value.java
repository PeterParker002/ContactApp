package com.contacts.querylayer;

public class Value<V> {
	public V value;

	public Value(V val) {
		this.value = val;
	}

//	public String toString() {
//		if (this.value instanceof Condition) {
//			return "(" + this.value.toString() + ")";
//		}
//		return this.value + "";
//	}
}
