package com.contacts.handler;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.dao.ContactDAO;
import com.contacts.dao.UserDAO;


import com.contacts.model.Contact;
import com.contacts.model.Session;
import com.google.gson.Gson;

@WebServlet("/edit-contact")
public class EditContactInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String sessionId = UserDAO.getSessionIdFromCookie(request, "session");
		Session userSession = UserDAO.getUserSession(sessionId);
		int user_id = userSession.getUserId();
		HttpSession session = request.getSession();
		Gson gson = new Gson();
		Contact contact = gson.fromJson(request.getReader(), Contact.class);
		contact.setModifiedAt(System.currentTimeMillis());
		boolean result;
		try {
			result = ContactDAO.editContactInfo(user_id, contact);
			if (result) {
				session.setAttribute("message", "Contact Updated Successfully");
			} else {
				session.setAttribute("message", "Operation Failed");
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		response.sendRedirect("home.jsp");
	}
}
