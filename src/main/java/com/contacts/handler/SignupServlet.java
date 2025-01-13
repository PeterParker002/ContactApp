package com.contacts.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.cache.SessionCache;
import com.contacts.dao.UserDAO;
import com.contacts.logger.MyCustomLogger;
import com.contacts.model.Mail;
import com.contacts.model.MobileNumber;
import com.contacts.model.Session;
import com.contacts.model.User;
import com.contacts.model.UserMail;
import com.contacts.model.UserMobile;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final MyCustomLogger logger = new MyCustomLogger(
			"/home/karthik-tt0479/eclipse-workspace/FirstProject/src/main/resources/logs/application.log",
			MyCustomLogger.LogLevel.INFO);

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		User user = new User();
		try {
			user.setUsername(request.getParameter("username"));
			user.setFirstName(request.getParameter("fname"));
			user.setMiddleName(request.getParameter("midname"));
			user.setLastName(request.getParameter("lname"));
			user.setGender(request.getParameter("gender"));
			user.setDateOfBirth(request.getParameter("dob"));
			UserMobile mobile = new UserMobile();
			mobile.setMobileNumber(Long.parseLong(request.getParameter("mobile")));
			user.setMobileNumber(mobile);
			UserMail mail = new UserMail();
			mail.setEmail(request.getParameter("email"));
			mail.setIsPrimary(true);
			user.setEmail(mail);
			user.setNotes(request.getParameter("notes"));
			user.setHomeAddress(request.getParameter("home"));
			user.setWorkAddress(request.getParameter("work"));
			user.setPassword(request.getParameter("password"));
		} catch (NumberFormatException numException) {
			out.println("<div class='message'>Mobile Number should be a 10 digit number.</div>");
			logger.error("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
					"Mobile Number should be a 10 digit number.");
			request.getRequestDispatcher("signup.jsp").include(request, response);
		}
		response.setContentType("text/html");
		UserDAO userdao = new UserDAO();
		if (user.getUsername().length() < 1 || user.getEmail().get(0).getEmail().length() < 1
				|| user.getPassword().length() < 6) {
			out.println("<div class='message'>Sign Up Failed</div>");
			logger.info("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
					"User Signup failed.");
			request.getRequestDispatcher("signup.jsp").include(request, response);
		} else {
			try {
				int user_id = userdao.SignupUser(user);
				if (user_id != -1) {
					user.setUserId(user_id);
					out.println("<div class='message'>Signup Successful</div>");
					HttpSession session = request.getSession(true);
					String now = LocalDateTime.now().toString();
					String sessionId = userdao.generateSessionId();
					Session s = new Session();
					s.setSessionId(sessionId);
					s.setUserId(user.getUserId());
					s.setCreatedAt(now);
					s.setLastAccessedAt(now);
					userdao.createSession(s);
					SessionCache.activeSessionObjects.put(sessionId, s);
					SessionCache.addUserToCache(user.getUserId(), user);
					response.addCookie(new Cookie("session", sessionId));
					session.setAttribute("user", user.getUserId());
					logger.info("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
							"User Signup Successful.");
					response.sendRedirect("home.jsp");
				} else {
					out.println("<div class='message'>Signup Failed</div>");
					logger.info("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
							"User Signup failed.");
					request.getRequestDispatcher("signup.jsp").include(request, response);
				}
			} catch (SQLIntegrityConstraintViolationException e) {
				String mail = e.getMessage().split("'")[1];
				String msgString = "Can't add duplicate email '" + mail + "'";
				out.println("<div class='message'>" + msgString + "</div>");
				logger.error("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						e.getMessage());
				e.printStackTrace();
			} catch (ClassNotFoundException | SQLException e) {
				logger.error("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						e.getMessage());
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		logger.info("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
				"Redirecting to signup.jsp page.");
		request.getRequestDispatcher("/signup.jsp").forward(request, response);
	}
}
