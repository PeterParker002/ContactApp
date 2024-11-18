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

import com.contacts.dao.UserDAO;
import com.contacts.model.Mail;
import com.contacts.model.MobileNumber;
import com.contacts.model.User;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

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
			MobileNumber mobile = new MobileNumber();
			mobile.setMobileNumber(Long.parseLong(request.getParameter("mobile")));
			user.setMobileNumber(mobile);
			Mail mail = new Mail();
			mail.setMail(request.getParameter("email"));
			user.setEmail(mail);
			user.setNotes(request.getParameter("notes"));
			user.setHomeAddress(request.getParameter("home"));
			user.setWorkAddress(request.getParameter("work"));
			user.setPassword(request.getParameter("password"));
		} catch (NumberFormatException numException) {
			out.println("<div class='message'>Mobile Number should be a 10 digit number.</div>");
			request.getRequestDispatcher("signup.jsp").include(request, response);
		}
		response.setContentType("text/html");
		UserDAO userdao = new UserDAO();
		if (user.getUsername().length() < 1 || user.getEmail().get(0).getMail().length() < 1
				|| user.getPassword().length() < 6) {
			out.println("<div class='message'>Sign Up Failed</div>");
			request.getRequestDispatcher("signup.jsp").include(request, response);
		} else {
			try {
				int user_id = userdao.SignupUser(user);
				if (user_id != -1) {
					out.println("<div class='message'>Signup Successful</div>");
					HttpSession session = request.getSession(true);
					session.setAttribute("user", user_id);
					request.getRequestDispatcher("home.jsp").include(request, response);
				} else {
					out.println("<div class='message'>Signup Failed</div>");
					request.getRequestDispatcher("signup.jsp").include(request, response);
				}
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		request.getRequestDispatcher("/signup.jsp").forward(request, response);
	}
}
