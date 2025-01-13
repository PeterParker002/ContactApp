package com.contacts.handler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.contacts.dao.ContactDAO;
import com.contacts.dao.UserDAO;
import com.contacts.logger.MyCustomLogger;
import com.contacts.model.Contact;
import com.contacts.model.Session;
import com.contacts.model.User;

@WebServlet("/getContactsByGroup/*")
public class GetFilteredContactsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final MyCustomLogger logger = new MyCustomLogger(
			"/home/karthik-tt0479/eclipse-workspace/FirstProject/src/main/resources/logs/application.log",
			MyCustomLogger.LogLevel.INFO);

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
//		String JsonString = "{\"status\": %d, \"name\": \"%s\", \"contacts\": %s }";
		JSONObject output = new JSONObject();
		try {
			int group_id = Integer.parseInt(request.getPathInfo().substring(1));
			ContactDAO c = new ContactDAO();
			HttpSession session = request.getSession(false);
			UserDAO userdao = new UserDAO();
			String sessionId = userdao.getSessionIdFromCookie(request, "session");
			Session userSession = userdao.getUserSession(sessionId);
			int user_id = userSession.getUserId();
			ArrayList<Contact> rs = c.getContactsByGroupId(user_id, group_id);
			String groupName = c.getGroupNameById(group_id);
			if (rs.size() > 0) {
//				String finalString = "";
				ArrayList<JSONObject> contacts = new ArrayList<>();
				for (Contact cont : rs) {
					JSONObject contactJson = new JSONObject();
					contactJson.put("id", cont.getContactId());
					contactJson.put("fname", cont.getFirstName());
					contactJson.put("mname", cont.getMiddleName());
					contactJson.put("lname", cont.getLastName());
					contacts.add(contactJson);
				}
				output.put("status", 1);
				output.put("name", groupName);
				output.put("contacts", contacts);
				logger.info("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"Contacts Filtered based on Groups has been Retreived Successfully.");
				response.getWriter().println(output.toString());
			} else {
				output.put("status", 0);
				output.put("name", groupName);
				output.put("contacts", "[]");
				logger.info("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(),
						"No Entry Found.");
				response.getWriter().println(output.toString());
			}
		} catch (NumberFormatException n) {
			output.put("status", -1);
			output.put("name", "");
			output.put("contacts", "[]");
			logger.error("GET", request.getRemoteAddr(), request.getRequestURI(), response.getStatus(), n.getMessage());
			response.getWriter().println(output.toString());
		}
	}
}