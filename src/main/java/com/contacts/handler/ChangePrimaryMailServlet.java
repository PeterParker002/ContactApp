package com.contacts.handler;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.cache.SessionCache;
import com.contacts.dao.UserDAO;


import com.contacts.model.Session;
import com.contacts.model.User;

@WebServlet("/makePrimary/*")
public class ChangePrimaryMailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			String sessionId = UserDAO.getSessionIdFromCookie(request, "session");
			Session userSession = UserDAO.getUserSession(sessionId);
			int user_id = userSession.getUserId();
			HttpSession session = request.getSession();
			int mail_id = Integer.parseInt(request.getPathInfo().substring(1));
			if (UserDAO.changePrimaryMail(user_id, mail_id)) {
				User newUser = UserDAO.getUserInfo(user_id);
				SessionCache.userCache.put(user_id, newUser);
				SessionCache.notifyUserUpdate(newUser);
				session.setAttribute("message", "Primary Mail Changed");
			} else {
				session.setAttribute("message", "Primary Mail Change Failed");
			}
		} catch (ClassNotFoundException | SQLException | IllegalArgumentException | SecurityException e) {
			e.printStackTrace();
		}
		response.sendRedirect("/home.jsp");
	}
}
