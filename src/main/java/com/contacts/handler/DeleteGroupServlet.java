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

@WebServlet("/delete-group")
public class DeleteGroupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final MyCustomLogger logger = new MyCustomLogger(
			"/home/karthik-tt0479/eclipse-workspace/FirstProject/src/main/resources/logs/application.log",
			MyCustomLogger.LogLevel.INFO);

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int group_id = Integer.parseInt(request.getParameter("group-id"));
		HttpSession session = request.getSession();
		ContactDAO c = new ContactDAO();
		try {
			if (c.deleteGroup(group_id)) {
				logger.info("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"Group Deleted Successful.");
				session.setAttribute("message", "Group Deleted Successfully");
			} else {
				logger.info("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"Can't Delete the Group");
				session.setAttribute("message", "Can't delete group");
			}
		} catch (ClassNotFoundException | SQLException e) {
			logger.error("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
					e.getMessage());
			e.printStackTrace();
		}
		response.sendRedirect("home.jsp");
	}
}
