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
import com.contacts.logger.MyCustomLogger;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final MyCustomLogger logger = new MyCustomLogger(
			"/home/karthik-tt0479/eclipse-workspace/FirstProject/src/main/resources/logs/application.log",
			MyCustomLogger.LogLevel.INFO);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		UserDAO u = new UserDAO();
		request.getSession().invalidate();
		HttpSession s = request.getSession(true);
		for (Cookie c : request.getCookies()) {
			if (c.getName().equals("session")) {
				u.clearSession(c.getValue());
				System.out.println("Cleared Session from DB");
				SessionCache.activeSessions.remove(c.getValue());
				System.out.println("Cleared Session from Cache");
				c.setValue("");
				c.setMaxAge(0);
				response.addCookie(c);
				s.setAttribute("message", "User Logged Out Successfully!");
			}
		}
		logger.info("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
				"User Logout Successful.");
		response.sendRedirect("index.jsp");
	}
}
