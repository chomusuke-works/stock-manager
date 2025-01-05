package app.controllers;

import app.util.ContextHelper;
import app.util.DBInfo;
import app.types.Product;
import io.javalin.http.Context;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductController extends Controller {
	private final DBInfo dbInfo;

	public ProductController(DBInfo dbInfo) {
		this.dbInfo = dbInfo;
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
		}
	}

	@Override
	public void getOne(Context context) {
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_GET)
		) {
			long code = ContextHelper.getLongQueryParam(context, "code");
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

	@Override
	public void delete(Context context) {
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_DELETE)
		) {
			long code = ContextHelper.getLongQueryParam(context, "code");
			statement.setLong(1, code);
			statement.executeUpdate();

			context.status(200);
		} catch (SQLException e) {
			context.status(500);
			context.result("Database error");
		}
	}

	private Product getProduct(ResultSet resultSet) throws SQLException {
		return new Product(
			resultSet.getLong(1),
			resultSet.getString(2),
			resultSet.getDouble(3),
			resultSet.getInt(4)
		);
	}
}
