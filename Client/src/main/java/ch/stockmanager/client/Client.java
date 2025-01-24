package ch.stockmanager.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import ch.stockmanager.client.views.*;

public class Client extends Application {

	public static String SERVER_IP = "localhost";

	@Override
	public void start(Stage stage) {
		TabPane mainPane = new TabPane();

		mainPane.getTabs().addAll(
			new Tab("Dates d'expiration", new ExpiryDatesPane()),
			new Tab("Commandes", new OrdersPane()),
			new Tab("Ventes & invendus", new SalesPane()),
			new Tab("Etagères", new ShelvesPane()),
			new Tab("Fournisseurs", new SuppliersPane())
		);
		mainPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

		Scene scene = new Scene(mainPane, 900, 600);
		stage.setScene(scene);
		stage.setTitle("Application - Dashboard");
		stage.show();
	}

	public static void main(String[] args) {
		if (args.length == 2) {
			if (!args[0].equals("--host")) return;

			SERVER_IP = args[1];
		}

		launch();
	}
}

