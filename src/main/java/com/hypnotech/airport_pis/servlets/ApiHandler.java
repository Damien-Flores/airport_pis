package com.hypnotech.airport_pis.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hypnotech.airport_pis.beans.JsonBuilder;

/**
 * ApiHandler
 * 
 * Handle apiQuery
 */
public class ApiHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected String _action;
	protected String _where;
	protected String _orderby;
	protected String _offset;
	protected String _limit;
       
    public ApiHandler() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		this.sendJsonResponse(response, JsonBuilder.badRequest());
	}

	/**
	 * Create and send the JSON response to the client
	 * 
	 * @param response - The HttpServletResponse object
	 * @param json     - Json Object to send to the client
	 */
	protected void sendJsonResponse(HttpServletResponse response, JsonBuilder json) {
		String res = new Gson().toJson(json);
		PrintWriter out;
		try {
			out = response.getWriter();
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			out.print(res);
			out.flush();
		} catch (IOException e) {
			System.err.println("Error while getting response.getWriter :");
			e.printStackTrace();
		}
	}

	/**
	 * Parse body content of POST request method.
	 * 
	 * @param request - The HttpServletRequest request with data to parse
	 * @return Return a JsonObject representing data from the body request
	 */
	protected JsonObject getRequestBody(HttpServletRequest request) {
		StringBuilder buffer = new StringBuilder();
		BufferedReader reader;
		String line;
		try {
			reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}

		} catch (IOException e) {
			System.err.println("getRequestBody() fail ! Exception thrown");
			e.printStackTrace();
		}

		return new Gson().fromJson(buffer.toString(), JsonObject.class);
	}
	
	/**
	 * 
	 * @param requestedPath
	 * @return String[] Holding part of the query as follow:
	 * [0] : action (get|set|checkfk|delete)
	 * [1] : where clause like tableColumn=clause
	 * [2] : orderby clause like tableColumn=(asc|desc)
	 * [3] : offset-limit
	 */
	protected void parseRequestedPath(String requestedPath) {
		String[] parsedPath = requestedPath.split("[/]");
		
		switch (parsedPath.length) {
		case 8:
			this._limit = parsedPath[7];
		case 7:
			this._offset = parsedPath[6];
		case 6:
			this._orderby = parsedPath[5];
		case 5:
			this._where = parsedPath[4];
		case 4:
			this._action = parsedPath[3];
		// case 3: `dataset` (Servlet Path)
		// case 2: `api` (api root route)
		// case 1: `` (empty String)
		default:
			//Handle a default case, in case...
		}
	}
	
}
