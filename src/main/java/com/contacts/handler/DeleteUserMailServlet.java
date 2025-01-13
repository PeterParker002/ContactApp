package com.contacts.handler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.cache.SessionCache;
import com.contacts.dao.UserDAO;
import com.contacts.logger.MyCustomLogger;
import com.contacts.model.Session;

@WebServlet("/deleteMail/*")
public class DeleteUserMailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final MyCustomLogger logger = new MyCustomLogger(
			"/home/karthik-tt0479/eclipse-workspace/FirstProject/src/main/resources/logs/application.log",
			MyCustomLogger.LogLevel.INFO);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			UserDAO userdao = new UserDAO();
			HttpSession session = request.getSession();
			String sessionId = userdao.getSessionIdFromCookie(request, "session");
			Session userSession = userdao.getUserSession(sessionId);
			int user_id = userSession.getUserId();
			int mail_id = Integer.parseInt(request.getPathInfo().substring(1));
			if (userdao.checkIsPrimaryMail(mail_id)) {
				logger.info("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"Can't Delete Primary Mail");
				session.setAttribute("message", "Can't Delete Primary Mail");
			} else {
				if (userdao.deleteMail(mail_id)) {
					logger.info("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
							"Mail Deleted Successfully.");
					SessionCache.userCache.put(user_id, userdao.getUserInfo(user_id));
					session.setAttribute("message", "Mail Deleted Successfully");
				} else {
					logger.info("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
							"Mail Deletion Failed.");
					session.setAttribute("message", "Mail Deletion Failed");
				}
			}
		} catch (IllegalArgumentException | ClassNotFoundException | IllegalAccessException | InvocationTargetException
				| InstantiationException | NoSuchMethodException | SecurityException | SQLException e) {
			logger.error("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
					e.getMessage());
		}
		response.sendRedirect("/home.jsp");
	}
}
