package com.hypnotech.airport_pis.model;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.hypnotech.airport_pis.beans.JsonBuilder;
import com.hypnotech.airport_pis.beans.JsonBuilder.Status;
import com.hypnotech.airport_pis.beans.BeanReference;
import com.hypnotech.airport_pis.beans.Destination;

public class DestinationModel extends Database {

	public DestinationModel() {
		super();
	}

	public void finalize() {
		super.finalize();
	}

	public int insertDestination(Destination destination) {
		if (this.connection != null) {
			try {
				PreparedStatement ps = connection.prepareStatement(
						"INSERT INTO destination (destination_name, destination_name_fr, destination_airport_code) VALUES (?, ?, ?);",
						Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, destination.destination_name);
				ps.setString(2, destination.destination_name_fr);
				ps.setString(3, destination.destination_airport_code);
				ps.executeUpdate();

				ResultSet keys = ps.getGeneratedKeys();
				keys.next();
				return keys.getInt(1);
			} catch (SQLException e) {
				System.err.println(
						"Query `\"INSERT INTO destination (destination_name, destination_name_fr, destination_airport_code) VALUES (?, ?, ?);\"` failed !");
				e.printStackTrace();
			}
		}

		return -1;
	}

	public boolean updateData(Destination destination) {
		if (this.connection != null) {
			final String query = "UPDATE destination SET destination_name = ?, destination_name_fr = ?, destination_airport_code = ? WHERE destination_id = ?;";
			try {
				PreparedStatement ps = connection.prepareStatement(query);
				ps.setString(1, destination.destination_name);
				ps.setString(2, destination.destination_name_fr);
				ps.setString(3, destination.destination_airport_code);
				ps.setInt(4, destination.destination_id);
				ps.executeUpdate();

				return true;
			} catch (SQLException e) {
				System.err.println("Query `" + query + "` failed !");
				e.printStackTrace();
			}
		}

		return false;
	}

	public List<Destination> getAllDestinations() {
		if (this.connection == null)
			return new ArrayList<Destination>();

		List<Destination> destinations = new ArrayList<Destination>();
		Statement statement = null;
		ResultSet resultat = null;

		try {
			statement = this.connection.createStatement();
			resultat = statement.executeQuery("SELECT * FROM destination ORDER BY destination_name;");

			while (resultat.next()) {
				Destination newDest = new Destination();
				newDest.destination_id = resultat.getInt("destination_id");
				newDest.destination_name = resultat.getString("destination_name");
				newDest.destination_name_fr = resultat.getString("destination_name_fr");
				newDest.destination_airport_code = resultat.getString("destination_airport_code");

				destinations.add(newDest);
			}
		} catch (SQLException e) {
			System.err.println("Query `SELECT * FROM destination ORDER BY destination_name` failed !");
			e.printStackTrace();
		} finally {
			try {
				if (resultat != null)
					resultat.close();
				if (statement != null)
					statement.close();
			} catch (SQLException e) {
				System.err.println(
						"Query `SELECT * FROM destination ORDER BY destination_name` resultat or statement close() failed !");
			}

		}

		return destinations;
	}

	public JsonBuilder getLinkedEntry(Integer objectId) {
		Destination dest = new Destination();
		dest.destination_id = objectId;
		JsonBuilder json = new JsonBuilder();
		List<BeanReference> foreignRefs;
		List<Object> linkedEntries = new ArrayList<Object>();

		try {
			foreignRefs = this.getForeignReferenceFor(dest);

			for (BeanReference ref : foreignRefs) {
				String className = ref.TABLE_NAME.substring(0, 1).toUpperCase() + ref.TABLE_NAME.substring(1);
				Object o = Class.forName("com.hypnotech.airport_pis.beans." + className).getDeclaredConstructor()
						.newInstance();
				o.getClass().getField(ref.COLUMN_NAME).set(o, objectId);
				linkedEntries.add(this.getBeanData(o));
			}

			json.setData(linkedEntries);
			json.setStatus(Status.OK);
		} catch (SQLException e) {
			e.printStackTrace();
			json.setStatus(Status.DB_SQL_ERR);
			json.errorMessage = e.getMessage();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NoSuchFieldException | ClassNotFoundException e) {
			e.printStackTrace();
			json.setStatus(Status.INTERNAL_EXCEPTION);
			json.errorMessage = "Serious server exception raised. Contact Administrator with this message:\r\n"
					+ e.getStackTrace();
		}

		return json;
	}
}
