package com.contacts.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.dao.ContactDAO;
import com.contacts.dao.UserDAO;
import com.contacts.model.Contact;
import com.contacts.model.Session;
import com.contacts.utils.GoogleJSONParser;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String sessionId = UserDAO.getSessionIdFromCookie(request, "session");
		Session userSession = UserDAO.getUserSession(sessionId);
		int user_id = userSession.getUserId();
		String userInfo = (String) request.getSession().getAttribute("user");
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		long now = System.currentTimeMillis();
		boolean isSyncSuccess = true;
		if (userInfo != null) {
			List<Map<String, Object>> result = GoogleJSONParser.parse(userInfo);
			for (Map<String, Object> entry : result) {
				try {
					Contact contact = new Contact();
					contact.setFirstName((String) entry.get("name"));
					contact.setDateOfBirth((String) entry.get("dateOfBirth"));
					if (entry.get("mobile") != null) contact.setMobileNumber((String) entry.get("mobile"));
					if (entry.get("email") != null) contact.setEmail((String) entry.get("email"));
					contact.setCreatedAt(now);
					contact.setModifiedAt(now);
					if (!ContactDAO.addContact(user_id, contact)) {
						System.out.println("Error while adding contact:" + contact.getFirstName());
						isSyncSuccess = false;
						break;
					}
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				}
			}
			if (isSyncSuccess) {
				session.setAttribute("message", "Contacts Sync Success!");
			} else {
				session.setAttribute("message", "Contacts Sync Failed!");
			}
			response.sendRedirect("home.jsp");
		} else {
			out.println("{ \"message\": \"No user information available.\" }");
		}
	}
}
