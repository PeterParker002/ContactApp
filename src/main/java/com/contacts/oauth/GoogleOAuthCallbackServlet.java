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

import com.contacts.model.OAuthDetails;
import com.contacts.utils.OAuthUtils;

@WebServlet("/google-callback")
public class GoogleOAuthCallbackServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String code = req.getParameter("code");

		if (code != null) {
			try {
				OAuthDetails oauthDetails = OAuthUtils.fetchAccessToken(req, code);
				if (oauthDetails != null) {
					String userInfo = OAuthUtils.fetchContacts(req, oauthDetails);
					req.getSession().setAttribute("user", userInfo);
					resp.sendRedirect("/profile");
				} else {
					resp.getWriter().write("Error retrieving access token.");
				}
			} catch (URISyntaxException | ClassNotFoundException | SQLException | InstantiationException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		} else {
			resp.getWriter().write("Authorization failed.");
		}
	}
}
