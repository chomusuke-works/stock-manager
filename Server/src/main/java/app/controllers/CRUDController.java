package app.controllers;

import app.types.DataType;
import io.javalin.http.Context;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public abstract class CRUDController<T extends DataType> implements Closeable {
	private final PreparedStatement
		insert,
		get,
		delete;

	public CRUDController(Connection connection) throws SQLException {
		final String
			QUERY_INSERT,
			QUERY_GET,
			QUERY_DELETE;

		try {
			var is = new BufferedReader(new InputStreamReader(
				Objects.requireNonNull(CRUDController.class.getClassLoader().getResourceAsStream("queries/%s.sql".formatted(getDataType()))),
				StandardCharsets.UTF_8
			));

			QUERY_INSERT = is.readLine();
			QUERY_GET = is.readLine();
			QUERY_DELETE = is.readLine();

			is.close();
		} catch (IOException e) {
			throw new RuntimeException("Error reading query file for %s.\n".formatted(CRUDController.class.getSimpleName()));
		}

		insert = connection.prepareStatement(QUERY_INSERT);
		get = connection.prepareStatement(QUERY_GET);
		delete = connection.prepareStatement(QUERY_DELETE);
	}

	public void insert(Context ctx) {
		try {
			T object = extractObject(ctx);
			prepareInsertQuery(insert, object);

			insert.executeUpdate();
			insert.clearParameters();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public T get(Context ctx) {
		try {
			int id = getId(ctx);

			get.setInt(1, id);
			ResultSet results = get.executeQuery();
			results.next();

			T object = getResult(results);
			results.close();
			get.clearParameters();

			return object;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void delete(Context ctx) {
		try {
			int objectId = getId(ctx);
			delete.setInt(1, objectId);

			delete.executeUpdate();
			delete.clearParameters();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * The data type must correspond to a .sql source file according to queryFilesSpec.md.
	 *
	 * @return the name of the data type
	 */
	public abstract String getDataType();

	// Utility methods redefined in child classes to interact with the JDBC

	/**
	 * Prepares the PreparedStatement with the correct object data
	 *
	 * @param statement the statement to prepare
	 * @param object the object from which to pull data
	 *
	 * @throws SQLException if a database error occurs during the statement preparation
	 */
	protected abstract void prepareInsertQuery(PreparedStatement statement, T object) throws SQLException;

	/**
	 * Retrieves the first line from a ResultSet according to a template type.
	 * The ResultSet is supposed to have been primed using .next().
	 *
	 * @param resultSet the ResultSet from which to fetch the data. Typically contains only one row
	 *
	 * @return the object retrieved from the ResultSet
	 *
	 * @throws SQLException if a database error occurs when parsing the data
	 */
	protected abstract T getResult(ResultSet resultSet) throws SQLException;

	protected abstract T extractObject(Context ctx);

	protected int getId(Context ctx) {
		try {
			return Integer.parseInt(ctx.pathParam("id"));
		} catch (NumberFormatException e) {
			throw new RuntimeException(e);
		}
	}

	public void close() {
		try {
			insert.close();
			get.close();
			delete.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
