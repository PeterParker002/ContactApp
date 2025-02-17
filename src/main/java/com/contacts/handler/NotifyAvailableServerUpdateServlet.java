package com.contacts.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.contacts.cache.SessionCache;

@WebServlet("/notifyAvailableServerUpdate")
public class NotifyAvailableServerUpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public NotifyAvailableServerUpdateServlet() {
		super();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		SessionCache.updateAvailableServers();
	}

}
