package ch.stockmanager.server.controllers;

import ch.stockmanager.types.Sale;
import ch.stockmanager.server.util.*;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;

public class Salescontroller extends Controller {
	private final String
		QUERY_SALE_EXISTS,
		QUERY_SELL_THROW;

	private final DBInfo dbInfo;

	public Salescontroller(DBInfo dbInfo) {
		super();

		this.dbInfo = dbInfo;
		List<String> extraQueries = getExtraQueries();
		QUERY_SALE_EXISTS = extraQueries.get(0);
		QUERY_SELL_THROW = extraQueries.get(1);
	}

	@Override
	public void insert(Context context) {
		context.status(HttpStatus.NOT_IMPLEMENTED);
	}

	public void getAll(Context context) {
		List<Sale> sales = new LinkedList<>();

		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_GETALL)
		) {
			ResultSet results = statement.executeQuery();
			while (results.next()) {
				var sale = new Sale(
					results.getDate(1).toString(),
					results.getLong(2),
					results.getInt(3),
					results.getInt(4)
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
		var sale = getSale(context);

		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_GET)
		) {
			statement.setDate(1, java.sql.Date.valueOf(sale.getDate()));
			statement.setLong(2, sale.getCode());

			ResultSet results = statement.executeQuery();
			if (results.next()) {
				sale.setSold(results.getInt(3));
				sale.setThrown(results.getInt(4));
			} else {
				sale.setSold(0);
				sale.setThrown(0);
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

	public void sell(Context context) {
		try (
			var connection = dbInfo.getConnection();
			var saleExistsStatement = connection.prepareStatement(QUERY_SALE_EXISTS);
			var updateStatement = connection.prepareStatement(QUERY_SELL_THROW);
			var insertStatement = connection.prepareStatement(QUERY_INSERT)
		) {
			Sale sale = getSale(context);

			saleExistsStatement.setDate(1, Date.valueOf(sale.getDate()));
			saleExistsStatement.setLong(2, sale.getCode());

			boolean saleExists = getBoolAndClose(saleExistsStatement.executeQuery());

			if (saleExists) {
				updateStatement.setInt(1, sale.getSold());
				updateStatement.setInt(2, sale.getThrown());
				updateStatement.setDate(3, Date.valueOf(sale.getDate()));
				updateStatement.setLong(4, sale.getCode());

				updateStatement.executeUpdate();
			} else {
				insertStatement.setDate(1, Date.valueOf(sale.getDate()));
				insertStatement.setLong(2, sale.getCode());
				insertStatement.setInt(3, sale.getSold());
				insertStatement.setInt(4, sale.getThrown());

				insertStatement.executeUpdate();
			}
		} catch (SQLException e) {
			context.result("Database error.");
			context.status(HttpStatus.INTERNAL_SERVER_ERROR);

			return;
		}

		context.status(HttpStatus.OK);
	}

	@Override
	protected String getDataType() {
		return "sale";
	}

	private Sale getSale(Context context) throws NullPointerException, IllegalArgumentException {
		Sale sale = new Sale();

		sale.setDate(context.pathParam("date"));
		sale.setCode(ContextHelper.getLongPathParam(context, "code"));

		try {
			sale.setSold(ContextHelper.getIntQueryParam(context, "sold"));
		} catch (NullPointerException e) {
			sale.setSold(0);
		}

		try {
			sale.setThrown(ContextHelper.getIntQueryParam(context, "thrown"));
		} catch (NullPointerException e) {
			sale.setThrown(0);
		}

		return sale;
	}

	private boolean getBoolAndClose(ResultSet results) throws SQLException {
		results.next();
		boolean r = results.getBoolean(1);
		results.close();

		return r;
	}
}
