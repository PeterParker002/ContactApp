package com.contacts.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.contacts.cache.SessionCache;
import com.contacts.dao.UserDAO;
import com.contacts.logger.MyCustomLogger;
import com.contacts.logger.MyCustomLogger.LogLevel;
import com.contacts.model.Session;
import com.contacts.model.User;
import com.contacts.schedulers.SessionScheduler;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
//	private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
	private static final MyCustomLogger logger = new MyCustomLogger(
			"/home/karthik-tt0479/eclipse-workspace/FirstProject/src/main/resources/logs/application.log",
			MyCustomLogger.LogLevel.INFO);

//	127.0.0.1 - - [25/Dec/2024:12:34:56 +0000] "GET /index.html HTTP/1.1" 200 1024\n Message: ---
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
				"Redirecting to login.jsp page.");
		request.getRequestDispatcher("login.jsp").forward(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		UserDAO userDAO = new UserDAO();
		try {
			User user = userDAO.LoginUser(email, password);
			if (user != null) {
				HttpSession session = request.getSession(true);
				String now = LocalDateTime.now().toString();
				String sessionId = userDAO.generateSessionId();
				Session s = new Session();
				s.setSessionId(sessionId);
				s.setUserId(user.getUserId());
				s.setCreatedAt(now);
				s.setLastAccessedAt(now);
				userDAO.createSession(s);
				SessionCache.activeSessionObjects.put(sessionId, s);
				SessionCache.addUserToCache(user.getUserId(), user);
				Cookie cookie = new Cookie("session", sessionId);
				response.addCookie(cookie);
				session.setAttribute("user", user.getUserId());
				session.setAttribute("message", "User Login Successful!");
				logger.info("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"User Login Successful.");
				response.sendRedirect("home.jsp");
			} else {
				out.println("<div class='message'>User Login Failed!</div>");
				logger.info("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"User Login Failed");
				request.getRequestDispatcher("login.jsp").include(request, response);
			}
		} catch (ClassNotFoundException | SQLException | IllegalAccessException | InvocationTargetException
				| InstantiationException | IllegalArgumentException | NoSuchMethodException | SecurityException e) {
			logger.error("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
					"Encountered Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}