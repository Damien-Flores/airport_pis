package com.hypnotech.airport_pis.model;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.hypnotech.airport_pis.beans.JsonBuilder;
import com.hypnotech.airport_pis.beans.JsonBuilder.Status;
import com.hypnotech.airport_pis.beans.BeanReference;

public class Database {
	protected Connection connection;
	private String errorMessage;
	private final String DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
	private final String DB_URL = "jdbc:mysql://localhost/airport_pis";
	private final String DB_USERNAME = "airport_pis";
	private final String DB_PASSWORD = "rSIMcFhIzMEiEVQr";

	/**
	 * Database constructor. Check Driver and establish connection to database
	 */
	public Database() {
		try {
			Class.forName(this.DRIVER_NAME);
			connection = DriverManager.getConnection(this.DB_URL, this.DB_USERNAME, this.DB_PASSWORD);
			System.out.println("Database Connection Established");
		} catch (ClassNotFoundException e) {
			String log = "Driver `" + this.DRIVER_NAME + "` not found!";
			System.err.println(log);
			this.errorMessage = log;
		} catch (SQLException e) {
			String log = "Connection to `" + this.DB_URL + "` error : " + e.getMessage();
			this.errorMessage = log;
			System.err.println(log);
		}
	}

	protected void finalize() {
		try {
			this.connection.close();
			System.out.println("Db con close");
		} catch (SQLException e) {
			System.err.println("Database connection close failed !");
		}
	}

	protected String getErrorMessage() {
		return this.errorMessage;
	}

	protected String getForeignKeysQuery(String tableName) {
		return "SELECT TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE  REFERENCED_TABLE_SCHEMA = 'si_vol' AND  REFERENCED_TABLE_NAME = '"
				+ tableName + "';";
	}

	protected List<BeanReference> getForeignReferenceFor(Object bean) throws SQLException {
		String className = bean.getClass().getName();
		String[] classBundles = className.split("[.]");
		String tableName = classBundles[classBundles.length - 1].toLowerCase();
		List<BeanReference> dataObjects = new ArrayList<BeanReference>();
		Statement statement = this.connection.createStatement();
		ResultSet result = statement.executeQuery(this.getForeignKeysQuery(tableName));

		System.out.println(this.getForeignKeysQuery(tableName));

		while (result.next()) {
			BeanReference data = new BeanReference();

			data.TABLE_NAME = result.getString("TABLE_NAME");
			data.COLUMN_NAME = result.getString("COLUMN_NAME");
			data.REFERENCED_TABLE_NAME = result.getString("REFERENCED_TABLE_NAME");
			data.REFERENCED_COLUMN_NAME = result.getString("REFERENCED_COLUMN_NAME");

			dataObjects.add(data);
		}
		return dataObjects;
	}

	/**
	 * Get data from database for the Bean object
	 * 
	 * @param bean
	 * @return Json object
	 */
	protected JsonBuilder getBeanData(Object bean, Object where) {
		Statement statement;
		ResultSet result;
		String className = bean.getClass().getName();
		String[] classBundles = className.split("[.]");
		String tableName = classBundles[classBundles.length - 1].toLowerCase();
		Field[] classFields = bean.getClass().getFields();
		List<Object> dataObjects = new ArrayList<Object>();
		JsonBuilder json = new JsonBuilder();

		if (this.errorMessage == null) {
			try {
				statement = this.connection.createStatement();
				result = statement.executeQuery("SELECT * FROM " + tableName);
				System.out.println("SELECT * FROM " + tableName);

				while (result.next()) {

					Object data = bean.getClass().getDeclaredConstructor().newInstance();

					for (Field field : classFields) {
						String fieldName = field.getName();
						field.set(data, result.getObject(fieldName));
					}

					dataObjects.add(data);
				}

				json.setData(dataObjects);
				json.setStatus(Status.OK);

			} catch (SQLException e) {
				e.printStackTrace();
				json.setStatus(Status.DB_SQL_ERR);
				json.errorMessage = e.getMessage();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				json.setStatus(Status.INTERNAL_EXCEPTION);
				json.errorMessage = "Serious server exception raised. Contact Administrator with this message:\r\n"
						+ e.getStackTrace();
			}

		} else {
			json.setStatus(Status.DB_CON_FAIL);
			json.errorMessage = this.errorMessage;
		}

		return json;
	}

