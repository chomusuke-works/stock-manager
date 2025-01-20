package ch.stockmanager.client.views;

import java.math.BigDecimal;
import java.util.List;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import ch.stockmanager.types.Product;
import ch.stockmanager.types.Supplier;

/**
 * This pane displays information about suppliers and which products they provide.
 */
public class SuppliersPane extends BorderPane {
    public SuppliersPane() {
        this.setPadding(new Insets(15));

        Label title = new Label("Vue des fournisseurs");
        title.setFont(new Font("Arial", 24));
        BorderPane.setMargin(title, new Insets(0, 0, 20, 0));

        ListView<Supplier> suppliersList = new ListView<>();
        suppliersList.setPrefWidth(200);

        VBox supplierDetails = getSupplierDetails(suppliersList.getSelectionModel().selectedItemProperty());

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(suppliersList, supplierDetails);
        splitPane.setDividerPositions(0.3); // 30% / 70%

        this.setTop(title);
        this.setCenter(splitPane);

        new Thread(() -> suppliersList.getItems().setAll(fetchSuppliers()))
            .start();
    }

    private List<Supplier> fetchSuppliers() {
        throw new RuntimeException("This function is not implemented yet");
    }

    @SuppressWarnings("unused")
    private List<Product> fetchProducts(Supplier supplier) {
        throw new RuntimeException("This function is not implemented yet");
    }

    private TableView<Product> getProductsTable() {
        TableView<Product> table = new TableView<>();
        table.setPrefHeight(150);

        TableColumn<Product, String> colProduit = new TableColumn<>("Produit");
        colProduit.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, BigDecimal> colPrix = new TableColumn<>("Prix");
        colPrix.setCellValueFactory(new PropertyValueFactory<>("price"));

        table.getColumns().add(colProduit);
        table.getColumns().add(colPrix);

        return table;
    }

    private VBox getSupplierDetails(ObservableValue<Supplier> selectedSupplier) {
        // Labels pour afficher le nom, le contact et le délai de livraison
        Label supplierNameLabel = new Label("Nom : ");
        Label supplierContactLabel = new Label("Contact : ");
        Label supplierOrderFrequencyLabel = new Label("Délai de livraison : ");

        TableView<Product> productsTable = getProductsTable();

        selectedSupplier.addListener(new supplierDetailsUpdater(
            supplierNameLabel.textProperty(),
            supplierContactLabel.textProperty(),
            supplierOrderFrequencyLabel.textProperty(),
            productsTable.getItems())
        );

        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        box.getChildren().addAll(
            supplierNameLabel,
            supplierContactLabel,
            supplierOrderFrequencyLabel,
            new Label("Produits du fournisseur :"),
            productsTable
        );

        return box;
    }

    private class supplierDetailsUpdater implements ChangeListener<Supplier> {
        private final StringProperty supplierNameLabel,
            supplierContactLabel,
            supplierOrderFrequencyLabel;

        private final ObservableList<Product> products;

        public supplierDetailsUpdater(
            StringProperty supplierNameLabel,
            StringProperty supplierContactLabel,
            StringProperty supplierOrderFrequencyLabel,
            ObservableList<Product> products

        ) {
            this.supplierNameLabel = supplierNameLabel;
            this.supplierContactLabel = supplierContactLabel;
            this.supplierOrderFrequencyLabel = supplierOrderFrequencyLabel;

            this.products = products;
        }

        @Override
        public void changed(ObservableValue<? extends Supplier> observable, Supplier oldValue, Supplier newValue) {
            if (newValue == null) return;

            supplierNameLabel.setValue("Nom : " + newValue.getName());
            supplierContactLabel.setValue("Contact : " + newValue.getEmail());
            supplierOrderFrequencyLabel.setValue("Délai de livraison : " + newValue.getOrderFrequency() + " jours");

            products.setAll(fetchProducts(newValue));
        }
    }
}

