package ch.stockmanager.client.views;

import java.util.List;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.converter.NumberStringConverter;

import ch.stockmanager.types.Sale;
import ch.stockmanager.client.util.HTTPHelper;

/**
 * This pane displays all sales and waste data.
 * It allows to search for a specific product, and enter a new sale or waste.
 */
public class SalesPane extends BorderPane {
	public SalesPane() {
        HBox topBar = getTopBar();

        TableView<Sale> salesTable = getTable();

        VBox centerBox = getCenterBox(salesTable);

        HBox transactionBox = getTransactionBox(salesTable.getSelectionModel().selectedItemProperty());

        this.setPadding(new Insets(15));
        BorderPane.setMargin(topBar, new Insets(0, 0, 20, 0));

        this.setTop(topBar);
        this.setCenter(centerBox);
        this.setBottom(transactionBox);
    }

    private HBox getTopBar() {
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setSpacing(10);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label titre = new Label("Ventes & Gestion de Déchets");
        titre.setFont(new Font("Arial", 24));

        Button backButton = new Button("<--");
        backButton.setOnAction(e -> Navigator.goToDashboard());

        topBar.getChildren().addAll(backButton, titre);

        return topBar;
    }

    private TableView<Sale> getTable() {
        TableView<Sale> table = new TableView<>();
        table.setPrefHeight(300);

        // Columns
        TableColumn<Sale, String> columnDate = new TableColumn<>("Date");
        TableColumn<Sale, Long> columnCode = new TableColumn<>("produit");
        TableColumn<Sale, Integer> columnSold = new TableColumn<>("Vendus");
        TableColumn<Sale, Integer> columnThrown = new TableColumn<>("Jetés");

        columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        columnCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        columnSold.setCellValueFactory(new PropertyValueFactory<>("sold"));
        columnThrown.setCellValueFactory(new PropertyValueFactory<>("thrown"));

        table.getColumns().add(columnDate);
        table.getColumns().add(columnCode);
        table.getColumns().add(columnSold);
        table.getColumns().add(columnThrown);

        new Thread(() -> table.getItems().setAll(fetchSales()))
            .start();

        return table;
    }

    private VBox getCenterBox(TableView<Sale> salesTable) {
        VBox box = new VBox(10);

        // Search field
        var searchField = new TextField();
        searchField.setPromptText("Rechercher un produit...");
        searchField.setPrefWidth(200);
        searchField.textProperty().addListener(new FilterListener(salesTable.getItems()));

        box.getChildren().setAll(searchField, salesTable);

        return box;
    }

    private Button getTransactionButton(String buttonText, ObservableValue<Sale> selectedItem, ObservableValue<String> quantitySource) {
        Button button = new Button(buttonText);
        button.setOnAction(event -> {
            Sale selectedSale = selectedItem.getValue();
            if (selectedSale != null) {
                System.out.printf("%s %d %s%n", buttonText, Integer.parseInt(quantitySource.getValue()), selectedSale.code);
                // TODO: API call
            }
        });
        return button;
    }

    private HBox getTransactionBox(ReadOnlyObjectProperty<Sale> selectedSale) {
        HBox box = new HBox(10);
        box.setPadding(new Insets(10, 0, 0, 0));

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantité");
        quantityField.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));

        // Transaction buttons
        ObservableValue<String> quantity = quantityField.textProperty();
        Button buttonSale = getTransactionButton(
            "Vendu",
            selectedSale,
            quantity
        );
        Button ButtonWaste = getTransactionButton(
            "Jeté",
            selectedSale,
            quantity
        );

        box.getChildren().addAll(quantityField, buttonSale, ButtonWaste);

        return box;
    }

    /**
     * Connects to the API to fetch sales data.
     */
    private List<Sale> fetchSales() {
        return HTTPHelper.getList("http://localhost:25565/api/sales/all", Sale.class);
    }

    private class FilterListener implements ChangeListener<String> {

        private final ObservableList<Sale> sales;

        public FilterListener(ObservableList<Sale> sales) {
            this.sales = sales;
        }

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            List<Sale> refreshedSales = fetchSales();
            if (newValue == null || newValue.isEmpty()) {
                sales.setAll(refreshedSales);
            } else {
                ObservableList<Sale> filteredSales = FXCollections.observableArrayList(refreshedSales.stream()
                    .filter(s -> String.valueOf(s.code).contains(newValue))
                    .toList());

                sales.setAll(filteredSales);
            }
        }
    }
}
