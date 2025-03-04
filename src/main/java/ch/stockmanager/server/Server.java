package ch.stockmanager.server;

import io.javalin.Javalin;

import ch.stockmanager.server.controllers.*;
import ch.stockmanager.server.util.DBInfo;

public class Server {
	// Database credentials
	private static String DB_HOST = "localhost";
	private static int DB_PORT = 5666;
	private static final String DB_NAME = System.getenv("POSTGRES_DB");
	private static final String USER = System.getenv("POSTGRES_USER");
	private static final String PASSWORD = System.getenv("POSTGRES_PASSWORD");

	// Web app information
	private static final int APP_PORT = 25565;

	public static void main(String[] args) {
		if (DB_NAME == null || USER == null || PASSWORD == null)
			throw new NullPointerException("The POSTGRES_DB, POSTGRES_USER and POSTGRES_PASSWORD environment variables need to be set.");

		if (args.length == 4) {
			if (!args[0].equals("--host") || !args[2].equals("--port"))
				throw new IllegalArgumentException("Invalid start parameter(s)");

			DB_HOST = args[1];
			DB_PORT = Integer.parseInt(args[3]);
		}

		DBInfo dbInfo = new DBInfo(
			String.format("jdbc:postgresql://%s:%d/%s",
				DB_HOST,
				DB_PORT,
				DB_NAME
			),
			USER,
			PASSWORD
		);

		var productController = new ProductController(dbInfo);
		var salesController = new Salescontroller(dbInfo);
		var shelfController = new ShelfController(dbInfo);
		var productShelfController = new ProductShelfController(dbInfo);
		var supplierController = new SupplierController(dbInfo);

		Javalin app = Javalin.create();

		// Products
		app.get("/api/products/all", productController::getAll)
			.get("/api/products/soonExpired", productController::getSoonExpired)
			.get("/api/products/expired", productController::getExpired)
			.get("/api/products/orders", productController::getOrders)
			.get("/api/products/{code}", productController::getOne)
			.post("/api/products", productController::insert)
			.delete("/api/products/{code}", productController::delete);

		// Sales
		app.get("/api/sales/all", salesController::getAllWithSearch)
			.get("/api/sales/{date}_{code}", salesController::getOne)
			.post("/api/sales", salesController::insert);

		// ProductShelf
		app.get("/api/shelves/products/all", productShelfController::getAllWithSearch)
			.get("/api/shelves/products/{productCode}_{shelfId}", productShelfController::getOne)
			.post("/api/shelves/products", productShelfController::insert)
			.delete("/api/shelves/products/{productCode}_{shelfId}", productShelfController::delete);
		// Shelves
		app.post("/api/shelves", shelfController::insert)
			.get("/api/shelves/all", shelfController::getAll)
			.put("/api/shelves/{id}", shelfController::update)
			.get("/api/shelves/{id}", shelfController::getOne)
			.delete("/api/shelves/{id}", shelfController::delete);

		// Supplier
		app.post("/api/suppliers", supplierController::insert)
			.get("/api/suppliers/all", supplierController::getAll)
			.get("/api/suppliers/{id}/products", supplierController::getSupplierProducts)
			.get("/api/suppliers/{id}", supplierController::getOne)
			.delete("/api/suppliers/{id}", supplierController::delete)
			.put("/api/suppliers/{id}", supplierController::update);

		app.start(APP_PORT);
	}
}
