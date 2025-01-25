package ch.stockmanager.client.views;

import ch.stockmanager.client.controllers.ExpiryDatesController;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import ch.stockmanager.types.ProductDateQuantity;
import ch.stockmanager.client.util.JavaFxHelper;

public class ExpiryDatesPane extends BorderPane {

	private final ExpiryDatesController controller;

	public ExpiryDatesPane(ExpiryDatesController controller) {
		this.controller = controller;

		this.setPadding(new Insets(15));

		Label title = new Label("Dates d'expiration proches");
		title.setFont(new Font("Arial", 24));
		BorderPane.setMargin(title, new Insets(0, 0, 20, 0));

		VBox tables = getTables();

		this.setTop(title);
		this.setCenter(tables);
	}

	private VBox getTables() {
		TableView<ProductDateQuantity> expiredProductsTable = JavaFxHelper.getTable(
			new String[]{"Produit", "Date", "Quantité"},
			new String[]{"name", "date", "quantity"}
		);
		TableView<ProductDateQuantity> soonExpiredProductsTable = JavaFxHelper.getTable(
			new String[]{"Produit", "Date", "Quantité"},
			new String[]{"name", "date", "quantity"}
		);

		expiredProductsTable.setItems(controller.getExpiredProducts());
		soonExpiredProductsTable.setItems(controller.getSoonExpiredProducts());

		Label expiredLabel = new Label("Produits expirés :");
		Label soonExpiredLabel = new Label("Produits bientôt expirés :");

		return new VBox(10,
			expiredLabel,
			expiredProductsTable,
			soonExpiredLabel,
			soonExpiredProductsTable
		);
	}
}
