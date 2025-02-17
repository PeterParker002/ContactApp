package com.contacts.handler;

import java.io.IOException;
import org.json.simple.JSONObject;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.contacts.dao.ContactDAO;
import com.contacts.dao.UserDAO;


import com.contacts.model.Contact;
import com.contacts.model.ContactMail;
import com.contacts.model.ContactMobile;
import com.contacts.model.Session;

@WebServlet("/contact/*")
public class GetContactInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		JSONObject json = new JSONObject();
		try {
			int contact_id = Integer.parseInt(request.getPathInfo().substring(1));
			String sessionId = UserDAO.getSessionIdFromCookie(request, "session");
			Session userSession = UserDAO.getUserSession(sessionId);
			int user_id = userSession.getUserId();
			Contact contact = ContactDAO.getContactInfo(contact_id, user_id);
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
				response.getWriter().println(json.toString());
			}
			else {
				json.put("status", 0);
				json.put("message", "No Entry Found");
				response.getWriter().println(json.toString());
			}
		} catch (NumberFormatException | SQLException | ClassNotFoundException n) {
			json.put("status", -1);
			json.put("message", "");
			response.getWriter().println(json.toString());
		}
	}
}
