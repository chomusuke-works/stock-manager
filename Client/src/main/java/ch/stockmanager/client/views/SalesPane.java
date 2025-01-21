package ch.stockmanager.client.views;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.converter.NumberStringConverter;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

import ch.stockmanager.types.Sale;
import ch.stockmanager.client.util.HTTPHelper;

/**
 * This pane displays all sales and waste data.
 * It allows to search for a specific product, and enter a new sale or waste.
 */
public class SalesPane extends BorderPane {
    private final String PATH_PREFIX = "http://localhost:25565/api/sales/";

    private Button buttonSale;
    private Button buttonWaste;

	public SalesPane() {
        this.setPadding(new Insets(15));

        Label title = new Label("Ventes & Gestion de Déchets");
        title.setFont(new Font("Arial", 24));
        BorderPane.setMargin(title, new Insets(0, 0, 20, 0));

        TableView<Sale> salesTable = getTable();

        VBox centerBox = getCenterBox(salesTable);

        HBox transactionBox = getTransactionBox(salesTable);

        this.setTop(title);
        this.setCenter(centerBox);
        this.setBottom(transactionBox);
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

    private Button getTransactionButton(String buttonText, EventHandler<ActionEvent> handler) {
        Button button = new Button(buttonText);
        button.setOnAction(handler);
        return button;
    }

    private HBox getTransactionBox(TableView<Sale> table) {
        ReadOnlyObjectProperty<Sale> selectedSale = table.getSelectionModel().selectedItemProperty();

        HBox box = new HBox(10);
        box.setPadding(new Insets(10, 0, 0, 0));

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantité");
        quantityField.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));

        TextField productCodeField = new TextField();
        productCodeField.setPromptText("Code Produit");

        // Update product code field when selecting an item in the table
        selectedSale.addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                productCodeField.setText(String.valueOf(newSelection.code));
            }
        });

        // Transaction buttons
        ObservableValue<String> quantity = quantityField.textProperty();
        buttonSale = getTransactionButton(
                "Vendu", e -> {
                    if (quantity.getValue().isEmpty()) return;
                    try {
                        sell(Integer.parseInt(quantity.getValue()), 0,
                                Long.parseLong(productCodeField.getText()));
                        updateTable(table);
                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid product code format");
                    }
                }
        );
        buttonWaste = getTransactionButton(
                "Jeté", e -> {
                    if (quantity.getValue().isEmpty()) return;
                    try {
                        sell(0, Integer.parseInt(quantity.getValue()),
                                Long.parseLong(productCodeField.getText()));
                        updateTable(table);
                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid product code format");
                    }
                }
        );

        box.getChildren().addAll(quantityField, productCodeField, buttonSale, buttonWaste);

        return box;
    }

    private void sell (int sold, int thrown, long code) {
        Date date = Date.from(Instant.now());
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = localDate.format(formatter);
        HTTPHelper.put(PATH_PREFIX, new Sale(formattedDate, code, sold, thrown));
    }

    private void updateTable(TableView<Sale> table) {
        //int index = table.getSelectionModel().getSelectedIndex();
        table.getItems().setAll(fetchSales());
        //table.getSelectionModel().select(index);
    }

    /**
     * Connects to the API to fetch sales data.
     */
    private List<Sale> fetchSales() {
        return HTTPHelper.getList(PATH_PREFIX + "all", Sale.class);
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
