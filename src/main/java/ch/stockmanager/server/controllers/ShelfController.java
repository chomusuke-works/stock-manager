package ch.stockmanager.server.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import ch.stockmanager.server.types.Shelf;
import ch.stockmanager.server.util.*;

public class ShelfController extends Controller {
	private final DBInfo dbInfo;
	private final String QUERY_UPDATE;


	public ShelfController(DBInfo dbInfo) {
		super();

		this.dbInfo = dbInfo;

		List<String> extraQueries = getExtraQueries();
		QUERY_UPDATE = extraQueries.getFirst();
	}

	@Override
	public String getDataType() {
		return "shelf";
	}

	@Override
	public void insert(Context context) {
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_INSERT)
		) {
			Shelf shelf = context.bodyAsClass(Shelf.class);

			statement.setString(1, shelf.name);
			statement.setBoolean(2, shelf.isStock);

			statement.executeUpdate();
			context.status(201);
		} catch (SQLException e) {
			context.status(500);
			context.result("Database error" + e);
		}
	}

	public void getAll(Context context) {
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_GETALL)
		) {
			List<Shelf> shelfs = new LinkedList<>();
			ResultSet results = statement.executeQuery();
			while (results.next()) {
				shelfs.add(getShelf(results));
			}

			context.status(HttpStatus.OK);
			context.json(shelfs);

			results.close();
		} catch (SQLException e) {
			context.status(500);
			context.result("Database error");
		}
	}

	@Override
	public void getOne(Context context) {
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_GET)
		) {
			int id = ContextHelper.getIntPathParam(context, "id");
			statement.setInt(1, id);

			ResultSet results = statement.executeQuery();
			if (!results.next()) {
				context.status(404);

				return;
			}

			context.status(200);
			context.json(getShelf(results));

			results.close();
		} catch (SQLException e) {
			context.status(500);
			context.result("Database error");
		}
	}

	@Override
	public void delete(Context context) {
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_DELETE)
		) {
			int id = ContextHelper.getIntPathParam(context, "id");
			statement.setInt(1, id);
			statement.executeUpdate();

			context.status(200);
		} catch (SQLException e) {
			context.status(500);
			context.result("Database error");
		}
	}

	public void update(Context context) {
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_UPDATE)
		) {
			Shelf shelf = context.bodyAsClass(Shelf.class);
			int id = ContextHelper.getIntPathParam(context, "id");

			statement.setString(1, shelf.name);
			statement.setBoolean(2, shelf.isStock);
			statement.setInt(3, id);

			statement.executeUpdate();
			context.status(HttpStatus.OK);
		} catch (SQLException e) {
			context.status(HttpStatus.INTERNAL_SERVER_ERROR);
			context.result("Database error" + e);
			System.out.println(e.getMessage());
			for (var l : e.getStackTrace()) {
				System.out.println(l);
			}
			System.out.println(e.getSQLState());
		}
	}

	private Shelf getShelf(ResultSet resultSet) throws SQLException {
		return new Shelf(
			resultSet.getInt(1),
			resultSet.getString(2),
			resultSet.getBoolean(3)
		);
	}
}
