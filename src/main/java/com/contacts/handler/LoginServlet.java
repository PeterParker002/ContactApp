package com.contacts.handler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.cache.SessionCache;
import com.contacts.dao.UserDAO;


import com.contacts.model.Session;
import com.contacts.model.User;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("login.jsp").forward(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		HttpSession session = request.getSession(true);
		response.setContentType("text/html");
		try {
			User user = UserDAO.LoginUser(email, password);
			if (user != null) {
				long now = System.currentTimeMillis();
				String sessionId = UserDAO.generateSessionId();
				Session s = new Session();
				s.setSessionId(sessionId);
				s.setUserId(user.getUserId());
				s.setCreatedAt(now);
				s.setLastAccessedAt(now);
				UserDAO.createSession(s);
				SessionCache.activeSessionObjects.put(sessionId, s);
				SessionCache.addUserToCache(user.getUserId(), user);
				Cookie cookie = new Cookie("session", sessionId);
				response.addCookie(cookie);
				session.setAttribute("message", "User Login Successful!");
				response.sendRedirect("home.jsp");
			} else {
				session.setAttribute("message", "User Login Failed!");
				request.getRequestDispatcher("login.jsp").include(request, response);
			}
		} catch (ClassNotFoundException | SQLException | IllegalAccessException | InvocationTargetException
				| InstantiationException | IllegalArgumentException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
}