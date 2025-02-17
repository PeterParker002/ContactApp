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
import com.contacts.utils.MobileNumberValidator;

@WebServlet("/add-mobile-number")
public class AddMobileNumberServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect("/");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String mobileNumberString[] = request.getParameterValues("mobile");
		String mobileNumbers[] = new String[mobileNumberString.length];
		int i = 0;
		for (String mobile : mobileNumberString) {
			if (MobileNumberValidator.validate(mobile)) {
				mobileNumbers[i] = mobileNumberString[i];
				i++;
			}
		}
		response.setContentType("text/html");
		boolean isUser = false;
		if (request.getParameter("role").equals("user")) {
			isUser = true;
		}
		HttpSession session = request.getSession();
		String sessionId = UserDAO.getSessionIdFromCookie(request, "session");
		Session userSession = UserDAO.getUserSession(sessionId);
		int user_id = userSession.getUserId();
		try {
			if (isUser) {
				if (UserDAO.addMobileNumbers(user_id, mobileNumbers)) {
					User newUser = UserDAO.getUserInfo(user_id);
					SessionCache.userCache.put(user_id, newUser);
					SessionCache.notifyUserUpdate(newUser);
					session.setAttribute("message", "Mobile Numbers Added Successfully");
				} else {
					session.setAttribute("message", "Mobile Numbers Addition Failed");
				}
			} else {
				int contact_id = Integer.parseInt(request.getParameter("contact-id"));
				if (ContactDAO.addMobileNumbers(contact_id, mobileNumbers)) {
					session.setAttribute("message", "Contact Numbers Added Successfully");
				} else {
					session.setAttribute("message", "Contact Numbers Addition Failed");
				}
			}
		} catch (BatchUpdateException e) {
			try {
				throw e.getCause();
			} catch (SQLIntegrityConstraintViolationException se) {
				String mail = se.getMessage().split("'")[1];
				String msgString = "Can't add duplicate mobile number '" + mail + "' to the Contact";
				session.setAttribute("message", msgString);
			} catch (Throwable e1) {
				e1.printStackTrace();
			}
		} catch (ClassNotFoundException | SQLException n) {
			n.printStackTrace();
		}
		response.sendRedirect("home.jsp");
	}
}
