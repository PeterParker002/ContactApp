package com.contacts.handler;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.dao.ContactDAO;



@WebServlet("/deleteContactMobile/*")
public class DeleteContactMobileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			HttpSession session = request.getSession();
			int mobile_id = Integer.parseInt(request.getPathInfo().substring(1));
			if (ContactDAO.deleteMobileNumber(mobile_id)) {
				session.setAttribute("message", "Mobile Number Deleted Successfully");
			} else {
				session.setAttribute("message", "Mobile Number Deletion Failed");
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		response.sendRedirect("/home.jsp");
	}
}
