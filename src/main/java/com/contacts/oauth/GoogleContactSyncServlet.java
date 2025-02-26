package com.contacts.oauth;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.contacts.dao.OAuthDAO;
import com.contacts.model.OAuthDetails;
import com.contacts.utils.OAuthUtils;

@WebServlet("/GoogleContactSyncServlet")
public class GoogleContactSyncServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int oauthId = Integer.parseInt(request.getParameter("id"));
		try {
			OAuthDetails od = OAuthDAO.getSyncMailById(oauthId);
			String userInfo = OAuthUtils.fetchContacts(request, od);
			request.getSession().setAttribute("user", userInfo);
			response.sendRedirect("/profile");
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | URISyntaxException | ClassNotFoundException
				| SQLException e) {
			e.printStackTrace();
		}
	}

}
