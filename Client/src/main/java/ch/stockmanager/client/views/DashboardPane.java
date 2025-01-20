package ch.stockmanager.client.views;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

/**
 * Vue principale (tableau de bord) permettant :
 * - Un aperçu rapide des infos importantes (produits expirés, commandes en attente, etc.)
 * - La navigation vers les autres vues (via des boutons, liens, etc.)
 */
public class DashboardPane extends BorderPane {

    public DashboardPane() {
        this.setPadding(new Insets(15));

        // Title
        Label titre = new Label("Tableau de bord");
        titre.setFont(new Font("Arial", 24));
        BorderPane.setMargin(titre, new Insets(0, 0, 20, 0));

        VBox navigationButtons = getButtons();

        this.setTop(titre);
        this.setLeft(navigationButtons);
    }

    private Button getButton(String label, EventHandler<ActionEvent> handler) {
        Button button = new Button(label);
        button.setOnAction(handler);

        return button;
    }

    private VBox getButtons() {
        Button expiredProductsButton = getButton("Produits expirés", e -> Navigator.goToProduitsExpires());
        Button salesWasteButton = getButton("Ventes & Déchets", e -> Navigator.goToVentesDechets());
        Button shelvesButton = getButton("Localisation & Rayons", e -> Navigator.goToLocalisationRayons());
        Button ordersButton = getButton("Commandes", e -> Navigator.goToGestionCommandes());
        Button suppliersButton = getButton("Fournisseurs", e -> Navigator.goToFournisseurs());

        VBox box = new VBox(10,
            expiredProductsButton,
            salesWasteButton,
            shelvesButton,
            ordersButton,
            suppliersButton
        );
        box.setPadding(new Insets(10));

        return box;
    }
}

