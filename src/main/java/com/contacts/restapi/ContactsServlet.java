package com.contacts.restapi;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.contacts.dao.ContactDAO;
import com.contacts.model.Contact;
import com.contacts.utils.MyCustomJsonObject;

@WebServlet("/ContactsServlet")
public class ContactsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings({ "static-access" })
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
		response.setContentType("application/json");
		MyCustomJsonObject<String, Object> jsonString = new MyCustomJsonObject<String, Object>();
		try (PrintWriter writer = response.getWriter()) {
			int userId = (int) request.getAttribute("user_id");
			System.out.println(userId);
			if (userId <= 0) {
				jsonString.put("message", "User authentication required to access this resource!");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				writer.println(jsonString.toString());
				return;
			}
			List<Contact> contacts = ContactDAO.getContactsInfo(userId, pageNumber - 1);
			List<MyCustomJsonObject<String, Object>> contactsList = new ArrayList<MyCustomJsonObject<String, Object>>();
			for (Contact contact : contacts) {
				MyCustomJsonObject<String, Object> contactJson = new MyCustomJsonObject<String, Object>();
				contactJson.put("id", contact.getContactId());
				contactJson.put("name", contact.getFirstName());
				contactsList.add(contactJson);
			}
			jsonString.put("contacts", contactsList);
			response.setStatus(response.SC_OK);
			writer.println(jsonString);
			return;
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	}

}
