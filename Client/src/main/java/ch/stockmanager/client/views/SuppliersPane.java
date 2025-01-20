package ch.stockmanager.client.views;

import java.math.BigDecimal;
import java.util.List;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import ch.stockmanager.types.Product;
import ch.stockmanager.types.Supplier;

/**
 * Vue purement informative permettant de consulter :
 * - la liste des fournisseurs
 * - les détails du fournisseur sélectionné (contact, délai de livraison)
 * - les produits associés à ce fournisseur
 * - l'historique des commandes passées auprès de ce fournisseur
 */
public class SuppliersPane extends BorderPane {
    public SuppliersPane() {
        this.setPadding(new Insets(15));

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setSpacing(10);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // Titre principal
        Label title = new Label("Vue des fournisseurs");
        title.setFont(new Font("Arial", 24));

        Button backButton = new Button("Retour Dashboard");
        backButton.setOnAction(e -> Navigator.goToDashboard());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(backButton, title);

        //this.setTop(titre);
        BorderPane.setMargin(topBar, new Insets(0, 0, 20, 0));

        ListView<Supplier> suppliersList = new ListView<>();
        suppliersList.setPrefWidth(200);

        // Supplier details
        VBox supplierDetails = getSupplierDetails(suppliersList.getSelectionModel().selectedItemProperty());

        // SplitPane pour séparer la liste des fournisseurs à gauche
        // et les détails du fournisseur sélectionné à droite
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(suppliersList, supplierDetails);
        splitPane.setDividerPositions(0.3); // 30% / 70%

        this.setTop(topBar);
        this.setCenter(splitPane);

        //suppliersList.getItems().setAll(fetchSuppliers());
    }

    /**
     * Charge la liste des fournisseurs (en dur ici).
     * À remplacer par un appel HTTP pour récupérer la liste depuis l'API.
     */
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

        ObservableList<Product> products;

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

