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
import com.contacts.logger.MyCustomLogger;
import com.contacts.model.Contact;
import com.contacts.model.Session;
import com.contacts.model.User;

@WebServlet("/edit-contact")
public class EditContactInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final MyCustomLogger logger = new MyCustomLogger(
			"/home/karthik-tt0479/eclipse-workspace/FirstProject/src/main/resources/logs/application.log",
			MyCustomLogger.LogLevel.INFO);

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		UserDAO userdao = new UserDAO();
		ContactDAO contactdao = new ContactDAO();
		String sessionId = userdao.getSessionIdFromCookie(request, "session");
		Session userSession = userdao.getUserSession(sessionId);
		int user_id = userSession.getUserId();
		HttpSession session = request.getSession();
		Contact contact = new Contact();
		int contactId = Integer.parseInt(request.getParameter("contact-id"));
		contact.setContactId(contactId);
		contact.setFirstName(request.getParameter("fname"));
		contact.setMiddleName(request.getParameter("midname"));
		contact.setLastName(request.getParameter("lname"));
		contact.setGender(request.getParameter("gender"));
		contact.setDateOfBirth(request.getParameter("dob"));
		contact.setNotes(request.getParameter("notes"));
		contact.setHomeAddress(request.getParameter("home"));
		contact.setWorkAddress(request.getParameter("work"));
		boolean result;
		try {
			result = contactdao.editContactInfo(user_id, contact);
			if (result) {
				logger.info("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"Contact Updated Successfully");
				session.setAttribute("message", "Contact Updated Successfully");
			} else {
				logger.info("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"Contact Updation Failed");
				session.setAttribute("message", "Operation Failed");
			}
		} catch (ClassNotFoundException | SQLException e) {
			logger.error("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
					e.getMessage());
			e.printStackTrace();
		}
		response.sendRedirect("home.jsp");
	}
}