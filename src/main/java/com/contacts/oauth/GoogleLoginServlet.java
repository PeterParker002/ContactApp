package com.contacts.oauth;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.contacts.connection.ConfigurationLoader;

@WebServlet("/login-with-google")
public class GoogleLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String AUTH_URL = ConfigurationLoader.getProperty("googleOAuthAuthURI");
		String CLIENT_ID = ConfigurationLoader.getProperty("googleOAuthClientID");
		String REDIRECT_URI = ConfigurationLoader.getProperty("googleOAuthRedirectURI");
		String authRequestUrl = AUTH_URL + "?response_type=code" + "&client_id=" + CLIENT_ID + "&redirect_uri="
				+ URLEncoder.encode(REDIRECT_URI, "UTF-8") + "&scope="
				+ URLEncoder.encode("openid email profile https://www.googleapis.com/auth/contacts.readonly", "UTF-8");
		response.sendRedirect(authRequestUrl);
	}

}
