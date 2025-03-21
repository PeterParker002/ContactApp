package com.contacts.handler;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

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
import com.contacts.model.UserMail;
import com.contacts.model.UserMobile;
import com.contacts.utils.MobileNumberValidator;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		User user = new User();
		long now = System.currentTimeMillis();
		try {
			user.setUsername(request.getParameter("username"));
			user.setFirstName(request.getParameter("fname"));
			user.setMiddleName(request.getParameter("midname"));
			user.setLastName(request.getParameter("lname"));
			user.setGender(request.getParameter("gender"));
			user.setDateOfBirth(request.getParameter("dob").equals("") ? null : request.getParameter("dob"));
			if (MobileNumberValidator.validate(request.getParameter("mobile"))) {
				UserMobile mobile = new UserMobile();				
				mobile.setMobileNumber(request.getParameter("mobile"));
				mobile.setCreatedAt(now);
				mobile.setModifiedAt(now);
				user.setMobileNumber(mobile);
			} else {
				throw new NumberFormatException();
			}
			UserMail mail = new UserMail();
			mail.setEmail(request.getParameter("email"));
			mail.setIsPrimary(true);
			mail.setCreatedAt(now);
			mail.setModifiedAt(now);
			user.setEmail(mail);
			user.setNotes(request.getParameter("notes"));
			user.setHomeAddress(request.getParameter("home"));
			user.setWorkAddress(request.getParameter("work"));
			user.setPassword(request.getParameter("password"));
			user.setCreatedAt(now);
			user.setModifiedAt(now);
		} catch (NumberFormatException numException) {
			session.setAttribute("message", "Enter a valid mobile number.");
			request.getRequestDispatcher("signup.jsp").include(request, response);
		}
		response.setContentType("text/html");
		if (user.getUsername().length() < 1 || user.getEmail().get(0).getEmail().length() < 1
				|| user.getPassword().length() < 6) {
			session.setAttribute("message", "Sign up failed!");
			request.getRequestDispatcher("signup.jsp").include(request, response);
		} else {
			try {
				int user_id = UserDAO.SignupUser(user);
				if (user_id != -1) {
					user.setUserId(user_id);
					session.setAttribute("message", "Sign Up successful!");
					String sessionId = UserDAO.generateSessionId();
					Session s = new Session();
					s.setSessionId(sessionId);
					s.setUserId(user.getUserId());
					s.setCreatedAt(now);
					s.setLastAccessedAt(now);
					UserDAO.createSession(s);
					SessionCache.activeSessions.put(sessionId, s);
					SessionCache.addUserToCache(user.getUserId(), user);
					response.addCookie(new Cookie("session", sessionId));
					response.sendRedirect("home.jsp");
				} else {
					session.setAttribute("message", "Sign Up Failed!");
					request.getRequestDispatcher("signup.jsp").include(request, response);
				}
			} catch (SQLIntegrityConstraintViolationException e) {
				String mail = e.getMessage().split("'")[1];
				String msgString = "Can't add duplicate email '" + mail + "'";
				session.setAttribute("message", msgString);
				e.printStackTrace();
				request.getRequestDispatcher("signup.jsp").include(request, response);
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		request.getRequestDispatcher("/signup.jsp").forward(request, response);
	}
}
