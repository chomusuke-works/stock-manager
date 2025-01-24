package ch.stockmanager.client.views;

import java.util.List;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import ch.stockmanager.client.Client;
import ch.stockmanager.client.util.HTTPHelper;
import ch.stockmanager.client.util.JavaFxHelper;
import ch.stockmanager.types.Product;
import ch.stockmanager.types.Supplier;

/**
 * This pane displays information about suppliers and which products they provide.
 */
public class SuppliersPane extends BorderPane {

    private final static String PATH_PREFIX = String.format("http://%s/api/supplier", Client.SERVER_IP);
    ListView<Supplier> suppliersList = new ListView<>();

    private List<Supplier> fetchSuppliers() {
        return HTTPHelper.getList(String.format("%s/all", PATH_PREFIX), Supplier.class);
    }

    private void addSupplier(Supplier supplier) {
        HTTPHelper.post(PATH_PREFIX, supplier);
    }

    private void modifySupplier(Supplier supplier) {
        HTTPHelper.put(String.format("%s/%d", PATH_PREFIX, supplier.getId()), supplier);
    }

    private void removeSupplier(int id) {
        HTTPHelper.delete(String.format("%s/%d", PATH_PREFIX, id));
    }

    private static List<Product> fetchSupplierProducts(int id) {
        return HTTPHelper.getList(String.format("%s/%d/products", PATH_PREFIX, id), Product.class);
    }

