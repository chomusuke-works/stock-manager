package ch.stockmanager.client.views;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.converter.NumberStringConverter;

import ch.stockmanager.types.Order;
import ch.stockmanager.client.Client;
import ch.stockmanager.client.util.JavaFxHelper;
import ch.stockmanager.client.util.HTTPHelper;


/**
 * This pane presents all products that need to be ordered, which are
 * all products that have a quantity lower than the order threshold assigned to them
 * for the current time of the year.
 */
public class OrdersPane extends BorderPane {
	public OrdersPane() {
        this.setPadding(new Insets(15));

        Label title = new Label("Gestion des commandes");
        title.setFont(new Font("Arial", 24));
        BorderPane.setMargin(title, new Insets(0, 0, 20, 0));

        TableView<Order> ordersTable = JavaFxHelper.getTable(
            new String[]{"Produit", "Quantité"},
            new String[]{"name", "quantity"}
        );

        // Section basse : formulaire pour commander manuellement
        HBox formBox = getOrderForm(ordersTable.getItems());

        // Mise en page verticale
        VBox centerBox = new VBox(10, ordersTable);

        this.setTop(title);
        this.setCenter(centerBox);
        this.setBottom(formBox);

        new Thread(() -> ordersTable.getItems().setAll(fetchOrders()))
            .start();
    }

    private List<Order> fetchOrders() {
        return HTTPHelper.getList(String.format("http://%s/api/products/orders", Client.SERVER_IP), Order.class);
    }

    private HBox getOrderForm(ObservableList<Order> orderDestination) {
        TextField nameField = new TextField();
        nameField.setPromptText("Nom du produit");

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantité");
        quantityField.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));

        Button boutonAjouterCommande = new Button("Commander");
        boutonAjouterCommande.setOnAction(e -> {
            Order newOrder = new Order(
                nameField.getText().trim(),
                Integer.parseInt(quantityField.getText())
            );
            nameField.clear();
            quantityField.clear();

            if (newOrder.name.isEmpty() || newOrder.quantity == 0) return;

            // Update the quantity if an order is found
            for (int i = 0; i < orderDestination.size(); ++i) {
                Order o = orderDestination.get(i);
                if (o.getName().equals(newOrder.name)) {
                    newOrder.quantity += o.getQuantity();
                    orderDestination.set(i, newOrder);

                    return;
                }
            }

            // If no matching order is found
            orderDestination.add(newOrder);
        });

        HBox hBoxForm = new HBox(10, nameField, quantityField, boutonAjouterCommande);
        hBoxForm.setPadding(new Insets(10, 0, 0, 0));

        return hBoxForm;
    }
}

