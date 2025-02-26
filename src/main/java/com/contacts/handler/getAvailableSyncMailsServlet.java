package com.contacts.handler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.contacts.dao.OAuthDAO;
import com.contacts.dao.UserDAO;
import com.contacts.model.OAuthDetails;
import com.contacts.model.Session;
import com.contacts.utils.MyCustomJsonObject;

@WebServlet("/getAvailableSyncMailsServlet")
public class getAvailableSyncMailsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String sessionId = UserDAO.getSessionIdFromCookie(request, "session");
		Session userSession = UserDAO.getUserSession(sessionId);
		int userId = userSession.getUserId();
		try {
			List<OAuthDetails> syncMails = OAuthDAO.getAllAvailableSyncMails(userId);
			MyCustomJsonObject<Integer, String> json = new MyCustomJsonObject<Integer, String>();
			syncMails.forEach(mail -> {
				json.put(mail.getId(), mail.getEmail());
			});
			response.setContentType("application/json");
			System.out.println(json.toString());
			response.getWriter().println(json.toString());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

}