	public List<Object> getBeanData(Object bean)
			throws SQLException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException {
		Statement statement = this.connection.createStatement();
		ResultSet result;
		String query;
		String className = bean.getClass().getName();
		String[] classBundles = className.split("[.]");
		String tableName = classBundles[classBundles.length - 1].toLowerCase();
		Field[] classFields = bean.getClass().getFields();
		List<Object> dataObjects = new ArrayList<Object>();

		String whereClause = "";
		for (Field field : classFields) {
			String fieldName = field.getName();
			Object fieldData = bean.getClass().getField(fieldName).get(bean);

			if (fieldData != null) {
				if (whereClause != "")
					whereClause += " AND ";
				whereClause += fieldName + "=\"" + fieldData + "\"";
			}
		}

		query = "SELECT * FROM " + tableName;
		if (whereClause != "")
			query += " WHERE " + whereClause;

		result = statement.executeQuery(query);
		System.out.println(query);

		while (result.next()) {

			Object data = bean.getClass().getDeclaredConstructor().newInstance();

			for (Field field : classFields) {
				String fieldName = field.getName();
				field.set(data, result.getObject(fieldName));
			}

			dataObjects.add(data);
		}

		return dataObjects;
	}

	public Integer setBeanData(Object bean) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, SQLException {
		PreparedStatement statement;
		String className = bean.getClass().getName();
		String[] classBundles = className.split("[.]");
		String tableName = classBundles[classBundles.length - 1].toLowerCase();
		Field[] classFields = bean.getClass().getFields();
		Integer updateId = -1;
		String queryAction = "INSERT INTO";
		String queryInsertTable = "";
		String queryInsertValue = "";
		String queryUpdate = "";
		String queryWhereClause = "";
		String query = "";
		
		for (Field field : classFields) {
			String fieldName = field.getName();
			Object fieldData = bean.getClass().getField(fieldName).get(bean);
			if (fieldName.equals(tableName + "_id") && fieldData != null) {
				queryAction = "UPDATE";
				queryWhereClause = "WHERE " + fieldName + "=" + fieldData;
				updateId = (Integer) fieldData;
			}
			else if (fieldData != null) {
				if (queryInsertTable != "" && queryInsertValue != "" && queryUpdate != "") {
					queryInsertTable += ", ";
					queryInsertValue += ", ";
					queryUpdate += ", ";
				}
				queryInsertTable += fieldName;
				queryInsertValue += "\"" + fieldData + "\"";
				queryUpdate += fieldName + "=\"" + fieldData + "\"";
			}
		}
		
		if (queryAction == "INSERT INTO") {
			query = queryAction + " " + tableName + "(" + queryInsertTable + ") VALUES (" + queryInsertValue + ");";
		}
		else {
			query = queryAction + " " + tableName + " SET " + queryUpdate + " " + queryWhereClause;
		}
		
		System.out.println(query);
		statement = this.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		statement.executeUpdate();
		
		if (queryAction == "INSERT INTO") {
			ResultSet keys = statement.getGeneratedKeys();
			keys.next();
			return keys.getInt(1);			
		}
		else {
			return updateId;
		}
	}
	
	public void deleteObject(Object bean) throws SQLException, IllegalArgumentException, IllegalAccessException,
			NoSuchFieldException, SecurityException {
		PreparedStatement statement;
		String className = bean.getClass().getName();
		String[] classBundles = className.split("[.]");
		String tableName = classBundles[classBundles.length - 1].toLowerCase();
		Field[] classFields = bean.getClass().getFields();
		String query = "DELETE FROM " + tableName;
		String whereClause = "";

		for (Field field : classFields) {
			String fieldName = field.getName();
			Object fieldData = bean.getClass().getField(fieldName).get(bean);

			if (fieldData != null) {
				if (whereClause != "")
					whereClause += " AND ";
				whereClause += fieldName + "=\"" + fieldData + "\"";
			}
		}

		if (whereClause != "")
			query += " WHERE " + whereClause;

		statement = this.connection.prepareStatement(query);
		statement.executeUpdate();
		System.out.println(query);
	}
}
