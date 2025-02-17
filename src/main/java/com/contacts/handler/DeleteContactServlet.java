package com.contacts.handler;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.dao.ContactDAO;



@WebServlet("/deleteContact/*")
public class DeleteContactServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		int contact_id = Integer.parseInt(request.getPathInfo().substring(1));
		try {
			boolean result = ContactDAO.deleteContact(contact_id);
			HttpSession session = request.getSession();
			if (result) {
				session.setAttribute("message", "Contact Deleted Successfully");
			} else {
				session.setAttribute("message", "Contact Deletion Failed");
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		response.sendRedirect("/home.jsp");
	}
}
