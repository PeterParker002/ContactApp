package com.contacts.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.contacts.cache.SessionCache;
import com.contacts.model.Session;
import com.google.gson.Gson;


@WebServlet("/notifySessionChange")
public class notifySessionChangeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Gson gson = new Gson();
		Session session = gson.fromJson(request.getReader(), Session.class);
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie c : cookies) {
				if (c.getName().equals("session")) {
					String sessionId = c.getValue();
					if (SessionCache.activeSessions.containsKey(sessionId)) {
						SessionCache.activeSessions.put(sessionId, session);
					}
				}
			}
		}
	}

}
