package ch.stockmanager.client.views;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.converter.IntegerStringConverter;

import ch.stockmanager.types.Order;
import ch.stockmanager.client.util.JavaFxHelper;
import ch.stockmanager.client.controllers.OrdersController;

/**
 * This pane presents all products that need to be ordered, which are
 * all products that have a quantity lower than the order threshold assigned to them
 * for the current time of the year.
 */
public class OrdersPane extends BorderPane {

    private final OrdersController controller;

	public OrdersPane(OrdersController controller) {
        this.controller = controller;

        this.setPadding(new Insets(15));

        Label title = new Label("Gestion des commandes");
        title.setFont(new Font("Arial", 24));
        BorderPane.setMargin(title, new Insets(0, 0, 20, 0));

        TableView<Order> ordersTable = JavaFxHelper.getTable(
            new String[]{"Produit", "Quantité"},
            new String[]{"name", "quantity"}
        );
        ordersTable.setItems(controller.getOrders());

        // Section basse : formulaire pour commander manuellement
        HBox formBox = getOrderForm();

        this.setTop(title);
        this.setCenter(ordersTable);
        this.setBottom(formBox);
    }

    private HBox getOrderForm() {
        TextField nameField = new TextField();
        nameField.setPromptText("Nom du produit");

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantité");
        quantityField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));

        Button orderButton = getOrderButton(nameField, quantityField);

        return new HBox(10, nameField, quantityField, orderButton);
    }

    private Button getOrderButton(TextField nameField, TextField quantityField) {
        Button button = new Button("Commander");
        button.setOnAction(e -> {
            Order newOrder = new Order(
                nameField.getText().trim(),
                Integer.parseInt(quantityField.getText())
            );

            if (newOrder.name.isEmpty() || newOrder.quantity == 0) return;

            controller.addOrder(newOrder);

            nameField.clear();
            quantityField.clear();
        });

        return button;
    }
}

