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

import com.contacts.cache.SessionCache;
import com.contacts.dao.ContactDAO;
import com.contacts.dao.UserDAO;
import com.contacts.logger.MyCustomLogger;
import com.contacts.model.Session;
import com.contacts.model.User;

@WebServlet("/add-email")
public class AddEmailServlet extends HttpServlet {
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
		String[] emails = request.getParameterValues("email");
		boolean isUser = false;
		if (request.getParameter("role").equals("user")) {
			isUser = true;
		}
		UserDAO userdao = new UserDAO();
		ContactDAO contactdao = new ContactDAO();
		HttpSession session = request.getSession();
		String sessionId = userdao.getSessionIdFromCookie(request, "session");
		Session userSession = userdao.getUserSession(sessionId);
		int user_id = userSession.getUserId();
		System.out.println(user_id);
		try {
			if (isUser) {
				if (userdao.addEmails(user_id, emails)) {
					logger.info("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
							"User Emails Added Successfully.");
					User newUser = userdao.getUserInfo(user_id);
					SessionCache.userCache.put(user_id, newUser);
					SessionCache.notifyUserUpdate(newUser);
					session.setAttribute("message", "Email Added to Profile");
				}
			} else {
				int contact_id = Integer.parseInt(request.getParameter("contact-id"));
				if (contactdao.addEmails(contact_id, emails)) {
					logger.info("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
							"Contact Mails Added Successfully.");
					session.setAttribute("message", "Emails Added to Contact");
				}
			}
		} catch (BatchUpdateException e) {
			try {
				throw e.getCause();
			} catch (SQLIntegrityConstraintViolationException se) {
				String mail = se.getMessage().split("'")[1];
				String msgString = "Can't add duplicate email '" + mail + "'";
				session.setAttribute("message", msgString);
				logger.error("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						se.getMessage());
			} catch (Throwable e1) {
				logger.error("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						e1.getMessage());
				e1.printStackTrace();
			}
		} catch (ClassNotFoundException | SQLException e) {
			logger.error("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
					e.getMessage());
			e.printStackTrace();
		}
		response.sendRedirect("home.jsp");
	}
}
