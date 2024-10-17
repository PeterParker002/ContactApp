package com.contacts.handler;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.dao.ContactDAO;

@WebServlet("/delete-group-contact")
public class DeleteGroupContactServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int group_id = Integer.parseInt(request.getParameter("group-id"));
		int contact_id = Integer.parseInt(request.getParameter("contact-id"));
		HttpSession session = request.getSession();
		ContactDAO c = new ContactDAO();
		try {
			if (c.deleteGroupContact(group_id, contact_id)) {
				session.setAttribute("message", "Contact Removed From the group Successfully");
			} else {
				session.setAttribute("message", "Can't Remove Contact From the group");
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		response.sendRedirect("/home.jsp");
	}
}
