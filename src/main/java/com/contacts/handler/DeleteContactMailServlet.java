package com.contacts.handler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.cache.SessionCache;
import com.contacts.dao.ContactDAO;
import com.contacts.dao.UserDAO;
import com.contacts.logger.MyCustomLogger;
import com.contacts.model.Session;

/**
 * Servlet implementation class DeleteContactMailServlet
 */
@WebServlet("/deleteContactMail/*")
public class DeleteContactMailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final MyCustomLogger logger = new MyCustomLogger(
			"/home/karthik-tt0479/eclipse-workspace/FirstProject/src/main/resources/logs/application.log",
			MyCustomLogger.LogLevel.INFO);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteContactMailServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			ContactDAO contactdao = new ContactDAO();
			HttpSession session = request.getSession();
			int mail_id = Integer.parseInt(request.getPathInfo().substring(1));
			if (contactdao.deleteMail(mail_id)) {
				logger.info("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"Contact Mail Deleted Successfully.");
				session.setAttribute("message", "Mail Deleted Successfully");
			} else {
				logger.info("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"Contact Mail Deletion Failed.");
				session.setAttribute("message", "Mail Deletion Failed");
			}
		} catch (IllegalArgumentException | ClassNotFoundException | SecurityException | SQLException e) {
			logger.error("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(), e.getMessage());
		}
		response.sendRedirect("/home.jsp");
	}

}
