package com.contacts.handler;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.dao.ContactDAO;

@WebServlet("/delete-group")
public class DeleteGroupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int group_id = Integer.parseInt(request.getParameter("group-id"));
		HttpSession session = request.getSession();
		ContactDAO c = new ContactDAO();
		try {
			if (c.deleteGroup(group_id)) {
				session.setAttribute("message", "Group Deleted Successfully");
			} else {
				session.setAttribute("message", "Can't delete group");
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		response.sendRedirect("home.jsp");
	}
}
