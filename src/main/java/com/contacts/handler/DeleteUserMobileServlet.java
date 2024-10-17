package com.contacts.handler;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.dao.UserDAO;

@WebServlet("/deleteMobile/*")
public class DeleteUserMobileServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			HttpSession session = request.getSession();
			int mobile_id = Integer.parseInt(request.getPathInfo().substring(1));
			UserDAO userdao = new UserDAO();
			if (userdao.deleteMobileNumber(mobile_id)) {
				session.setAttribute("message", "Mobile Number Deleted Successfully");
			} else {
				session.setAttribute("message", "Mobile Number Deletion Failed");
			}
		} catch (NumberFormatException e) {

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		response.sendRedirect("/home.jsp");
	}
}