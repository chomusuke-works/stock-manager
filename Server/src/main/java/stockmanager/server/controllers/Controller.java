package stockmanager.server.controllers;

import io.javalin.http.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public abstract class Controller {
	protected final String
		QUERY_INSERT,
		QUERY_GETALL,
		QUERY_GET,
		QUERY_DELETE;

	public Controller() {
		// Load basic query templates
		try {
			var is = new BufferedReader(new InputStreamReader(
				Objects.requireNonNull(Controller.class.getClassLoader().getResourceAsStream("queries/%s_default.sql".formatted(getDataType()))),
				StandardCharsets.UTF_8
			));

			QUERY_INSERT = is.readLine();
			QUERY_GETALL = is.readLine();
			QUERY_GET    = is.readLine();
			QUERY_DELETE = is.readLine();

			is.close();
		} catch (IOException e) {
			throw new RuntimeException("Error reading query file for %s.\n".formatted(getClass().getSimpleName()));
		}
	}

	protected List<String> getExtraQueries() {
		List<String> queries = new LinkedList<>();

		try {
			var is = new BufferedReader(new InputStreamReader(
				Objects.requireNonNull(Controller.class.getClassLoader().getResourceAsStream("queries/%s_extra.sql".formatted(getDataType()))),
				StandardCharsets.UTF_8
			));

			String query;
			while ((query = is.readLine()) != null) {
				queries.add(query);
			}

			is.close();
		} catch (IOException e) {
			throw new RuntimeException("Error reading extra query file for %s.\n".formatted(getClass().getSimpleName()));
		}

		return queries;
	}

	public abstract void insert(Context context);
	public abstract void getOne(Context context);
	public abstract void delete(Context context);

	protected abstract String getDataType();
}
