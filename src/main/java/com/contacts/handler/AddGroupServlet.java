package com.contacts.handler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.dao.UserDAO;

@WebServlet("/add-group")
public class AddGroupServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String group_name = request.getParameter("name");
		String[] contacts = request.getParameterValues("contact");

		ArrayList<Integer> contact_ids = new ArrayList<>();
		if (contacts != null) {
			for (String c : contacts) {
				contact_ids.add(Integer.parseInt(c));
			}
		}
		UserDAO userdao = new UserDAO();
		HttpSession session = request.getSession(false);
		int user_id = (int) session.getAttribute("user");
		try {
			if (userdao.addGroup(user_id, group_name, contact_ids)) {
				session.setAttribute("message", "Group Added");
			} else {
				session.setAttribute("message", "Can't Add Group");
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		response.sendRedirect("home.jsp");
	}
}
