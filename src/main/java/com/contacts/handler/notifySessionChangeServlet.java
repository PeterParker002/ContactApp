package com.contacts.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.contacts.cache.SessionCache;
import com.contacts.dao.UserDAO;
import com.contacts.model.Session;
import com.contacts.model.User;
import com.google.gson.Gson;

/**
 * Servlet implementation class notifySessionChangeServlet
 */
@WebServlet("/notifySessionChange")
public class notifySessionChangeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public notifySessionChangeServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Gson gson = new Gson();
		Session session = gson.fromJson(request.getReader(), Session.class);
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie c : cookies) {
				if (c.getName().equals("session")) {
					String sessionId = c.getValue();
					if (SessionCache.activeSessionObjects.containsKey(sessionId)) {
						SessionCache.activeSessionObjects.put(sessionId, session);
					}
				}
			}
		}
//		else {
//			SessionCache.activeSessionObjects.put(session.getSessionId(), session);
//			UserDAO u = new UserDAO();
//			User user = u.getUserInfo(session.getUserId());
//			SessionCache.addUserToCache(user.getUserId(), user);
//		}
	}

}
