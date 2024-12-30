package app.controllers;

import app.types.Product;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductController extends CRUDController<Product> {
	public ProductController(Connection connection) throws SQLException {
		super(connection);
	}

	@Override
	public String getDataType() {
		return "product";
	}

	@Override
	protected void prepareInsertQuery(PreparedStatement statement, Product product) throws SQLException {
		statement.setInt(1, product.code());
		statement.setString(2, product.name());
		statement.setDouble(3, product.price());
		statement.setString(4, product.category());
	}

	@Override
	protected Product getResult(ResultSet resultSet) throws SQLException {
		return new Product(
			resultSet.getInt(1),
			resultSet.getString(2),
			resultSet.getDouble(3),
			resultSet.getString(4)
		);
	}

	@Override
	protected Product extractObject(Context ctx) {
		return new Product(
			Integer.parseInt(ctx.pathParam("code")),
			ctx.pathParam("name"),
			Double.parseDouble(ctx.pathParam("price")),
			ctx.pathParam("category")
		);
	}

	@Override
	protected int getId(Context ctx) {
		try {
			return Integer.parseInt(ctx.pathParam("code"));
		} catch (NumberFormatException e) {
			throw new RuntimeException("Invalid product code");
		}
	}
}
