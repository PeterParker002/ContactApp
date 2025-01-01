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

@WebServlet("/delete-group-contact")
public class DeleteGroupContactServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final MyCustomLogger logger = new MyCustomLogger(
			"/home/karthik-tt0479/eclipse-workspace/FirstProject/src/main/resources/logs/application.log",
			MyCustomLogger.LogLevel.INFO);

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int group_id = Integer.parseInt(request.getParameter("group-id"));
		int contact_id = Integer.parseInt(request.getParameter("contact-id"));
		HttpSession session = request.getSession();
		ContactDAO c = new ContactDAO();
		try {
			if (c.deleteGroupContact(group_id, contact_id)) {
				logger.info("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"Contact Removed from the group Successfully.");
				session.setAttribute("message", "Contact Removed From the group Successfully");
			} else {
				logger.info("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"Can't Remove Contact from the group.");
				session.setAttribute("message", "Can't Remove Contact From the group");
			}
		} catch (ClassNotFoundException | SQLException e) {
			logger.error("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
					e.getMessage());
			e.printStackTrace();
		}
		response.sendRedirect("/home.jsp");
	}
}
