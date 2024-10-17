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
import com.contacts.model.Contact;

@WebServlet("/add-contact")
public class AddContactServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
			request.getRequestDispatcher("home.jsp").include(request, response);
		}
		ContactDAO contactdao = new ContactDAO();
		HttpSession session = request.getSession();
		int user_id = (int) session.getAttribute("user");
		try {
			if (contactdao.addContact(user_id, contact)) {
				out.println("<div class='message'>Contact Added Successfully.</div>");
				request.getRequestDispatcher("home.jsp").include(request, response);
			} else {
				out.println("<div class='message'>Contact Addition Failed.</div>");
				request.getRequestDispatcher("home.jsp").include(request, response);
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
}
