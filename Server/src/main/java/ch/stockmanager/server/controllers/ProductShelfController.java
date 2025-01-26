package ch.stockmanager.server.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import io.javalin.http.Context;

import ch.stockmanager.server.util.ContextHelper;
import ch.stockmanager.server.util.DBInfo;
import ch.stockmanager.types.ProductShelf;
import io.javalin.http.HttpStatus;


public class ProductShelfController extends Controller {
	private final DBInfo dbInfo;

	public ProductShelfController(DBInfo dbInfo) {
		super();
		this.dbInfo = dbInfo;
	}

	@Override
	public void insert(Context context) {
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_INSERT)
		) {
			ProductShelf productShelf = context.bodyAsClass(ProductShelf.class);

			statement.setLong(1, productShelf.productCode);
			statement.setInt(2, productShelf.shelfId);

			statement.executeUpdate();
			context.status(201);
		} catch (SQLException e) {
			context.status(HttpStatus.INTERNAL_SERVER_ERROR);
			context.result("Database error");

			throw new RuntimeException(e);
		}
	}

	@Override
	public void getOne(Context context) {
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_GET)
		) {
			long productCode = Long.parseLong(context.pathParam("productCode"));
			int shelfId = Integer.parseInt(context.pathParam("shelfId"));
			statement.setLong(1, productCode);
			statement.setInt(2, shelfId);

			ResultSet results = statement.executeQuery();
			if (results.next()) {
				ProductShelf result = getProductShelf(results);

				context.status(HttpStatus.OK);
				context.json(result);
			} else {
				context.status(HttpStatus.NOT_FOUND);
				context.result("Product not found on given shelf");
			}
		} catch (NumberFormatException e) {
			context.status(HttpStatus.BAD_REQUEST);
			context.result("Correct format: <productCode>_<shelfId>");
		} catch (SQLException e) {
			context.status(HttpStatus.INTERNAL_SERVER_ERROR);

			throw new RuntimeException(e);
		}
	}

	@Override
	public void delete(Context context) {
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_DELETE)
		) {
			long productCode = ContextHelper.getLongPathParam(context, "productCode");
			int shelfId = ContextHelper.getIntPathParam(context, "shelfId");

			statement.setLong(1, productCode);
			statement.setInt(2, shelfId);

			statement.executeUpdate();

			context.status(HttpStatus.OK);
		} catch (SQLException e) {
			context.status(HttpStatus.INTERNAL_SERVER_ERROR);
			context.result("Database error");

			throw new RuntimeException(e);
		}
	}

	@Override
	protected String getDataType() {
		return "product_shelf";
	}

	public void getAll(Context context) {
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_GETALL)
		) {
			List<ProductShelf> products = new LinkedList<>();
			ResultSet results = statement.executeQuery();
			while (results.next()) {
				products.add(getProductShelf(results));
			}

			context.status(HttpStatus.OK);
			context.json(products);
		} catch (SQLException e) {
			context.status(HttpStatus.INTERNAL_SERVER_ERROR);
			context.result("Database error");

			throw new RuntimeException(e);
		}
	}

	private ProductShelf getProductShelf(ResultSet resultSet) throws SQLException {
		return new ProductShelf(
			resultSet.getLong(1),
			resultSet.getString(2),
			resultSet.getInt(3),
			resultSet.getString(4),
			resultSet.getString(5)
		);
	}
}
