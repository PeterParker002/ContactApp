package com.contacts.handler;

import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet("/add-mobile-number")
public class AddMobileNumberServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect("/");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String mobileNumberString[] = request.getParameterValues("mobile");
		Long mobileNumbers[] = new Long[mobileNumberString.length];
		for (int i = 0; i < mobileNumberString.length; i++) {
			mobileNumbers[i] = Long.parseLong(mobileNumberString[i]);
		}
		response.setContentType("text/html");
		UserDAO userdao = new UserDAO();
		HttpSession session = request.getSession();
		int user_id = (int) session.getAttribute("user");
		try {
			if (userdao.addMobileNumbers(user_id, mobileNumbers)) {
				session.setAttribute("message", "Mobile Numbers Added Successfully");
			} else {
				session.setAttribute("message", "Mobile Numbers Addition Failed");
			}
		} catch (BatchUpdateException e) {
			try {
				throw e.getCause();
			} catch (SQLIntegrityConstraintViolationException se) {
				String mail = se.getMessage().split("'")[1];
				String msgString = "Can't add duplicate mobile number '" + mail + "'";
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