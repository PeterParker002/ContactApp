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
import com.contacts.model.User;

@WebServlet("/makePrimary/*")
public class ChangePrimaryMailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final MyCustomLogger logger = new MyCustomLogger(
			"/home/karthik-tt0479/eclipse-workspace/FirstProject/src/main/resources/logs/application.log",
			MyCustomLogger.LogLevel.INFO);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			UserDAO userdao = new UserDAO();
			String sessionId = userdao.getSessionIdFromCookie(request, "session");
			Session userSession = userdao.getUserSession(sessionId);
			int user_id = userSession.getUserId();
			HttpSession session = request.getSession();
			int mail_id = Integer.parseInt(request.getPathInfo().substring(1));
			if (userdao.changePrimaryMail(user_id, mail_id)) {
				logger.info("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"User Primary Mail Changed Successfully.");
				User newUser = userdao.getUserInfo(user_id);
				SessionCache.userCache.put(user_id, newUser);
				SessionCache.notifyUserUpdate(newUser);
				session.setAttribute("message", "Primary Mail Changed");
			} else {
				session.setAttribute("message", "Primary Mail Change Failed");
			}
		} catch (ClassNotFoundException | SQLException | IllegalArgumentException | SecurityException e) {
			logger.error("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(), e.getMessage());
			e.printStackTrace();
		}
		response.sendRedirect("/home.jsp");
	}
}
