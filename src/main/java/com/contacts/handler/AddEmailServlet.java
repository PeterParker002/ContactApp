package com.contacts.handler;

import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.dao.UserDAO;
import com.contacts.model.User;

@WebServlet("/add-email")
public class AddEmailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect("/");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String[] emails = request.getParameterValues("email");
		UserDAO userdao = new UserDAO();
		HttpSession session = request.getSession();
		int user_id = (int) session.getAttribute("user");
		try {
			if (userdao.addEmails(user_id, emails)) {
				session.setAttribute("message", "Email Added");
			}
		} catch (BatchUpdateException e) {
			try {
				throw e.getCause();
			} catch (SQLIntegrityConstraintViolationException se) {
				String mail = se.getMessage().split("'")[1];
				String msgString = "Can't add duplicate email '" + mail + "'";
				session.setAttribute("message", msgString);
			} catch (Throwable e1) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		response.sendRedirect("home.jsp");
	}
}
