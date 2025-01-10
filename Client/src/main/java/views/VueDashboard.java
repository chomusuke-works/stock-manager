package views;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.io.IOException;

/**
 * Vue principale (tableau de bord) permettant :
 * - Un aperçu rapide des infos importantes (produits expirés, commandes en attente, etc.)
 * - La navigation vers les autres vues (via des boutons, liens, etc.)
 */
public class VueDashboard extends BorderPane {

    public VueDashboard() {
        // Configuration du padding
        this.setPadding(new Insets(15));

        // Titre
        Label titre = new Label("Tableau de bord");
        titre.setFont(new Font("Arial", 24));
        this.setTop(titre);
        BorderPane.setMargin(titre, new Insets(0, 0, 10, 0));

        // -----------------------------
        // Zone centrale : Aperçu global
        // -----------------------------
        // Pour l’exemple, on va juste présenter quelques stats fictives
        Label labelProduitsExpires = new Label("Produits bientôt expirés : 5");
        Label labelCommandesEnAttente = new Label("Commandes en attente : 2");
        Label labelVentesAujourdHui = new Label("Ventes aujourd'hui : 37");
        Label labelDechetsAujourdHui = new Label("Déchets aujourd'hui : 4");

        VBox vboxCenter = new VBox(10,
                labelProduitsExpires,
                labelCommandesEnAttente,
                labelVentesAujourdHui,
                labelDechetsAujourdHui
        );
        this.setCenter(vboxCenter);

        // -----------------------------
        // Barre de navigation / raccourcis
        // -----------------------------
        // Idée : un HBox ou VBox contenant des boutons qui ouvrent les autres vues
        Button boutonProduitsExpires = new Button("Produits expirés");
        boutonProduitsExpires.setOnAction(e -> {
            try {
                Navigator.goToProduitsExpires();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        Button boutonVentesDechets = new Button("Ventes & Déchets");
        boutonVentesDechets.setOnAction(e -> Navigator.goToVentesDechets());

        Button boutonLocalisationRayons = new Button("Localisation & Rayons");
        boutonLocalisationRayons.setOnAction(e -> Navigator.goToLocalisationRayons());

        Button boutonCommandes = new Button("Commandes");
        boutonCommandes.setOnAction(e -> Navigator.goToGestionCommandes());

        Button boutonFournisseurs = new Button("Fournisseurs");
        boutonFournisseurs.setOnAction(e -> Navigator.goToFournisseurs());

        // Dans un vrai projet, chaque bouton pourrait appeler un contrôleur ou une méthode
        // pour changer la scène et afficher la vue correspondante. Exemple rapide :
        // boutonProduitsExpires.setOnAction(e -> {
        //     // switchToVueProduitsExpires();
        // });

        VBox vboxNavigation = new VBox(10,
                boutonProduitsExpires,
                boutonVentesDechets,
                boutonLocalisationRayons,
                boutonCommandes,
                boutonFournisseurs
        );
        vboxNavigation.setPadding(new Insets(10));

        this.setRight(vboxNavigation);

        // -----------------------------
        // Chargement initial (ex. data)
        // -----------------------------
        // ICI, on pourrait placer les appels HTTP pour récupérer
        // les stats globales (produits expirés, commandes, etc.)
        chargerStatsGlobales();
    }

    /**
     * Méthode pour récupérer les stats globales (produits expirés, nb de commandes, etc.)
     * Dans cet exemple, on est en dur, mais on pourrait faire un GET sur un endpoint du style /dashboard
     */
    private void chargerStatsGlobales() {
        // Ex : int nbProduitsExpires = api.getNbProduitsExpiresBientot();
        //      int nbCommandesEnAttente = api.getNbCommandesEnAttente();
        // etc.
        //
        // Pour l’instant, on a mis des labels en dur dans le constructeur pour l’exemple.
    }
}

