package com.contacts.handler;

import java.io.IOException;
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
import com.google.gson.Gson;

@WebServlet("/edit-profile")
public class EditUserInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String sessionId = UserDAO.getSessionIdFromCookie(request, "session");
		Session userSession = UserDAO.getUserSession(sessionId);
		int user_id = userSession.getUserId();
		HttpSession session = request.getSession();
		Gson gson = new Gson();
		User u = gson.fromJson(request.getReader(), User.class);
		User user = SessionCache.userCache.get(user_id);
		boolean isChanged = false;
		if (u.getFirstName() != null) {
			isChanged = true;
			user.setFirstName(u.getFirstName());
		}
		if (u.getMiddleName() != null) {
			isChanged = true;
			user.setMiddleName(u.getMiddleName());
		}
		if (u.getLastName() != null) {
			isChanged = true;
			user.setLastName(u.getLastName());
		}
		if (u.getGender() != null) {
			isChanged = true;
			user.setGender(u.getGender());
		}
		if (u.getDateOfBirth() != null) {
			isChanged = true;
			user.setDateOfBirth(u.getDateOfBirth());
		}
		if (u.getNotes() != null) {
			isChanged = true;
			user.setNotes(u.getNotes());
		}
		if (u.getHomeAddress() != null) {
			isChanged = true;
			user.setHomeAddress(u.getHomeAddress());
		}
		if (u.getWorkAddress() != null) {
			isChanged = true;
			user.setWorkAddress(u.getWorkAddress());
		}
		u.setModifiedAt(System.currentTimeMillis());
		boolean result;
		if (isChanged) {
			try {
				result = UserDAO.editUserInfo(user_id, u);
				if (result) {
					SessionCache.userCache.put(user_id, user);
					SessionCache.notifyUserUpdate(user);
					session.setAttribute("message", "User updated successfully!");
				} else {
					session.setAttribute("message", "Operation Failed");
				}
			} catch (ClassNotFoundException | SQLException | IllegalArgumentException | SecurityException e) {
				e.printStackTrace();
			}
		}
		response.sendRedirect("home.jsp");
	}
}
