package ch.stockmanager.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import ch.stockmanager.client.controllers.*;
import ch.stockmanager.client.views.*;

public class Client extends Application {

	private static String serverIP = "localhost";

	@Override
	public void start(Stage stage) {
		var expiryDatesController = new ExpiryDatesController(serverIP);
		var ordersController = new OrdersController(serverIP);
		var salesController = new SalesController(serverIP, ordersController);
		var shelvesController = new ShelvesController(serverIP);
		var suppliersController = new SuppliersController(serverIP);

		TabPane mainPane = new TabPane();

		mainPane.getTabs().addAll(
			new Tab("Dates d'expiration", new ExpiryDatesPane(expiryDatesController)),
			new Tab("Commandes", new OrdersPane(ordersController)),
			new Tab("Ventes & invendus", new SalesPane(salesController)),
			new Tab("Rayons", new ShelvesPane(shelvesController)),
			new Tab("Fournisseurs", new SuppliersPane(suppliersController))
		);
		mainPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

		Scene scene = new Scene(mainPane, 900, 600);
		stage.setScene(scene);
		stage.setTitle("Stock Manager");
		stage.show();

		expiryDatesController.update();
		ordersController.update();
		salesController.update();
		shelvesController.update();
		suppliersController.update();
	}

	public static void main(String[] args) {
		if (args.length == 2) {
			if (!args[0].equals("--host")) return;

			serverIP = args[1];
		}

		launch();
	}
}

