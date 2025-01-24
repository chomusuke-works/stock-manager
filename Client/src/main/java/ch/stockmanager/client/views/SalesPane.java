package ch.stockmanager.client.views;


import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.BiFunction;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.converter.NumberStringConverter;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

import ch.stockmanager.types.Sale;
import ch.stockmanager.client.util.HTTPHelper;
import ch.stockmanager.client.Client;
import ch.stockmanager.client.util.JavaFxHelper;

/**
 * This pane displays all sales and waste data.
 * It allows to search for a specific product, and enter a new sale or waste.
 */
public class SalesPane extends BorderPane {
    private final String PATH_PREFIX = String.format("http://%s/api/sales", Client.SERVER_IP);

    /**
     * Connect to the API to update the total of units sold a thrown on a date, for one product
     * @param sold the quantity of product that has just been sold
     * @param thrown the quantity of product that has just been thrown
     * @param code the product code
     */
    private void sell (int sold, int thrown, long code) {
        HTTPHelper.put(PATH_PREFIX, new Sale(today(), code, sold, thrown));
    }

    /**
     * Connects to the API to fetch sales data.
     */
    private List<Sale> fetchSales() {
        return HTTPHelper.getList(String.format("%s/all", PATH_PREFIX), Sale.class);
    }

	public SalesPane() {
        this.setPadding(new Insets(15));

        // Components (Structure) :

        // - Title
        Label title = new Label("Ventes & Gestion de Déchets");
        title.setFont(new Font("Arial", 24));
        BorderPane.setMargin(title, new Insets(0, 0, 20, 0));

        // - Main box -> search bar and table of sales
        TableView<Sale> salesTable = JavaFxHelper.getTable(
            new String[]{"Date", "Produit", "Vendus", "Jetés"},
            new String[]{"date", "code", "sold", "thrown"}
        );

        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher un produit...");

        VBox mainBox = new VBox(searchField, salesTable);

        StringProperty quantity;
        StringProperty productCode;
        HBox transactionBox; {
            TextField productQuantityField = new TextField();
            productQuantityField.setPromptText("Quantité");
            quantity = productQuantityField.textProperty();

            TextField productCodeField = new TextField();
            productCodeField.setPromptText("Code Produit");
            productQuantityField.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
            productCode = productCodeField.textProperty();

            Button sellButton = new Button("Vendu");
            Button throwButton = new Button("Jeté");

            BiFunction<ObservableValue<String>, ObservableValue<String>, EventHandler<ActionEvent>> handler = (sold, thrown) ->
                (EventHandler<ActionEvent>) event -> {
                    if (quantity.getValue().isEmpty()) return;
                    try {
                        int s = (sold == null || sold.getValue().isEmpty()) ?
                            0 : Integer.parseInt(sold.getValue());
                        int t = (thrown == null || thrown.getValue().isEmpty()) ?
                            0 : Integer.parseInt(thrown.getValue());
                        sell(s, t, Long.parseLong(productCodeField.getText()));
                        // fetch the updated data after modification
                        salesTable.getItems().setAll(fetchSales());
                    } catch (NumberFormatException ex) {
                        JavaFxHelper.showAlert("Invalid code format", "The product code is invalid, or you have not selected any product.");
                    }
                };

            sellButton.setOnAction(handler.apply(quantity, null));
            throwButton.setOnAction(handler.apply(null, quantity));

            transactionBox = new HBox(10, productQuantityField, productCodeField, sellButton, throwButton);
        }


        // Size preferences
        salesTable.setPrefHeight(300);
        searchField.setPrefWidth(200);

        // Logic
        // - Update and filter the sales field when the content of the search bar changes
        searchField.textProperty().addListener(new FilterListener(salesTable.getItems()));
        // - Update product code field when selecting an item in the table
        salesTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSelection, newSelection) -> {
                    if (newSelection != null)
                        productCode.setValue(String.valueOf(newSelection.code));
                });

        // Pane creation
        this.setTop(title);
        this.setCenter(mainBox);
        this.setBottom(transactionBox);

        // Initialisation (real data)
        new Thread(() -> salesTable.getItems().setAll(fetchSales()))
                .start();
    }

    /**
     * Reload and filter the sale table when the content of the search bar changes.
     */
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

    /**
     * @return today's date as a string, in format "yyyy-MM-dd" as used by the api and database
     */
    private String today() {
        Date date = Date.from(Instant.now());
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDate.format(formatter);
    }
}
