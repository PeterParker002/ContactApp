package com.contacts.handler;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.dao.UserDAO;

@WebServlet("/makePrimary/*")
public class ChangePrimaryMailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			HttpSession session = request.getSession();
			int user_id = (int) session.getAttribute("user");
			int mail_id = Integer.parseInt(request.getPathInfo().substring(1));
			UserDAO userdao = new UserDAO();
			if (userdao.changePrimaryMail(user_id, mail_id)) {
				session.setAttribute("message", "Primary Mail Changed");
			} else {
				session.setAttribute("message", "Primary Mail Change Failed");
			}
		} catch (NumberFormatException e) {

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		response.sendRedirect("/home.jsp");
	}
}
