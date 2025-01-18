package com.contacts.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.contacts.cache.SessionCache;
import com.contacts.model.User;
import com.google.gson.Gson;

/**
 * Servlet implementation class NotifyUserUpdateServlet
 */
@WebServlet("/notifyUserUpdate")
public class NotifyUserUpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public NotifyUserUpdateServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Gson gson = new Gson();
		User user = gson.fromJson(request.getReader(), User.class);

		System.out.println("Updating User Cache");
		if (SessionCache.userCache.containsKey(user.getUserId())) {
			SessionCache.userCache.put(user.getUserId(), user);
		}
	}
}
