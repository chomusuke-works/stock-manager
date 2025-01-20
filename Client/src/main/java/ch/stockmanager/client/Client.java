package ch.stockmanager.client;//import javafx.scene.Scene;
//import javafx.stage.Stage;
//import views.*;
//
//public class stockmanager.client.Client extends Application {
//
//	@Override
//	public void start(Stage stage) {
//		// Instanciation des nouvelles vues
//		VueLocalisationRayons vueLocalisationRayons = new VueLocalisationRayons();
//		VueProduitsExpires vueProduitsExpires = new VueProduitsExpires();
//		VueVentesDechets vueVentesDechets = new VueVentesDechets();
//		VueGestionCommandes vueGestionCommandes = new VueGestionCommandes();
//		VueFournisseurs vueFournisseurs = new VueFournisseurs();
//		VueDashboard vueDashboard = new VueDashboard();
//
//		// Création des scènes
//		Scene scene1 = new Scene(vueLocalisationRayons, 1000, 600);
//		Scene scene2 = new Scene(vueProduitsExpires, 1000, 600);
//		Scene scene3 = new Scene(vueVentesDechets, 1000, 600);
//		Scene scene4 = new Scene(vueGestionCommandes, 1000, 600);
//		Scene scene5 = new Scene(vueFournisseurs, 1000, 600);
//		Scene scene6 = new Scene(vueDashboard, 1000, 600);
//
//		stage.setTitle("Application");
//		stage.setScene(scene6); //TODO Changer la scène ici
//		stage.show();
//	}
//
//	public static void main(String[] args) {
//		launch();
//	}
//}

import ch.stockmanager.client.views.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class Client extends Application {

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
		launch();
	}
}

