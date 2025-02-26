package com.contacts.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.Response;

import com.contacts.connection.ConfigurationLoader;
import com.contacts.dao.OAuthDAO;
import com.contacts.dao.UserDAO;
import com.contacts.model.OAuthDetails;
import com.contacts.model.Session;

public class OAuthUtils {
	private static final String TOKEN_URL = ConfigurationLoader.getProperty("googleOAuthTokenURI");
	private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
	private static final String CONTACTS_URL = "https://people.googleapis.com/v1/people/me/connections";
	private static final String CLIENT_ID = ConfigurationLoader.getProperty("googleOAuthClientID");
	private static final String CLIENT_SECRET = ConfigurationLoader.getProperty("googleOAuthClientSecret");
	private static final String REDIRECT_URI = ConfigurationLoader.getProperty("googleOAuthRedirectURI");

	@SuppressWarnings("unused")
	public static OAuthDetails fetchAccessToken(HttpServletRequest request, String code) throws IOException,
			URISyntaxException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		URL url = new URI(TOKEN_URL).toURL();
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

		String responseString = readResponse(conn);
		System.out.println(responseString);
		OAuthDetails oauthDetail = populateOAuthDetails(request, responseString);
		return oauthDetail;
	}

	@SuppressWarnings("unused")
	public static OAuthDetails updateAccessTokenUsingRefreshToken(HttpServletRequest request, OAuthDetails oauthDetails)
			throws IOException, URISyntaxException, ClassNotFoundException, SQLException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException {

		URL url = new URI(TOKEN_URL).toURL();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setDoOutput(true);

		String postData = "refresh_token=" + URLEncoder.encode(oauthDetails.getRefreshToken(), "UTF-8") + "&client_id="
				+ URLEncoder.encode(CLIENT_ID, "UTF-8") + "&client_secret=" + URLEncoder.encode(CLIENT_SECRET, "UTF-8")
				+ "&grant_type=refresh_token";

		try (OutputStream os = conn.getOutputStream()) {
			os.write(postData.getBytes());
		}

		String responseString = readResponse(conn);
		System.out.println(responseString);

		OAuthDetails oauthDetail = populateOAuthDetails(request, responseString);
		return oauthDetail;
	}

	public static OAuthDetails populateOAuthDetails(HttpServletRequest request, String responseString)
			throws ClassNotFoundException, SQLException, IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		String accessToken = extractValueFromJson(responseString, "access_token");
		String refreshToken = extractValueFromJson(responseString, "refresh_token");
		String idToken = extractValueFromJson(responseString, "id_token");
		String email = extractValueFromJson(decodeJWT(idToken), "email");
		long now = System.currentTimeMillis();
		String sessionId = UserDAO.getSessionIdFromCookie(request, "session");
		Session userSession = UserDAO.getUserSession(sessionId);
		int user_id = userSession.getUserId();
		OAuthDetails oauthDetail = new OAuthDetails();
		oauthDetail.setUserId(user_id);
		oauthDetail.setEmail(email);
		oauthDetail.setRefreshToken(refreshToken);
		oauthDetail.setAccessToken(accessToken);
		oauthDetail.setCreatedAt(now);
		oauthDetail.setModifiedAt(now);
		if (OAuthDAO.isSyncMailExist(oauthDetail)) {
			OAuthDAO.updateSyncMail(oauthDetail);
		} else {
			OAuthDAO.addSyncMail(oauthDetail);
		}
		return oauthDetail;
	}

	public static String decodeJWT(String jwt) {
		String[] parts = jwt.split("\\.");
		if (parts.length < 2) {
			System.out.println("Invalid JWT!");
			return null;
		}

		String headerJson = decodeBase64(parts[0]);
		String payloadJson = decodeBase64(parts[1]);

		System.out.println("Header: " + headerJson);
		System.out.println("Payload: " + payloadJson);
		return payloadJson;
	}

	private static String decodeBase64(String base64String) {
		return new String(Base64.getUrlDecoder().decode(base64String));
	}

	@SuppressWarnings("unused")
	public static String fetchUserInfo(String accessToken) throws IOException, URISyntaxException {
		URL url = new URI(USER_INFO_URL + "?access_token=" + URLEncoder.encode(accessToken, "UTF-8")).toURL();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");

		return readResponse(conn);
	}

	@SuppressWarnings("unused")
	public static String fetchContacts(HttpServletRequest request, OAuthDetails oauthDetails)
			throws IOException, URISyntaxException, ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, SQLException {
		String endpoint = "?personFields=names,emailAddresses,birthdays,addresses,phoneNumbers";
		String contactsUrl = CONTACTS_URL + endpoint + "&pageSize=10";
		String response = null;

		URL url = new URI(contactsUrl).toURL();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Authorization", "Bearer " + oauthDetails.getAccessToken());
		if (conn.getResponseCode() == Response.SC_OK) {
			response = readResponse(conn);
		} else {
			OAuthDetails newOauthDetails = updateAccessTokenUsingRefreshToken(request, oauthDetails);
			return fetchContacts(request, newOauthDetails);
		}
		return response;
	}

	public static String extractValueFromJson(String responseString, String key) throws IOException {
		String response = responseString;
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

	public static String readResponse(HttpURLConnection conn) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuilder response = new StringBuilder();
		String line;

		while ((line = reader.readLine()) != null) {
			response.append(line);
		}

		return response.toString();
	}
}
