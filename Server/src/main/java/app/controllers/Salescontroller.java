package app.controllers;

import app.types.Sale;
import app.util.ContextHelper;
import app.util.DBInfo;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
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

	@Override
	public void getOne(Context context) {
		Date date;
		long code;
		int sold;
		int thrown;

		try {
			date = Date.valueOf(context.pathParam("date"));
		} catch (IllegalArgumentException e) {
			context.result("Wrong date format. Correct format: yyyy-[M]M-[d]d");
			context.status(HttpStatus.BAD_REQUEST);

			return;
		}

		code = ContextHelper.getLongPathParam(context, "code");

		Sale sale;
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_GET)
		) {
			statement.setDate(1, date);
			statement.setLong(2, code);

			ResultSet results = statement.executeQuery();
			if (results.next()) {
				sold = results.getInt(3);
				thrown = results.getInt(4);
			} else {
				sold = 0;
				thrown = 0;
			}

			results.close();
		} catch (SQLException e) {
			context.result("Database error.");
			context.status(HttpStatus.INTERNAL_SERVER_ERROR);

			return;
		}

		sale = new Sale(date, code, sold, thrown);

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
			java.sql.Date sqlDate = new java.sql.Date(sale.date().getTime());

			saleExistsStatement.setDate(1, sqlDate);
			saleExistsStatement.setLong(2, sale.code());

			boolean saleExists = getBoolAndClose(saleExistsStatement.executeQuery());

			if (saleExists) {
				updateStatement.setInt(1, sale.sold());
				updateStatement.setInt(2, sale.thrown());
				updateStatement.setDate(3, sqlDate);
				updateStatement.setLong(4, sale.code());

				updateStatement.executeUpdate();
			} else {
				insertStatement.setDate(1, sqlDate);
				insertStatement.setLong(2, sale.code());
				insertStatement.setInt(3, sale.sold());
				insertStatement.setInt(4, sale.thrown());

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
		Date date;
		long code;
		int sold;
		int thrown;

		date = Date.valueOf(context.pathParam("date"));
		code = ContextHelper.getLongPathParam(context, "code");

		try {
			sold = ContextHelper.getIntQueryParam(context, "sold");
		} catch (NullPointerException e) {
			sold = 0;
		}

		try {
			thrown = ContextHelper.getIntQueryParam(context, "thrown");
		} catch (NullPointerException e) {
			thrown = 0;
		}

		return new Sale(date, code, sold, thrown);
	}

	private boolean getBoolAndClose(ResultSet results) throws SQLException {
		results.next();
		boolean r = results.getBoolean(1);
		results.close();

		return r;
	}
}
