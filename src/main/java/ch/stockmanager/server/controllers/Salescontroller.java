package ch.stockmanager.server.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import ch.stockmanager.server.types.Sale;
import ch.stockmanager.server.util.*;

public class Salescontroller extends Controller {
	private final DBInfo dbInfo;

	public Salescontroller(DBInfo dbInfo) {
		super();

		this.dbInfo = dbInfo;
	}

	@Override
	public void insert(Context context) {
		try (
			var connection = dbInfo.getConnection();
			var insertStatement = connection.prepareStatement(QUERY_INSERT)
		) {
			Sale sale = context.bodyAsClass(Sale.class);

			insertStatement.setTimestamp(1, sale.getTimestamp());
			insertStatement.setLong(2, sale.getProductCode());
			insertStatement.setString(3, sale.getProductName());
			insertStatement.setInt(4, sale.getSold());
			insertStatement.setInt(5, sale.getThrown());

			insertStatement.executeUpdate();
		} catch (SQLException e) {
			context.result("Database error.");
			context.status(HttpStatus.INTERNAL_SERVER_ERROR);

			return;
		}

		context.status(HttpStatus.CREATED);
	}

	public void getAllWithSearch(Context context) {
		List<Sale> sales = new LinkedList<>();

		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_GETALL)
		) {
			String searchTerm = context.queryParam("searchTerm");
			searchTerm = searchTerm == null ? "" : searchTerm;
			searchTerm = '%' + searchTerm + '%';
			statement.setString(1, searchTerm);

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				var sale = new Sale(
					results.getTimestamp(1),
					results.getLong(2),
					results.getString(3),
					results.getInt(4),
					results.getInt(5)
				);

				sales.add(sale);
			}

			results.close();
		} catch (SQLException e) {
			context.result("Database error.");
			context.status(HttpStatus.INTERNAL_SERVER_ERROR);

			return;
		} catch (DateTimeParseException e) {
			context.result("Wrong date format. Correct format: yyyy-[M]M-[d]d");
			context.status(HttpStatus.BAD_REQUEST);

			return;
		}

		context.json(sales);
		context.status(HttpStatus.OK);
	}

	@Override
	public void getOne(Context context) {
		var sale = context.bodyAsClass(Sale.class);
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_GET)
		) {
			statement.setTimestamp(1, sale.getTimestamp());
			statement.setLong(2, sale.getProductCode());

			ResultSet results = statement.executeQuery();
			if (results.next()) {
				sale.sold = results.getInt(3);
				sale.thrown = results.getInt(4);
			} else {
				sale.sold = 0;
				sale.thrown = 0;
			}

			results.close();
		} catch (SQLException e) {
			context.result("Database error.");
			context.status(HttpStatus.INTERNAL_SERVER_ERROR);

			return;
		} catch (DateTimeParseException e) {
			context.result("Wrong date format. Correct format: yyyy-[M]M-[d]d");
			context.status(HttpStatus.BAD_REQUEST);

			return;
		}

		context.json(sale);
		context.status(HttpStatus.OK);
	}

	@Override
	public void delete(Context context) {
		context.status(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	protected String getDataType() {
		return "sale";
	}
}
