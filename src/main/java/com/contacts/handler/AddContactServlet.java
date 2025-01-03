package com.contacts.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.dao.ContactDAO;
import com.contacts.logger.MyCustomLogger;
import com.contacts.model.Contact;
import com.contacts.model.User;

@WebServlet("/add-contact")
public class AddContactServlet extends HttpServlet {
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
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		Contact contact = new Contact();
		try {
			contact.setFirstName(request.getParameter("fname"));
			contact.setMiddleName(request.getParameter("midname"));
			contact.setLastName(request.getParameter("lname"));
			contact.setGender(request.getParameter("gender"));
			contact.setDateOfBirth(request.getParameter("dob"));
			contact.setMobileNumber(Long.parseLong(request.getParameter("mobile")));
			contact.setNotes(request.getParameter("notes"));
			contact.setHomeAddress(request.getParameter("home"));
			contact.setWorkAddress(request.getParameter("work"));
			contact.setEmail(request.getParameter("email"));
		} catch (NumberFormatException numException) {
			out.println("<div class='message'>Mobile Number should be a 10 digit number.</div>");
			logger.error("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
					"Mobile Number should be a 10 digit number.");
			request.getRequestDispatcher("home.jsp").include(request, response);
		}
		ContactDAO contactdao = new ContactDAO();
		HttpSession session = request.getSession();
		int user_id = (int) session.getAttribute("user");
		try {
			if (contactdao.addContact(user_id, contact)) {
				out.println("<div class='message'>Contact Added Successfully.</div>");
				logger.info("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"Contact Added Successfully.");
				request.getRequestDispatcher("home.jsp").include(request, response);
			} else {
				out.println("<div class='message'>Contact Addition Failed.</div>");
				logger.info("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"Contact Addition Failed.");
				request.getRequestDispatcher("home.jsp").include(request, response);
			}
		} catch (ClassNotFoundException | SQLException e) {
			logger.error("POST", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
					e.getMessage());
			e.printStackTrace();
		}
	}
}
