package com.contacts.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.dao.UserDAO;
import com.contacts.model.User;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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
				session.setAttribute("user", user.getUserId());
				session.setAttribute("message", "User Login Successful!");
				response.sendRedirect("home.jsp");
			} else {
				out.println("<div class='message'>User Login Failed!</div>");
				request.getRequestDispatcher("login.jsp").include(request, response);
			}
		} catch (ClassNotFoundException | SQLException | IllegalAccessException | InvocationTargetException
				| InstantiationException | IllegalArgumentException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
}