package ch.stockmanager.client.views;

import java.util.List;

import ch.stockmanager.client.Client;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import ch.stockmanager.client.util.HTTPHelper;
import ch.stockmanager.types.ProductDateQuantity;

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

	private TableView<ProductDateQuantity> getTable() {
		TableView<ProductDateQuantity> table = new TableView<>();

		TableColumn<ProductDateQuantity, String> columnName = new TableColumn<>("Nom du Product");
		TableColumn<ProductDateQuantity, String> columnExpiryDate = new TableColumn<>("Date péremption");
		TableColumn<ProductDateQuantity, String> columnQuantity = new TableColumn<>("Quantité");

		columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		columnExpiryDate.setCellValueFactory(new PropertyValueFactory<>("date"));
		columnQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

		table.getColumns().add(columnName);
		table.getColumns().add(columnExpiryDate);
		table.getColumns().add(columnQuantity);

		return table;
	}

	private VBox getTables() {
		TableView<ProductDateQuantity> expiredProductsTable = getTable();
		TableView<ProductDateQuantity> soonExpiredProductsTable = getTable();

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
