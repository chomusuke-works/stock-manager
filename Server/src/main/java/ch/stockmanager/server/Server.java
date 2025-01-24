package ch.stockmanager.server;

import io.javalin.Javalin;

import ch.stockmanager.server.controllers.ProductShelfController;
import ch.stockmanager.server.controllers.Salescontroller;
import ch.stockmanager.server.controllers.ShelfController;
import ch.stockmanager.server.util.DBInfo;
import ch.stockmanager.server.controllers.ProductController;

public class Server {
	// Database credentials
	private static String DB_HOST = "locahost";
	private static int DB_PORT = 5666;
	private static final String DB_NAME = "stoman";
	private static final String USER = "kowag";
	private static final String PASSWORD = "cjcex@&08GWzqRy6zMCqBR7E%ZYCFM5f";

	// Web app information
	private static final int APP_PORT = 25565;

	public static void main(String[] args) {
		if (args.length == 4) {
			if (!args[0].equals("--host") || !args[2].equals("--port")) {
				System.err.println("Invalid start parameter(s)");

				return;
			}

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
		app.get("/api/sales/all", salesController::getAll)
			.get("/api/sales/{date}_{code}", salesController::getOne)
			.put("/api/sales/", salesController::sell);

		// Shelves
		app.post("/api/shelves", shelfController::insert)
			.get("/api/shelves/all", shelfController::getAll)
			.put("/api/shelves/{id}", shelfController::update)
			.get("/api/shelves/products", shelfController::getProducts)
			.get("/api/shelves/{id}", shelfController::getOne)
			.delete("/api/shelves/{id}", shelfController::delete);
		// ProductShelf (only relevant for shelves)
		app.post("/api/shelves/products", productShelfController::insert)
			.delete("/api/shelves/products/{productCode}_{shelfId}", productShelfController::delete);

		app.start(APP_PORT);
	}
}
