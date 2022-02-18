package com.hypnotech.airport_pis.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import com.google.gson.JsonObject;
import com.hypnotech.airport_pis.beans.Airline;
import com.hypnotech.airport_pis.beans.JsonBuilder;
import com.hypnotech.airport_pis.beans.JsonBuilder.Status;
import com.hypnotech.airport_pis.model.Database;

/**
 * Servlet implementation class ApiAirline
 */
public class ApiAirline extends ApiHandler {
	private static final long serialVersionUID = 1L;
       
    public ApiAirline() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.parseRequestedPath(request.getRequestURI());
		//JsonObject body = this.getRequestBody(request);
		
		switch (this._action) {
		case "get":
			this.get(response);
			break;
		case "set":
			this.set(request, response);
			break;
		case "checkfk":
			//TODO this.checkForeignKeyExistance(response);
			break;
		case "delete":
			//TODO this.delete(request, response);
			break;
		default:
			this.sendJsonResponse(response, JsonBuilder.badRequest());
		}
	}
	
	private void get(HttpServletResponse response) {
		Database db = new Database();
		JsonBuilder jb = new JsonBuilder();
		List<Object> airlines;
		
		try {
			airlines = db.getBeanData(new Airline());
			jb.setStatus(Status.OK);
			jb.setData(airlines);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NoSuchFieldException | SQLException e) {
			jb.setStatus(Status.INTERNAL_EXCEPTION);
			jb.errorMessage = e.getMessage();
		}
		
		this.sendJsonResponse(response, jb);
	}
	
	private void set(HttpServletRequest request, HttpServletResponse response) {
		Database db = new Database();
		JsonBuilder jb = new JsonBuilder();
		JsonObject requestBody = this.getRequestBody(request);
		Airline airline = new Airline();
		
		if (requestBody != null && requestBody.has("airline_code") && requestBody.has("airline_name")) {
			airline.airline_code = requestBody.get("airline_code").getAsString();
			airline.airline_name = requestBody.get("airline_name").getAsString();
			
			//TODO Handle Image Loading
			
			if (requestBody.has("airline_id") && !requestBody.get("airline_id").isJsonNull())
				airline.airline_id = requestBody.get("airline_id").getAsInt();			
			
			
			try {
				airline.airline_id = db.setBeanData(airline);
				jb.setStatus(Status.OK);
				jb.setData(airline);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				jb.setStatus(Status.INTERNAL_EXCEPTION);
				jb.errorMessage = e.getMessage();
			} catch (SQLException e) {
				jb.setStatus(Status.DB_SQL_ERR);
				jb.errorMessage = e.getMessage();
			}
			
		}
		else {
			jb.setStatus(Status.BAD_REQUEST);
			jb.errorMessage = "Required Field no defined";
		}

		this.sendJsonResponse(response, jb);
	}

}
