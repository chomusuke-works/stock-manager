package ch.stockmanager.client.views;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

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
import ch.stockmanager.client.util.RequestHelper;

/**
 * This pane displays all sales and waste data.
 * It allows to search for a specific product, and enter a new sale or waste.
 */
public class SalesPane extends BorderPane {
    private final ObservableList<Sale> sales = FXCollections.observableArrayList();

	public SalesPane() throws IOException, URISyntaxException {
        this.setPadding(new Insets(15));

        // Top bar
        Label titre = new Label("Ventes & Gestion de Déchets");
        titre.setFont(new Font("Arial", 24));

        Button backButton = new Button("<--");
        backButton.setOnAction(e -> Navigator.goToDashboard());

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setSpacing(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getChildren().addAll(backButton, titre);

        BorderPane.setMargin(topBar, new Insets(0, 0, 20, 0));

        // Search field
        var searchField = new TextField();
        searchField.setPromptText("Rechercher un produit...");
        searchField.setPrefWidth(200);
        searchField.textProperty().addListener(new FilterListener(sales));

        // Main table for sales data
        TableView<Sale> salesTable = new TableView<>();
        salesTable.setPrefHeight(300);
        salesTable.setItems(sales);

        // Columns
        TableColumn<Sale, String> columnDate = new TableColumn<>("Date");
        TableColumn<Sale, Long> columnCode = new TableColumn<>("produit");
        TableColumn<Sale, Integer> columnSold = new TableColumn<>("Vendus");
        TableColumn<Sale, Integer> columnThrown = new TableColumn<>("Jetés");

        columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        columnCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        columnSold.setCellValueFactory(new PropertyValueFactory<>("sold"));
        columnThrown.setCellValueFactory(new PropertyValueFactory<>("thrown"));

        salesTable.getColumns().add(columnDate);
        salesTable.getColumns().add(columnCode);
        salesTable.getColumns().add(columnSold);
        salesTable.getColumns().add(columnThrown);

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantité");
        quantityField.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));

        // Transaction buttons
        ObservableValue<Sale> selectedItem = salesTable.getSelectionModel().selectedItemProperty();
        ObservableValue<String> quantity = quantityField.textProperty();
        Button buttonSale = getTransactionButton(
            "Vendu",
            selectedItem,
            quantity
        );
        Button ButtonWaste = getTransactionButton(
            "Jeté",
            selectedItem,
            quantity
        );

        HBox hBoxForm = new HBox(10);
        hBoxForm.getChildren().addAll(quantityField, buttonSale, ButtonWaste);
        hBoxForm.setPadding(new Insets(10, 0, 0, 0));

        // Main layout
        VBox vboxCenter = new VBox(10);
        vboxCenter.getChildren().addAll(searchField, salesTable);

        this.setTop(topBar);
        this.setCenter(vboxCenter);
        this.setBottom(hBoxForm);

        sales.setAll(fetchSales());
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

    /**
     * Connects to the API to fetch sales data.
     */
    private List<Sale> fetchSales() throws IOException, URISyntaxException {
        HttpURLConnection connection = RequestHelper.createConnexion(
                "http://localhost:25565/api/sales/all",
                "GET");
        RequestHelper.sendRequest(connection, HttpURLConnection.HTTP_OK);
        String answer = RequestHelper.getAnswer(connection);
        connection.disconnect();

        var objectMapper = new ObjectMapper();
        return objectMapper.readValue(answer, objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, Sale.class));
    }

    private class FilterListener implements ChangeListener<String> {

        private final ObservableList<Sale> sales;

        public FilterListener(ObservableList<Sale> sales) {
            this.sales = sales;
        }

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            List<Sale> refreshedSales;
            try {
                refreshedSales = fetchSales();
            } catch (URISyntaxException | IOException e) {
                System.err.println("An error occured while fetching sales.");
                System.err.println(e.getMessage());

                return;
            }
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
