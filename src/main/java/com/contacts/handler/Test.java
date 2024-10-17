package com.contacts.handler;

import com.lambdaworks.crypto.SCryptUtil;

public class Test {
	public static void main(String[] args) {
		int N = 32; // CPU cost
		int r = 20; // Memory Cost
		int p = 2; // Parallelization
		String hashed = SCryptUtil.scrypt("121212", N, r, p);
		System.out.println(SCryptUtil.check("121212", hashed));
	}
}
