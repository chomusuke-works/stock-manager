package ch.stockmanager.server.controllers;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import ch.stockmanager.types.Sale;
import ch.stockmanager.server.util.*;

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
			statement.setDate(1, java.sql.Date.valueOf(sale.date));
			statement.setLong(2, sale.code);

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

	public void sell(Context context) {
		try (
			var connection = dbInfo.getConnection();
			var saleExistsStatement = connection.prepareStatement(QUERY_SALE_EXISTS);
			var updateStatement = connection.prepareStatement(QUERY_SELL_THROW);
			var insertStatement = connection.prepareStatement(QUERY_INSERT)
		) {
			Sale sale = getSale(context);

			saleExistsStatement.setDate(1, Date.valueOf(sale.date));
			saleExistsStatement.setLong(2, sale.code);

			boolean saleExists = getBoolAndClose(saleExistsStatement.executeQuery());

			if (saleExists) {
				updateStatement.setInt(1, sale.sold);
				updateStatement.setInt(2, sale.thrown);
				updateStatement.setDate(3, Date.valueOf(sale.date));
				updateStatement.setLong(4, sale.code);

				updateStatement.executeUpdate();
			} else {
				insertStatement.setDate(1, Date.valueOf(sale.date));
				insertStatement.setLong(2, sale.code);
				insertStatement.setInt(3, sale.sold);
				insertStatement.setInt(4, sale.thrown);

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

		sale.date = context.pathParam("date");
		sale.code = ContextHelper.getLongPathParam(context, "code");

		try {
			sale.sold = ContextHelper.getIntQueryParam(context, "sold");
		} catch (NullPointerException e) {
			sale.sold = 0;
		}

		try {
			sale.thrown = ContextHelper.getIntQueryParam(context, "thrown");
		} catch (NullPointerException e) {
			sale.thrown = 0;
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
