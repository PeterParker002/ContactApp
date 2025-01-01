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
import com.contacts.logger.MyCustomLogger;
import com.contacts.model.User;

@WebServlet("/add-mobile-number")
public class AddMobileNumberServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final MyCustomLogger logger = new MyCustomLogger(
			"/home/karthik-tt0479/eclipse-workspace/FirstProject/src/main/resources/logs/application.log",
			MyCustomLogger.LogLevel.INFO);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		logger.info("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
				"Redirecting to home page (/).");
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
				logger.info("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"User Mobile Numbers Added Successfully.");
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
				logger.error("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						se.getMessage());
			} catch (Throwable e1) {
				logger.error("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						e1.getMessage());
				e1.printStackTrace();
			}
		} catch (ClassNotFoundException | SQLException n) {
			logger.error("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
					n.getMessage());
			n.printStackTrace();
		}
		response.sendRedirect("home.jsp");
	}
}
