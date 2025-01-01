package com.contacts.handler;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.dao.UserDAO;
import com.contacts.logger.MyCustomLogger;

@WebServlet("/deleteMobile/*")
public class DeleteUserMobileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final MyCustomLogger logger = new MyCustomLogger(
			"/home/karthik-tt0479/eclipse-workspace/FirstProject/src/main/resources/logs/application.log",
			MyCustomLogger.LogLevel.INFO);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			HttpSession session = request.getSession();
			int mobile_id = Integer.parseInt(request.getPathInfo().substring(1));
			UserDAO userdao = new UserDAO();
			if (userdao.deleteMobileNumber(mobile_id)) {
				logger.info("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"Mobile Number Deleted Successfully.");
				session.setAttribute("message", "Mobile Number Deleted Successfully");
			} else {
				logger.info("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"Mobile Number Deletion Failed.");
				session.setAttribute("message", "Mobile Number Deletion Failed");
			}
		} catch (ClassNotFoundException | SQLException e) {
			logger.error("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(), e.getMessage());
			e.printStackTrace();
		}
		response.sendRedirect("/home.jsp");
	}
}
