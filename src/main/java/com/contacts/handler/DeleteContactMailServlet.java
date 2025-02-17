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



/**
 * Servlet implementation class DeleteContactMailServlet
 */
@WebServlet("/deleteContactMail/*")
public class DeleteContactMailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			HttpSession session = request.getSession();
			int mail_id = Integer.parseInt(request.getPathInfo().substring(1));
			if (ContactDAO.deleteMail(mail_id)) {
				session.setAttribute("message", "Mail Deleted Successfully");
			} else {
				session.setAttribute("message", "Mail Deletion Failed");
			}
		} catch (IllegalArgumentException | ClassNotFoundException | SecurityException | SQLException e) {
			e.printStackTrace();
		}
		response.sendRedirect("/home.jsp");
	}

}
