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


import com.contacts.model.Session;
import com.contacts.model.User;

@WebServlet("/deleteMail/*")
public class DeleteUserMailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			HttpSession session = request.getSession();
			String sessionId = UserDAO.getSessionIdFromCookie(request, "session");
			Session userSession = UserDAO.getUserSession(sessionId);
			int user_id = userSession.getUserId();
			int mail_id = Integer.parseInt(request.getPathInfo().substring(1));
			if (UserDAO.checkIsPrimaryMail(mail_id)) {
				
				session.setAttribute("message", "Can't Delete Primary Mail");
			} else {
				if (UserDAO.deleteMail(mail_id)) {
					User newUser = UserDAO.getUserInfo(user_id);
					SessionCache.userCache.put(user_id, newUser);
					SessionCache.notifyUserUpdate(newUser);
					session.setAttribute("message", "Mail Deleted Successfully");
				} else {
					session.setAttribute("message", "Mail Deletion Failed");
				}
			}
		} catch (IllegalArgumentException | ClassNotFoundException | IllegalAccessException | InvocationTargetException
				| InstantiationException | NoSuchMethodException | SecurityException | SQLException e) {
			e.printStackTrace();
		}
		response.sendRedirect("/home.jsp");
	}
}
