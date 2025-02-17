package com.contacts.handler;

import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.cache.SessionCache;
import com.contacts.dao.ContactDAO;
import com.contacts.dao.UserDAO;


import com.contacts.model.Session;
import com.contacts.model.User;

@WebServlet("/add-email")
public class AddEmailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect("/");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String[] emails = request.getParameterValues("email");
		boolean isUser = false;
		if (request.getParameter("role").equals("user")) {
			isUser = true;
		}
		HttpSession session = request.getSession();
		String sessionId = UserDAO.getSessionIdFromCookie(request, "session");
		Session userSession = UserDAO.getUserSession(sessionId);
		int user_id = userSession.getUserId();
		System.out.println(user_id);
		try {
			if (isUser) {
				if (UserDAO.addEmails(user_id, emails)) {
					User newUser = UserDAO.getUserInfo(user_id);
					SessionCache.userCache.put(user_id, newUser);
					SessionCache.notifyUserUpdate(newUser);
					session.setAttribute("message", "Email Added to Profile");
				}
			} else {
				int contact_id = Integer.parseInt(request.getParameter("contact-id"));
				if (ContactDAO.addEmails(contact_id, emails)) {
					session.setAttribute("message", "Emails Added to Contact");
				}
			}
		} catch (BatchUpdateException e) {
			try {
				throw e.getCause();
			} catch (SQLIntegrityConstraintViolationException se) {
				String mail = se.getMessage().split("'")[1];
				String msgString = "Can't add duplicate email '" + mail + "'";
				session.setAttribute("message", msgString);
			} catch (Throwable e1) {
				e1.printStackTrace();
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		response.sendRedirect("home.jsp");
	}
}
