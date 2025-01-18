package com.contacts.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.cache.SessionCache;
import com.contacts.dao.UserDAO;
import com.contacts.logger.MyCustomLogger;
import com.contacts.model.Server;
import com.contacts.model.Session;
import com.contacts.model.User;
import com.contacts.notifier.Notifier;
import com.google.gson.Gson;

@WebServlet("/edit-profile")
public class EditUserInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final MyCustomLogger logger = new MyCustomLogger(
			"/home/karthik-tt0479/eclipse-workspace/FirstProject/src/main/resources/logs/application.log",
			MyCustomLogger.LogLevel.INFO);

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		UserDAO userdao = new UserDAO();
		String sessionId = userdao.getSessionIdFromCookie(request, "session");
		Session userSession = userdao.getUserSession(sessionId);
		int user_id = userSession.getUserId();
		HttpSession session = request.getSession();
		User user = SessionCache.userCache.get(user_id);
		user.setFirstName(request.getParameter("fname"));
		user.setMiddleName(request.getParameter("midname"));
		user.setLastName(request.getParameter("lname"));
		user.setGender(request.getParameter("gender"));
		user.setDateOfBirth(request.getParameter("dob"));
		user.setNotes(request.getParameter("notes"));
		user.setHomeAddress(request.getParameter("home"));
		user.setWorkAddress(request.getParameter("work"));
		boolean result;
		try {
			result = userdao.editUserInfo(user_id, user);
			if (result) {
				session.setAttribute("user", user);
				logger.info("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"User Profile Updated Successfully");
				SessionCache.userCache.put(user_id, user);
				SessionCache.notifyUserUpdate(user);
				session.setAttribute("message", "User Profile Updated Successfully");
			} else {
				logger.info("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"User Profile Updation Failed");
				session.setAttribute("message", "Operation Failed");
			}
		} catch (ClassNotFoundException | SQLException | IllegalArgumentException | SecurityException e) {
			logger.error("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
					e.getMessage());
			e.printStackTrace();
		}
		response.sendRedirect("home.jsp");
	}
}
