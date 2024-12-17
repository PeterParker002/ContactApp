package com.contacts.handler;

import java.io.IOException;
import org.json.simple.JSONObject;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.dao.ContactDAO;
import com.contacts.model.User;

@WebServlet("/contact/*")
public class GetContactInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
//		String JsonString = "{\"status\": %d, \"fname\": \"%s\", \"mname\": \"%s\", \"lname\": \"%s\", \"gender\": \"%s\", \"notes\": \"%s\", \"dob\": \"%s\", \"home\": \"%s\", \"work\": \"%s\", \"mail\": \"%s\", \"mobile\": \"%s\" }";
		JSONObject json = new JSONObject();
		try {
			int contact_id = Integer.parseInt(request.getPathInfo().substring(1));
			HttpSession session = request.getSession(false);
			int user_id = (int) session.getAttribute("user");
			ContactDAO c = new ContactDAO();
			ResultSet rs = c.getContactInfo(contact_id, user_id);
			if (rs.next()) {
				json.put("status", 1);
				json.put("fname", rs.getString(1));
				json.put("mname", rs.getString(2));
				json.put("lname", rs.getString(3));
				json.put("gender", rs.getString(4));
				json.put("notes", rs.getString(5));
				json.put("dob", rs.getString(6));
				json.put("home", rs.getString(7));
				json.put("work", rs.getString(8));
				json.put("mail", rs.getString(9));
				json.put("mobile", rs.getString(10));
				response.getWriter().println(json.toString());
			} else {
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
