package com.contacts.handler;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.dao.ContactDAO;
import com.contacts.logger.MyCustomLogger;

@WebServlet("/deleteContact/*")
public class DeleteContactServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final MyCustomLogger logger = new MyCustomLogger(
			"/home/karthik-tt0479/eclipse-workspace/FirstProject/src/main/resources/logs/application.log",
			MyCustomLogger.LogLevel.INFO);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		int contact_id = Integer.parseInt(request.getPathInfo().substring(1));
		ContactDAO c = new ContactDAO();
		try {
			boolean result = c.deleteContact(contact_id);
			HttpSession session = request.getSession();
			if (result) {
				logger.info("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"Contact Deleted Successfully.");
				session.setAttribute("message", "Contact Deleted Successfully");
			} else {
				logger.info("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"Contact Deletion Failed.");
				session.setAttribute("message", "Contact Deletion Failed");
			}
		} catch (ClassNotFoundException | SQLException e) {
			logger.error("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(), e.getMessage());
		}
		response.sendRedirect("/home.jsp");
	}
}
