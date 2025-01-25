package ch.stockmanager.client.views;

import ch.stockmanager.client.controllers.SalesController;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LongStringConverter;

import ch.stockmanager.types.Sale;
import ch.stockmanager.client.util.JavaFxHelper;

/**
 * This pane displays all sales and waste data.
 * It allows to search for a specific product, and enter a new sale or waste.
 */
public class SalesPane extends BorderPane {
    private final SalesController controller;

	public SalesPane(SalesController controller) {
        this.controller = controller;

        this.setPadding(new Insets(15));

        // - Title
        Label title = new Label("Ventes & Gestion de Déchets");
        title.setFont(new Font("Arial", 24));
        BorderPane.setMargin(title, new Insets(0, 0, 20, 0));

        // - Main box -> search bar and table of sales
        TableView<Sale> salesTable = JavaFxHelper.getTable(
            new String[]{"Date", "Produit", "Vendus", "Jetés"},
            new String[]{"timestamp", "code", "sold", "thrown"}
        );
        salesTable.setItems(controller.getSales());
        salesTable.setPrefHeight(300);

        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher un produit...");
        searchField.setPrefWidth(200);

        VBox mainBox = new VBox(searchField, salesTable);

        HBox transactionBox = getTransactionBox();

        // - Update and filter the sales field when the content of the search bar changes
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
            controller.filterSales(newValue)
        );

        this.setTop(title);
        this.setCenter(mainBox);
        this.setBottom(transactionBox);
    }

    public HBox getTransactionBox() {
        TextField productCodeField = new TextField();
        productCodeField.setPromptText("Code Produit");
        productCodeField.setTextFormatter(new TextFormatter<>(new LongStringConverter()));

        TextField productQuantityField = new TextField();
        productQuantityField.setPromptText("Quantité");
        productQuantityField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));

        LongProperty codeProperty = new SimpleLongProperty();
        codeProperty.bind(productCodeField.textProperty().map(s -> {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                return -1;
            }
        }));

        IntegerProperty quantityProperty = new SimpleIntegerProperty();
        quantityProperty.bind(productQuantityField.textProperty().map(s -> {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return -1;
            }
        }));

        Button sellButton = new Button("Vendu");
        Button throwButton = new Button("Jeté");

        sellButton.setOnAction(e -> controller.sell(codeProperty.get(), quantityProperty.get()));
        throwButton.setOnAction(e -> controller.dispose(codeProperty.get(), quantityProperty.get()));

        return new HBox(10, productQuantityField, productCodeField, sellButton, throwButton);
    }
}
