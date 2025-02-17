package com.contacts.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.contacts.cache.SessionCache;
import com.contacts.model.User;
import com.google.gson.Gson;

@WebServlet("/notifyUserUpdate")
public class NotifyUserUpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
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
