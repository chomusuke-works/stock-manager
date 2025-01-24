package ch.stockmanager.client.views;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import ch.stockmanager.types.ProductDateQuantity;
import ch.stockmanager.client.util.HTTPHelper;
import ch.stockmanager.client.Client;
import ch.stockmanager.client.util.JavaFxHelper;

public class ExpiryDatesPane extends BorderPane {
	public ExpiryDatesPane() {
		this.setPadding(new Insets(15));

		Label title = new Label("Dates d'expiration proches");
		title.setFont(new Font("Arial", 24));
		BorderPane.setMargin(title, new Insets(0, 0, 20, 0));

		VBox tables = getTables();

		this.setTop(title);
		this.setCenter(tables);
	}

	private List<ProductDateQuantity> fetchData(EntryType entryType) {
		return HTTPHelper.getList(String.format("http://%s/api/products/%s", Client.SERVER_IP, entryType.getPathSuffix()), ProductDateQuantity.class);
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

		Label expiredLabel = new Label("Produits expirés :");
		Label soonExpiredLabel = new Label("Produits bientôt expirés :");

		VBox tables = new VBox(10,
			expiredLabel,
			expiredProductsTable,
			soonExpiredLabel,
			soonExpiredProductsTable
		);

		new Thread(() -> {
			expiredProductsTable.setItems(FXCollections.observableArrayList(fetchData(EntryType.EXPIRED)));
			soonExpiredProductsTable.setItems(FXCollections.observableArrayList(fetchData(EntryType.SOON_EXPIRED)));
		}).start();

		return tables;
	}

	private enum EntryType {
		EXPIRED("expired"),
		SOON_EXPIRED("soonExpired");

		private final String pathSuffix;

		EntryType(String pathSuffix) {
			this.pathSuffix = pathSuffix;
		}

		public String getPathSuffix() {
			return pathSuffix;
		}
	}
}
