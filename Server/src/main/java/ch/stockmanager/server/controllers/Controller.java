package ch.stockmanager.server.controllers;

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
		String fileName = "queries/%s_default.sql".formatted(getDataType());
		List<String> queries = getQueries(fileName);

		QUERY_INSERT = queries.get(0);
		QUERY_GETALL = queries.get(1);
		QUERY_GET    = queries.get(2);
		QUERY_DELETE = queries.get(3);
	}

	protected List<String> getExtraQueries() {
		String fileName = "queries/%s_extra.sql".formatted(getDataType());

		return getQueries(fileName);
	}

	public abstract void insert(Context context);
	public abstract void getOne(Context context);
	public abstract void delete(Context context);

	protected abstract String getDataType();

	private String getNextQuery(BufferedReader source) throws IOException {
		StringBuilder query = new StringBuilder();
		String line;
		while ((line = source.readLine()) != null &&
				query.append(line).charAt(query.length() - 1) != ';') {
			query.append('\n');
		}

		return query.toString().trim();
	}

	private List<String> getQueries(String fileName) {
		List<String> queries = new LinkedList<>();

		try {
			var is = new BufferedReader(new InputStreamReader(
				Objects.requireNonNull(Controller.class.getClassLoader().getResourceAsStream(fileName)),
				StandardCharsets.UTF_8
			));

			String query;
			while (!(query = getNextQuery(is)).isEmpty()) {
				queries.add(query);
			}

			is.close();
		} catch (IOException e) {
			throw new RuntimeException("Error reading query file for %s.\n".formatted(getClass().getSimpleName()));
		}

		return queries;
	}
}
