package ch.stockmanager.client.views;

import ch.stockmanager.types.ProductDateQuantity;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import ch.stockmanager.client.util.HTTPHelper;

import java.io.IOException;
import java.util.List;

public class ExpiryDatesPane extends BorderPane {
	public ExpiryDatesPane() {
		this.setPadding(new Insets(15));

		HBox topBar = getTopBar();
		BorderPane.setMargin(topBar, new Insets(0, 0, 20, 0));

		VBox tables = getTables();

		this.setTop(topBar);
		this.setCenter(tables);
	}

	private List<ProductDateQuantity> fetchData(EntryType entryType) throws IOException {
		return HTTPHelper.getList("http://localhost:25565/api/products/" + entryType.getPathSuffix(), ProductDateQuantity.class);
	}

	private HBox getTopBar() {
		HBox topBar = new HBox();
		topBar.setPadding(new Insets(10));
		topBar.setSpacing(10);
		topBar.setAlignment(Pos.CENTER_LEFT);

		Label titre = new Label("Dates d'expiration proches");
		titre.setFont(new Font("Arial", 24));

		// Bouton retour en haut à droite
		Button backButton = new Button("<--");
		backButton.setOnAction(e -> Navigator.goToDashboard());

		topBar.getChildren().addAll(backButton, titre);

		return topBar;
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
			try {
				expiredProductsTable.setItems(FXCollections.observableArrayList(fetchData(EntryType.EXPIRED)));
				soonExpiredProductsTable.setItems(FXCollections.observableArrayList(fetchData(EntryType.SOON_EXPIRED)));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
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
