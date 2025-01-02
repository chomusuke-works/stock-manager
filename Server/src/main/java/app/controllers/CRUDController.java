package app.controllers;

import app.types.DataType;
import io.javalin.http.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class CRUDController<T extends DataType> {
	DBInfo dbInfo;

	private final String
		QUERY_INSERT,
		QUERY_GET,
		QUERY_DELETE;

	public CRUDController(DBInfo dbInfo) {
		this.dbInfo = dbInfo;

		// Load query templates
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
	}

	public void insert(Context ctx) {
		executeQuery(QUERY_INSERT, statement -> {
			T object = extractObject(ctx);
			prepareInsertQuery(statement, object);
		});
	}

	public void get(Context ctx) {
		executeQuery(QUERY_GET,
			statement -> {
				int id = getId(ctx);

				statement.setInt(1, id);
			},
			results -> {
				results.next();

				T object = getResult(results);
				results.close();

				ctx.result(object.toString());
			});
	}

	public void delete(Context ctx) {
		executeQuery(QUERY_DELETE, statement -> {
			int objectId = getId(ctx);
			statement.setInt(1, objectId);
		});
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
	 * @param object    the object from which to pull data
	 * @throws SQLException if a database error occurs during the statement preparation
	 */
	protected abstract void prepareInsertQuery(PreparedStatement statement, T object) throws SQLException;

	/**
	 * Retrieves the first line from a ResultSet according to a template type.
	 * The ResultSet is supposed to have been primed using .next().
	 *
	 * @param resultSet the ResultSet from which to fetch the data. Typically contains only one row
	 * @return the object retrieved from the ResultSet
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


	private void executeQuery(String query, ThrowingConsumer<PreparedStatement> paramSetter) {
		executeQuery(query, paramSetter, null);
	}

	private void executeQuery(
		String query,
		ThrowingConsumer<PreparedStatement> paramSetter,
		ThrowingConsumer<ResultSet> resultConsumer
	) {
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(query)
		) {
			paramSetter.accept(statement);

			ResultSet results = statement.executeQuery();
			if (resultConsumer != null) {
				resultConsumer.accept(results);
			}

			results.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private interface ThrowingConsumer<T> extends Consumer<T> {

		@Override
		default void accept(T t) {
			try {
				applyThrows(t);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		void applyThrows(T t) throws Exception;
	}
}
