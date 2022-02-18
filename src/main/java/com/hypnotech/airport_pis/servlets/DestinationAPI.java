package com.hypnotech.airport_pis.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hypnotech.airport_pis.beans.JsonBuilder;
import com.hypnotech.airport_pis.beans.JsonBuilder.Status;
import com.hypnotech.airport_pis.beans.Destination;
import com.hypnotech.airport_pis.model.Database;
import com.hypnotech.airport_pis.model.DestinationModel;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DestinationAPI
 * 
 * @author damien
 */
public class DestinationAPI extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public DestinationAPI() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		this.sendJsonResponse(response, JsonBuilder.badRequest());
	}

	private void isSafeToDelete(JsonObject body, HttpServletResponse response) {
		if (body.has("destination_id")) {
			DestinationModel destinations = new DestinationModel();
			this.sendJsonResponse(response, destinations.getLinkedEntry(body.get("destination_id").getAsInt()));
			destinations.finalize();
		} else {
			this.sendJsonResponse(response, JsonBuilder.badRequest());
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		JsonObject body = getRequestBody(request);
		if (body.has("action")) {
			String action = body.get("action").getAsString();

			switch (action) {
			case "isSafeToDelete":
				this.isSafeToDelete(body, response);
				break;

			case "addData":
				this.addData(body, response);
				break;

			case "getAllDatas":
				this.getAllDatas(response);
				break;

			case "updateData":
				this.updateData(body, response);
				break;

			case "delete":
				this.deleteData(body, response);
				break;

			default:
				this.sendJsonResponse(response, JsonBuilder.badRequest());
			}
		} else {
			this.sendJsonResponse(response, JsonBuilder.badRequest());
		}
	}

	private void addData(JsonObject body, HttpServletResponse response) {
		if (body.has("destination_name") && body.has("destination_name_fr") && body.has("destination_airport_code")) {
			DestinationModel destinations = new DestinationModel();
			Destination newDest = new Destination();

			newDest.destination_name = body.get("destination_name").getAsString();
			newDest.destination_name_fr = body.get("destination_name_fr").getAsString();
			newDest.destination_airport_code = body.get("destination_airport_code").getAsString();

			newDest.destination_id = destinations.insertDestination(newDest);
			;

			if (newDest.destination_id >= 0) {
				JsonBuilder json = new JsonBuilder();
				json.setStatus(Status.OK);
				json.setData(newDest);
				this.sendJsonResponse(response, json);
			} else {
				this.sendJsonResponse(response, JsonBuilder.badRequest());
			}
		} else {
			this.sendJsonResponse(response, JsonBuilder.badRequest());
		}

	}

	private void updateData(JsonObject body, HttpServletResponse response) {
		if (body.has("destination_id") && body.has("destination_name") && body.has("destination_name_fr")
				&& body.has("destination_airport_code")) {
			DestinationModel destinations = new DestinationModel();
			Destination uDest = new Destination();

			uDest.destination_id = body.get("destination_id").getAsInt();
			uDest.destination_name = body.get("destination_name").getAsString();
			uDest.destination_name_fr = body.get("destination_name_fr").getAsString();
			uDest.destination_airport_code = body.get("destination_airport_code").getAsString();

			if (destinations.updateData(uDest)) {
				JsonBuilder json = new JsonBuilder();
				json.setStatus(Status.OK);
				json.setData(uDest);
				this.sendJsonResponse(response, json);
			} else {
				this.sendJsonResponse(response, JsonBuilder.badRequest());
			}
		} else {
			this.sendJsonResponse(response, JsonBuilder.badRequest());
		}
	}

	private void deleteData(JsonObject body, HttpServletResponse response) {
		Destination deletingDestination = new Destination();
		JsonBuilder json = new JsonBuilder();
		if (body.has("destination_id")) {
			deletingDestination.destination_id = body.get("destination_id").getAsInt();

			try {
				new Database().deleteObject(deletingDestination);
				json.setStatus(Status.OK);
			} catch (SQLException e) {
				e.printStackTrace();
				json.setStatus(Status.DB_SQL_ERR);
				json.errorMessage = e.getMessage();
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
				json.setStatus(Status.INTERNAL_EXCEPTION);
				json.errorMessage = "Serious server exception raised. Contact Administrator with this message:\r\n"
						+ e.getMessage();
			}

			this.sendJsonResponse(response, json);

		} else {
			this.sendJsonResponse(response, JsonBuilder.badRequest());
		}
	}

	/**
	 * Read : Get data from database and request the response for client
	 * 
	 * @param response - The HttpServletResponse object
	 */
	private void getAllDatas(HttpServletResponse response) {
		DestinationModel destinations = new DestinationModel();
		JsonBuilder json = new JsonBuilder();
		json.setStatus(Status.OK);
		json.setData(destinations.getAllDestinations());
		destinations.finalize();
		this.sendJsonResponse(response, json);
	}

	/**
	 * Create and send the JSON response to the client
	 * 
	 * @param response - The HttpServletResponse object
	 * @param json     - Json Object to send to the client
	 */
	private void sendJsonResponse(HttpServletResponse response, JsonBuilder json) {
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
	private JsonObject getRequestBody(HttpServletRequest request) {
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
}
