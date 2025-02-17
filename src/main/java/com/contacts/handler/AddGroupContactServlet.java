package com.contacts.handler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.dao.ContactDAO;
import com.contacts.dao.UserDAO;



@WebServlet("/add-group-contact")
public class AddGroupContactServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int group_id = Integer.parseInt(request.getParameter("group-id"));
		String[] contacts = request.getParameterValues("contact");

		ArrayList<Integer> contact_ids = new ArrayList<>();
		if (contacts != null) {
			for (String c : contacts) {
				contact_ids.add(Integer.parseInt(c));
			}
		}
		HttpSession session = request.getSession(false);
		try {
			if (UserDAO.addGroupContact(group_id, contact_ids)) {
				session.setAttribute("message",
						"Added Contact to the group '" + ContactDAO.getGroupNameById(group_id) + "'");
			} else {
				session.setAttribute("message", "No Contacts Left to Add.");
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		response.sendRedirect("home.jsp");
	}
}
