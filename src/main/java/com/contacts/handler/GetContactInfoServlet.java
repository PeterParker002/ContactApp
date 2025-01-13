package com.contacts.handler;

import java.io.IOException;
import org.json.simple.JSONObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.dao.ContactDAO;
import com.contacts.dao.UserDAO;
import com.contacts.logger.MyCustomLogger;
import com.contacts.model.Contact;
import com.contacts.model.ContactMail;
import com.contacts.model.ContactMobile;
import com.contacts.model.Session;
import com.contacts.model.User;

@WebServlet("/contact/*")
public class GetContactInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final MyCustomLogger logger = new MyCustomLogger(
			"/home/karthik-tt0479/eclipse-workspace/FirstProject/src/main/resources/logs/application.log",
			MyCustomLogger.LogLevel.INFO);

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
//		String JsonString = "{\"status\": %d, \"fname\": \"%s\", \"mname\": \"%s\", \"lname\": \"%s\", \"gender\": \"%s\", \"notes\": \"%s\", \"dob\": \"%s\", \"home\": \"%s\", \"work\": \"%s\", \"mail\": \"%s\", \"mobile\": \"%s\" }";
		JSONObject json = new JSONObject();
		try {
			int contact_id = Integer.parseInt(request.getPathInfo().substring(1));
			HttpSession session = request.getSession(false);
			UserDAO userdao = new UserDAO();
			String sessionId = userdao.getSessionIdFromCookie(request, "session");
			Session userSession = userdao.getUserSession(sessionId);
			int user_id = userSession.getUserId();
			ContactDAO c = new ContactDAO();
			Contact contact = c.getContactInfo(contact_id, user_id);
			if (contact != null) {
				json.put("status", 1);
				json.put("fname", contact.getFirstName());
				json.put("mname", contact.getMiddleName());
				json.put("lname", contact.getLastName());
				json.put("gender", contact.getGender());
				json.put("notes", contact.getNotes());
				json.put("dob", contact.getDateOfBirth());
				json.put("home", contact.getHomeAddress());
				json.put("work", contact.getWorkAddress());
				ArrayList<JSONObject> mails = new ArrayList<>();
				for (ContactMail mail : contact.getEmail()) {
					JSONObject mailJson = new JSONObject();
					mailJson.put("id", mail.getId());
					mailJson.put("mail", mail.getEmail());
					mails.add(mailJson);
				}
				json.put("mails", mails);
				ArrayList<JSONObject> mobiles = new ArrayList<>();
				for (ContactMobile mobile : contact.getMobileNumber()) {
					JSONObject mobileJson = new JSONObject();
					mobileJson.put("id", mobile.getId());
					mobileJson.put("mobile", mobile.getMobileNumber());
					mobiles.add(mobileJson);
				}
				json.put("mobiles", mobiles);
				logger.info("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"Contact Info Retreived Successfully.");
				response.getWriter().println(json.toString());
			}
//			if (rs.next()) {
//				json.put("status", 1);
//				json.put("fname", rs.getString(1));
//				json.put("mname", rs.getString(2));
//				json.put("lname", rs.getString(3));
//				json.put("gender", rs.getString(4));
//				json.put("notes", rs.getString(5));
//				json.put("dob", rs.getString(6));
//				json.put("home", rs.getString(7));
//				json.put("work", rs.getString(8));
//				json.put("mail", rs.getString(9));
//				json.put("mobile", rs.getString(10));
//				logger.info("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
//						"Contact Info Retreived Successfully.");
//				response.getWriter().println(json.toString());
//			} 
			else {
				json.put("status", 0);
				json.put("message", "No Entry Found");
				logger.info("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"No Entry Found.");
				response.getWriter().println(json.toString());
			}
		} catch (NumberFormatException | SQLException | ClassNotFoundException n) {
			json.put("status", -1);
			json.put("message", "");
			logger.error("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(), n.getMessage());
			response.getWriter().println(json.toString());
		}
	}
}
