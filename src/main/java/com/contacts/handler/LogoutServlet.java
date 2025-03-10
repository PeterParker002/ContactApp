package com.contacts.handler;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.cache.SessionCache;
import com.contacts.dao.UserDAO;


import com.contacts.model.Session;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String sessionId = UserDAO.getSessionIdFromCookie(request, "session");
		Session userSession = UserDAO.getUserSession(sessionId);
		request.getSession().invalidate();
		HttpSession s = request.getSession(true);
		for (Cookie c : request.getCookies()) {
			if (c.getName().equals("session")) {
				UserDAO.clearSession(c.getValue());
				System.out.println("Cleared Session from DB");
				SessionCache.activeSessions.remove(c.getValue());
				SessionCache.checkAndUpdateUserCache(userSession);
				System.out.println("Cleared Session from Cache");
				SessionCache.notifySessionRemove(userSession);
				c.setValue("");
				c.setMaxAge(0);
				response.addCookie(c);
				s.setAttribute("message", "User Logged Out Successfully!");
			}
		}
		response.sendRedirect("index.jsp");
	}
}
