package com.contacts.handler;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.dao.ContactDAO;
import com.contacts.dao.UserDAO;

import com.contacts.model.Contact;
import com.contacts.model.ContactMail;
import com.contacts.model.ContactMobile;
import com.contacts.model.Session;
import com.contacts.utils.MobileNumberValidator;

@WebServlet("/add-contact")
public class AddContactServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect("/");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		HttpSession session = request.getSession();
		response.setContentType("text/html");
		Contact contact = new Contact();
		long now = System.currentTimeMillis();
		try {
			contact.setFirstName(request.getParameter("fname"));
			contact.setMiddleName(request.getParameter("midname"));
			contact.setLastName(request.getParameter("lname"));
			contact.setGender(request.getParameter("gender"));
			contact.setDateOfBirth(request.getParameter("dob").equals("") ? null : request.getParameter("dob"));
			if (MobileNumberValidator.validate(request.getParameter("mobile"))) {
				ContactMobile mobile = new ContactMobile();
				mobile.setMobileNumber(request.getParameter("mobile"));
				mobile.setCreatedAt(now);
				mobile.setModifiedAt(now);
				contact.setMobileNumber(mobile);
			} else {
				throw new NumberFormatException();
			}
			contact.setNotes(request.getParameter("notes"));
			contact.setHomeAddress(request.getParameter("home"));
			contact.setWorkAddress(request.getParameter("work"));
			String mailId = request.getParameter("email");
			if (!mailId.equals("")) {
				ContactMail mail = new ContactMail();
				mail.setEmail(mailId);
				mail.setCreatedAt(now);
				mail.setModifiedAt(now);
				contact.setEmail(mail);
			}
			contact.setCreatedAt(now);
			contact.setModifiedAt(now);
		} catch (NumberFormatException numException) {
			session.setAttribute("message", "Mobile Number should be a 10 digit number.");
			request.getRequestDispatcher("home.jsp").include(request, response);
		}
		String sessionId = UserDAO.getSessionIdFromCookie(request, "session");
		Session userSession = UserDAO.getUserSession(sessionId);
		int user_id = userSession.getUserId();
		try {
			if (ContactDAO.addContact(user_id, contact)) {
				session.setAttribute("message", "Contact Added Successfully");
				response.sendRedirect("home.jsp");
			} else {
				session.setAttribute("message", "Contact Added Successfully");
				response.sendRedirect("home.jsp");
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
}
