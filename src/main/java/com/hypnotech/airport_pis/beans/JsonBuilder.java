package com.hypnotech.airport_pis.beans;

public class JsonBuilder {
	private Status status;
	public String errorMessage;
	private Object data;

	public enum Status {
		OK("Ok"), BAD_REQUEST("Bad Request"), DB_CON_FAIL("Database Fails"), DB_SQL_ERR("SQL Error"),
		INTERNAL_EXCEPTION("Exception Raised");

		private String description;

		private Status(String description) {
			this.description = description;
		}

		public String getDescription() {
			return this.description;
		}

	}

	public static JsonBuilder badRequest() {
		JsonBuilder badRequest = new JsonBuilder();
		badRequest.setStatus(Status.BAD_REQUEST);
		return badRequest;
	}

	public String getStatus() {
		return this.status.getDescription();
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
