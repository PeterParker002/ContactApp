package com.contacts.handler;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Servlet implementation class Notifier
 */
@WebServlet("/notify")
public class Notifier extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Notifier() {
        super();
        // TODO Auto-generated constructor stub
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

        // Step 2: Read the JSON data from the request body
        StringBuilder jsonBuffer = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        }

        // Step 3: Parse the JSON data using Simple JSON
        String jsonData = jsonBuffer.toString();
        JSONParser jsonParser = new JSONParser();

        try {
            // Parse the string into a JSONObject
            JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonData);

            // Step 4: Extract data from the JSON object
            String name = (String) jsonObject.get("name");

            // Step 5: Send a JSON response back to the client
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            JSONObject responseJson = new JSONObject();
            System.out.println(name);
            responseJson.put("status", "success");
            responseJson.put("message", "JSON processed successfully.");
            responseJson.put("name", name);

            response.getWriter().println(responseJson.toString());

        } catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

}
