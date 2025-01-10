package views;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.*;


public class VueProduitsExpires extends BorderPane {

    // Table pour afficher les produits expirés
    private TableView<Produit> tableProduitsExpires;

    // Table pour afficher les produits sur le point d'expirer
    private TableView<Produit> tableProduitsBientotExpires;

    public VueProduitsExpires() {
        // Mise en page principale
        this.setPadding(new Insets(15));

        // Titre principal
        Label titre = new Label("Vue des produits expirés");
        titre.setFont(new Font("Arial", 24));

        Button boutonRetour = new Button("Retour Dashboard");
        boutonRetour.setOnAction(e -> Navigator.goToDashboard());

        // Création des tableaux
        tableProduitsExpires = new TableView<>();
        tableProduitsBientotExpires = new TableView<>();

        // Configuration des colonnes pour la table des produits expirés
        TableColumn<Produit, String> colNomExpire = new TableColumn<>("Nom du produit");
        colNomExpire.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<Produit, LocalDate> colDateExpire = new TableColumn<>("Date de péremption");
        colDateExpire.setCellValueFactory(new PropertyValueFactory<>("datePeremption"));

        tableProduitsExpires.getColumns().addAll(colNomExpire, colDateExpire);
        tableProduitsExpires.setPrefHeight(200); // Hauteur préférée pour l'affichage

        // Configuration des colonnes pour la table des produits bientôt expirés
        TableColumn<Produit, String> colNomBientotExpire = new TableColumn<>("Nom du produit");
        colNomBientotExpire.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<Produit, LocalDate> colDateBientotExpire = new TableColumn<>("Date de péremption");
        colDateBientotExpire.setCellValueFactory(new PropertyValueFactory<>("datePeremption"));

        tableProduitsBientotExpires.getColumns().addAll(colNomBientotExpire, colDateBientotExpire);
        tableProduitsBientotExpires.setPrefHeight(200);

        // Création des labels
        Label labelExpires = new Label("Produits expirés :");
        labelExpires.setFont(new Font("Arial", 16));

        Label labelBientotExpires = new Label("Produits sur le point d'expirer :");
        labelBientotExpires.setFont(new Font("Arial", 16));

        // Disposition verticale : label + table (expirés), puis label + table (bientôt expirés)
        VBox vboxCenter = new VBox(10);
        vboxCenter.getChildren().addAll(
                boutonRetour,
                labelExpires,
                tableProduitsExpires,
                labelBientotExpires,
                tableProduitsBientotExpires
        );

        // On place le titre en haut et le contenu au centre
        this.setTop(titre);
        this.setCenter(vboxCenter);

        // Appel à la méthode pour simuler la population des données
        // Plus tard, vous remplacerez cette méthode par des appels HTTP réels à votre API.
        chargerDonnees();
    }

    /**
     * Méthode privée qui récupère la liste des produits expirés et des produits sur le point d'expirer.
     * Pour l'instant, elle simule les données en dur.
     * Vous pourrez remplacer l'intérieur de cette méthode par vos appels HTTP réels.
     */
    private void chargerDonnees() {
        // -------------------------------
        // ICI, vous ferez l'appel HTTP pour récupérer la liste des produits expirés
        // Exemple : List<Produit> produitsExpires = api.getProduitsExpires();
        // -------------------------------
        List<Produit> produitsExpires = getFakeExpiredProducts();

        // -------------------------------
        // ICI, vous ferez l'appel HTTP pour récupérer la liste des produits sur le point d'expirer
        // Exemple : List<Produit> produitsBientotExpires = api.getProduitsBientotExpires();
        // -------------------------------
        List<Produit> produitsBientotExpires = getFakeAboutToExpireProducts();

        tableProduitsExpires.getItems().setAll(produitsExpires);
        tableProduitsBientotExpires.getItems().setAll(produitsBientotExpires);
    }

    /**
     * Méthode simulant des données de produits expirés.
     * À remplacer par un appel HTTP vers votre API.
     */
    private List<Produit> getFakeExpiredProducts() {
        List<Produit> produits = new ArrayList<>();
        produits.add(new Produit("Yaourt nature", LocalDate.now().minusDays(1)));
        produits.add(new Produit("Fromage frais", LocalDate.now().minusDays(3)));
        produits.add(new Produit("Lait demi-écrémé", LocalDate.now().minusDays(2)));
        return produits;
    }

    /**
     * Méthode simulant des données de produits sur le point d'expirer.
     * À remplacer par un appel HTTP vers votre API.
     */
    private List<Produit> getFakeAboutToExpireProducts() {
        List<Produit> produits = new ArrayList<>();
        produits.add(new Produit("Jus d'orange", LocalDate.now().plusDays(1)));
        produits.add(new Produit("Salade verte", LocalDate.now().plusDays(2)));
        return produits;
    }

    /**
     * Classe interne représentative d'un produit.
     * Vous pouvez la déplacer dans un fichier séparé au besoin.
     */
    public static class Produit {
        private String nom;
        private LocalDate datePeremption;

        public Produit(String nom, LocalDate datePeremption) {
            this.nom = nom;
            this.datePeremption = datePeremption;
        }

        public String getNom() {
            return nom;
        }

        public LocalDate getDatePeremption() {
            return datePeremption;
        }
    }
}
