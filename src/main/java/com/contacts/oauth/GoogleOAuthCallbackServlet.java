package com.contacts.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.contacts.connection.ConfigurationLoader;

@WebServlet("/google-callback")
public class GoogleOAuthCallbackServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String TOKEN_URL = ConfigurationLoader.getProperty("googleOAuthTokenURI");
	private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
	private static final String CONTACTS_URL = "https://www.googleapis.com/auth/contacts";
	private static final String CLIENT_ID = ConfigurationLoader.getProperty("googleOAuthClientID");
	private static final String CLIENT_SECRET = ConfigurationLoader.getProperty("googleOAuthClientSecret");
	private static final String REDIRECT_URI = ConfigurationLoader.getProperty("googleOAuthRedirectURI");

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String code = req.getParameter("code");

		if (code != null) {
			String accessToken = fetchAccessToken(code);
			if (accessToken != null) {
				String userInfo = fetchContacts(accessToken);
				req.getSession().setAttribute("user", userInfo);
				resp.sendRedirect("/profile");
			} else {
				resp.getWriter().write("Error retrieving access token.");
			}
		} else {
			resp.getWriter().write("Authorization failed.");
		}
	}

	private String fetchAccessToken(String code) throws IOException {
		URL url = new URL(TOKEN_URL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setDoOutput(true);

		String postData = "code=" + URLEncoder.encode(code, "UTF-8") + "&client_id="
				+ URLEncoder.encode(CLIENT_ID, "UTF-8") + "&client_secret=" + URLEncoder.encode(CLIENT_SECRET, "UTF-8")
				+ "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8") + "&grant_type=authorization_code";

		try (OutputStream os = conn.getOutputStream()) {
			os.write(postData.getBytes());
		}

		return extractValueFromResponse(conn, "access_token");
	}

	private String fetchUserInfo(String accessToken) throws IOException, URISyntaxException {
		URL url = new URI(USER_INFO_URL + "?access_token=" + URLEncoder.encode(accessToken, "UTF-8")).toURL();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");

		return readResponse(conn);
	}

	private String fetchContacts(String accessToken) throws IOException {
		String endpoint = "?personFields=names,emailAddresses,birthdays,addresses,phoneNumbers";
		String contactsUrl = "https://people.googleapis.com/v1/people/me/connections" + endpoint + "&pageSize=5";

		URL url = new URL(contactsUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Authorization", "Bearer " + accessToken);
		String response = readResponse(conn);
		return response;
	}

	private String extractValueFromResponse(HttpURLConnection conn, String key) throws IOException {
		String response = readResponse(conn);
		String token = null;
		response = response.replace("{", "").replace("}", "").trim();
		for (String s : response.split(",")) {
			String sub = s.replace("\"", "").trim();
			String[] entry = sub.split(":");
			System.out.println(entry[0]);
			if (entry[0].equals(key)) {
				token = entry[1].trim();
				break;
			}
		}

		return token;
	}

	private String readResponse(HttpURLConnection conn) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuilder response = new StringBuilder();
		String line;

		while ((line = reader.readLine()) != null) {
			response.append(line);
		}

		return response.toString();
	}
}
