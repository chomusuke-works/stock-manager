package app.controllers;

import app.types.productDateQuantity;
import app.util.ContextHelper;
import app.util.DBInfo;
import app.types.Product;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class ProductController extends Controller {
	private final DBInfo dbInfo;

	private final String
		QUERY_GET_SOON_EXPIRED,
		QUERY_GET_EXPIRED;

	private static final int EXPIRY_THRESHOLD = 7;

	public ProductController(DBInfo dbInfo) {
		super();

		this.dbInfo = dbInfo;

		List<String> extraQueries = getExtraQueries();
		QUERY_GET_SOON_EXPIRED = extraQueries.get(0);
		QUERY_GET_EXPIRED = extraQueries.get(1);
	}

	@Override
	public String getDataType() {
		return "product";
	}

	@Override
	public void insert(Context context) {
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_INSERT)
		) {
			Product product = context.bodyAsClass(Product.class);

			statement.setLong(1, product.code());
			statement.setString(2, product.name());
			statement.setDouble(3, product.price());
			statement.setInt(4, product.supplierId());

			statement.executeUpdate();
			context.status(201);
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
			long code = ContextHelper.getLongPathParam(context, "code");
			statement.setLong(1, code);

			ResultSet results = statement.executeQuery();
			if (!results.next()) {
				context.status(404);

				return;
			}

			context.status(200);
			context.json(getProduct(results));

			results.close();
		} catch (SQLException e) {
			context.status(500);
			context.result("Database error");
		}
	}

	public void getAll(Context context) {
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_GETALL)
		) {
			List<Product> products = new LinkedList<>();
			ResultSet results = statement.executeQuery();
			while (results.next()) {
				products.add(getProduct(results));
			}

			context.status(HttpStatus.OK);
			context.json(products);

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
			long code = ContextHelper.getLongPathParam(context, "code");
			statement.setLong(1, code);
			statement.executeUpdate();

			context.status(200);
		} catch (SQLException e) {
			context.status(500);
			context.result("Database error");
		}
	}

	public void getSoonExpired(Context context) {
		List<productDateQuantity> expired = new LinkedList<>();

		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_GET_SOON_EXPIRED)
		) {
			statement.setInt(1, EXPIRY_THRESHOLD);

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				productDateQuantity row = getExpiredProductQuantity(results);

				expired.add(row);
			}
			results.close();

			context.status(HttpStatus.OK);
			context.json(expired);
		} catch (SQLException e) {
			context.status(HttpStatus.INTERNAL_SERVER_ERROR);
			context.result("Database error");
		}
	}

	public void getExpired(Context context) {
		List<productDateQuantity> expired = new LinkedList<>();

		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_GET_EXPIRED)
		) {
			ResultSet results = statement.executeQuery();
			while (results.next()) {
				productDateQuantity row = getExpiredProductQuantity(results);

				expired.add(row);
			}
			results.close();

			context.status(HttpStatus.OK);
			context.json(expired);
		} catch (SQLException e) {
			context.status(HttpStatus.INTERNAL_SERVER_ERROR);
			context.result("Database error");
		}
	}

	private Product getProduct(ResultSet resultSet) throws SQLException {
		int supplierId = resultSet.getInt(4);

		return new Product(
			resultSet.getLong(1),
			resultSet.getString(2),
			resultSet.getDouble(3),
			supplierId
		);
	}

	private productDateQuantity getExpiredProductQuantity(ResultSet resultSet) throws SQLException {
		return new productDateQuantity(
			resultSet.getString(1),
			resultSet.getDate(2),
			resultSet.getInt(3)
		);
	}
}
