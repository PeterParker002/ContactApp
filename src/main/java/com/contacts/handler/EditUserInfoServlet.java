package com.contacts.handler;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.dao.UserDAO;
import com.contacts.model.User;

@WebServlet("/edit-profile")
public class EditUserInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		int user_id = (int) session.getAttribute("user");
		User user = new User();
		user.setFirstName(request.getParameter("fname"));
		user.setMiddleName(request.getParameter("midname"));
		user.setLastName(request.getParameter("lname"));
		user.setGender(request.getParameter("gender"));
		user.setDateOfBirth(request.getParameter("dob"));
		user.setNotes(request.getParameter("notes"));
		user.setHomeAddress(request.getParameter("home"));
		user.setWorkAddress(request.getParameter("work"));
		UserDAO userdao = new UserDAO();
		boolean result;
		try {
			result = userdao.EditUserInfo(user_id, user);
			if (result) {
				session.setAttribute("user", user);
				session.setAttribute("message", "User Profile Updated Successfully");
			} else {
				session.setAttribute("message", "Operation Failed");
			}
			response.sendRedirect("home.jsp");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
}
