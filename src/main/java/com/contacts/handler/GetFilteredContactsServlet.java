package com.contacts.handler;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.contacts.dao.ContactDAO;
import com.contacts.dao.UserDAO;


import com.contacts.model.Contact;
import com.contacts.model.Session;

@WebServlet("/getContactsByGroup/*")
public class GetFilteredContactsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		JSONObject output = new JSONObject();
		try {
			int group_id = Integer.parseInt(request.getPathInfo().substring(1));
			String sessionId = UserDAO.getSessionIdFromCookie(request, "session");
			Session userSession = UserDAO.getUserSession(sessionId);
			int user_id = userSession.getUserId();
			ArrayList<Contact> rs = ContactDAO.getContactsByGroupId(user_id, group_id);
			String groupName = ContactDAO.getGroupNameById(group_id);
			if (rs.size() > 0) {
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
				response.getWriter().println(output.toString());
			} else {
				output.put("status", 0);
				output.put("name", groupName);
				output.put("contacts", "[]");
				response.getWriter().println(output.toString());
			}
		} catch (NumberFormatException n) {
			output.put("status", -1);
			output.put("name", "");
			output.put("contacts", "[]");
			response.getWriter().println(output.toString());
		}
	}
}