    public SuppliersPane() {
        this.setPadding(new Insets(15));

        // Components (Structure)
        // - Title
        Label title = new Label("Fournisseurs");
        title.setFont(new Font("Arial", 24));
        BorderPane.setMargin(title, new Insets(0, 0, 20, 0));

        suppliersList.setPrefWidth(200);
        VBox.setVgrow(suppliersList, Priority.ALWAYS);
        Button addSupplierButton = new Button("+");
        Button removeSupplierButton = new Button("-");

        VBox suppliersListBox = new VBox();
        suppliersListBox.getChildren().setAll(new HBox(10, addSupplierButton, removeSupplierButton), suppliersList);

        // - Details of the supplier
        VBox supplierDetailsBox = new VBox(10);
        supplierDetailsBox.setPadding(new Insets(10));

        Label supplierNameLabel = new Label("Nom : ");
        Label supplierContactLabel = new Label("Contact : ");
        Label supplierOrderFrequencyLabel = new Label("Délai de livraison : ");
        Button editButton = new Button("Edit");

        VBox detailsBox = new VBox(10, supplierNameLabel, supplierContactLabel, supplierOrderFrequencyLabel, editButton);

        // - Table of the supplier's products
        TableView<Product> productsTable = JavaFxHelper.getTable(
            new String[]{"Produit", "Prix"},
            new String[]{"code", "price"}
        );
        productsTable.setPrefHeight(150);

        supplierDetailsBox.getChildren().setAll(
                detailsBox,
                new Label("Produits du fournisseur :"),
                productsTable
        );

        // - UI division
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().setAll(suppliersListBox, supplierDetailsBox);
        splitPane.setDividerPositions(0.3); // 30% / 70%

        // Logic
        // - Display the details of the supplier selected
        suppliersList.getSelectionModel().selectedItemProperty().addListener(new supplierDetailsUpdater(
                supplierNameLabel.textProperty(),
                supplierContactLabel.textProperty(),
                supplierOrderFrequencyLabel.textProperty(),
                productsTable.getItems())
        );
        // - Modify the ListView Cells so they display the name of the supplier
        suppliersList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Supplier supplier, boolean empty) {
                super.updateItem(supplier, empty);
                if (empty || supplier == null) {
                    setText(null);
                } else {
                    setText(supplier.getName());
                }
            }
        });
        // - Edit supplier button action
        editButton.setOnAction(event -> showEditOrCreateFields(false, supplierNameLabel, supplierContactLabel, supplierOrderFrequencyLabel, suppliersList.getSelectionModel().getSelectedItem()));
        // - Create supplier button action
        addSupplierButton.setOnAction(event -> {
            Supplier s = new Supplier(0, "", "", 0);
            suppliersList.getItems().add(s);
            suppliersList.getSelectionModel().select(s);
            showEditOrCreateFields(true, supplierNameLabel, supplierContactLabel, supplierOrderFrequencyLabel, suppliersList.getSelectionModel().getSelectedItem());
        });

        ReadOnlyObjectProperty<Supplier> selectedSupplier = suppliersList.getSelectionModel().selectedItemProperty();
        removeSupplierButton.disableProperty().bind(selectedSupplier.isNull());  // Can't delete a supplier if no selection
        removeSupplierButton.setOnAction(event -> {
            removeSupplier(suppliersList.getSelectionModel().getSelectedItem().getId());
            suppliersList.getItems().setAll(fetchSuppliers());
        });

        // Pane creation
        this.setTop(title);
        this.setCenter(splitPane);

        // Initialisation
        new Thread(() -> suppliersList.getItems().setAll(fetchSuppliers())).start();
    }

    private void showEditOrCreateFields(boolean isNewSupplier, Label nameLabel, Label contactLabel, Label orderFrequencyLabel, Supplier supplier) {
        if (supplier == null) return;

        // Create the dialog
        Dialog<Supplier> dialog = new Dialog<>();
        dialog.setTitle("Edit Supplier");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the fields
        TextField nameField = new TextField(supplier.getName());
        TextField contactField = new TextField(supplier.getEmail());
        TextField orderFrequencyField = new TextField(String.valueOf(supplier.getOrderFrequency()));

        // Create the layout
        VBox editBox = new VBox(10, new Label("Name:"), nameField, new Label("Contact:"), contactField, new Label("Order Frequency:"), orderFrequencyField);
        dialog.getDialogPane().setContent(editBox);

        // Convert the result to a supplier when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String newName = nameField.getText();
                String newContact = contactField.getText();
                int newOrderFrequency;
                try {
                    newOrderFrequency = Integer.parseInt(orderFrequencyField.getText());
                } catch (NumberFormatException e) {
                    JavaFxHelper.showAlert("Mauvais format", "La fréquence de livraison doit être un nombre entier.");
                    return null;
                }
                return new Supplier(supplier.getId(), newName, newContact, newOrderFrequency);
            }
            return null;
        });

        // Show the dialog and wait for the result
        dialog.showAndWait().ifPresent(newSupplier -> {
            if (isNewSupplier) {
                addSupplier(newSupplier);
            } else {
                modifySupplier(newSupplier);
            }
            // Instantly changes the values
            nameLabel.setText("Nom : " + newSupplier.getName());
            contactLabel.setText("Contact : " + newSupplier.getEmail());
            orderFrequencyLabel.setText("Délai de livraison : " + newSupplier.getOrderFrequency() + " jours");
            // Then loads from server to assure they're true
            suppliersList.getItems().setAll(fetchSuppliers());
            suppliersList.getItems().forEach(item -> {
                if (item.name.equals(newSupplier.getName()))
                    suppliersList.getSelectionModel().select(item);}
            );
        });
    }

    private record supplierDetailsUpdater(StringProperty supplierNameLabel, StringProperty supplierContactLabel,
                                          StringProperty supplierOrderFrequencyLabel,
                                          ObservableList<Product> products) implements ChangeListener<Supplier> {

        @Override
            public void changed(ObservableValue<? extends Supplier> observable, Supplier oldValue, Supplier newValue) {
                if (newValue == null) return;

                supplierNameLabel.setValue("Nom : " + newValue.getName());
                supplierContactLabel.setValue("Contact : " + newValue.getEmail());
                supplierOrderFrequencyLabel.setValue("Délai de livraison : " + newValue.getOrderFrequency() + " jours");

                products.setAll(fetchSupplierProducts(newValue.getId()));
            }
        }
}