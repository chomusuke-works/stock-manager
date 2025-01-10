package views;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Vue permettant d'enregistrer les ventes du jour et de signaler des produits comme déchets.
 * Elle inclut un champ de recherche de produits, un tableau pour lister les produits filtrés
 * et un formulaire pour saisir la quantité vendue ou jetée.
 */
public class VueVentesDechets extends BorderPane {

    // Champ de recherche
    private TextField champRecherche;
    // Table pour afficher la liste des produits
    private TableView<Produit> tableProduits;
    // Zone de saisie de quantité
    private TextField champQuantite;
    // Boutons d'action
    private Button boutonVente;
    private Button boutonDechet;

    public VueVentesDechets() {
        // Padding autour de la vue
        this.setPadding(new Insets(15));

        // Titre
        Label titre = new Label("Ventes & Gestion de Déchets");
        titre.setFont(new Font("Arial", 24));

        Button boutonRetour = new Button("Retour Dashboard");
        boutonRetour.setOnAction(e -> Navigator.goToDashboard());

        this.setTop(titre);
        BorderPane.setMargin(titre, new Insets(0, 0, 20, 0));

        // Création du champ de recherche
        champRecherche = new TextField();
        champRecherche.setPromptText("Rechercher un produit...");
        champRecherche.setPrefWidth(200);
        champRecherche.textProperty().addListener((observable, oldValue, newValue) -> {
            // Ici, vous pourrez lancer la requête HTTP pour rechercher les produits correspondants
            // ou filtrer localement si vous avez déjà chargé les produits en mémoire.
            filtrerProduits(newValue);
        });

        // Table des produits
        tableProduits = new TableView<>();
        TableColumn<Produit, String> colNom = new TableColumn<>("Nom du produit");
        colNom.setCellValueFactory(param -> param.getValue().nomProperty());

        TableColumn<Produit, Integer> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(param -> param.getValue().stockProperty().asObject());

        TableColumn<Produit, LocalDate> colPeremption = new TableColumn<>("Date de péremption");
        colPeremption.setCellValueFactory(param -> param.getValue().datePeremptionProperty());

        tableProduits.getColumns().addAll(colNom, colStock, colPeremption);
        tableProduits.setPrefHeight(300);

        // Partie formulaire : saisir quantité, puis boutons Vente ou Déchet
        champQuantite = new TextField();
        champQuantite.setPromptText("Quantité");

        boutonVente = new Button("Enregistrer la vente");
        boutonVente.setOnAction(event -> {
            // Ici, vous ferez l'appel HTTP pour soustraire la quantité vendue du stock
            Produit produitSelectionne = tableProduits.getSelectionModel().getSelectedItem();
            if (produitSelectionne != null) {
                try {
                    int quantiteV = Integer.parseInt(champQuantite.getText());
                    // Appel HTTP pour valider la vente
                    // ex: api.vendreProduit(produitSelectionne.getId(), quantiteV);
                    System.out.println("Vente de " + quantiteV + " unités de " + produitSelectionne.getNom());
                } catch (NumberFormatException e) {
                    System.err.println("Quantité invalide.");
                }
            }
        });

        boutonDechet = new Button("Signaler en déchet");
        boutonDechet.setOnAction(event -> {
            // Ici, vous ferez l'appel HTTP pour signaler la perte de cette quantité en tant que déchet
            Produit produitSelectionne = tableProduits.getSelectionModel().getSelectedItem();
            if (produitSelectionne != null) {
                try {
                    int quantiteD = Integer.parseInt(champQuantite.getText());
                    // Appel HTTP pour signaler que cette quantité est jetée
                    // ex: api.jeterProduit(produitSelectionne.getId(), quantiteD);
                    System.out.println("Jeter " + quantiteD + " unités de " + produitSelectionne.getNom());
                } catch (NumberFormatException e) {
                    System.err.println("Quantité invalide.");
                }
            }
        });

        HBox hBoxForm = new HBox(10);
        hBoxForm.getChildren().addAll(new Label("Quantité :"), champQuantite, boutonVente, boutonDechet);
        hBoxForm.setPadding(new Insets(10, 0, 0, 0));

        // Disposition verticale : Champ recherche, Table, Formulaire
        VBox vboxCenter = new VBox(10);
        vboxCenter.getChildren().addAll(boutonRetour, champRecherche, tableProduits, hBoxForm);

        this.setCenter(vboxCenter);

        // Chargement initial des produits (simulé ici en dur)
        chargerProduitsParDefaut();
    }

    /**
     * Méthode pour charger une liste initiale de produits (fictive).
     * À remplacer par un appel HTTP pour récupérer les produits depuis l'API.
     */
    private void chargerProduitsParDefaut() {
        List<Produit> produits = new ArrayList<>();
        produits.add(new Produit("Yaourt nature", 50, LocalDate.now().plusDays(3)));
        produits.add(new Produit("Pomme", 100, LocalDate.now().plusDays(7)));
        produits.add(new Produit("Steak haché", 20, LocalDate.now().minusDays(1)));
        produits.add(new Produit("Lait demi-écrémé", 30, LocalDate.now().minusDays(2)));

        // On lie directement la liste au TableView
        tableProduits.getItems().setAll(produits);
    }

    /**
     * Méthode pour filtrer les produits affichés dans le tableau,
     * selon la chaîne de recherche passée en paramètre.
     */
    private void filtrerProduits(String searchTerm) {
        // Ici, on pourrait refaire un appel HTTP pour obtenir la liste filtrée.
        // En attendant, on simule simplement un filtrage local sur la liste affichée.
        List<Produit> produitsActuels = new ArrayList<>(tableProduits.getItems());
        List<Produit> resultats = new ArrayList<>();

        for (Produit p : produitsActuels) {
            if (p.getNom().toLowerCase().contains(searchTerm.toLowerCase())) {
                resultats.add(p);
            }
        }

        tableProduits.getItems().setAll(resultats);
    }

    /**
     * Classe interne représentant un produit.
     * On utilise des propriétés JavaFX (StringProperty, IntegerProperty...) pour faciliter le binding avec la TableView.
     */
    public static class Produit {
        private final javafx.beans.property.SimpleStringProperty nom;
        private final javafx.beans.property.SimpleIntegerProperty stock;
        private final javafx.beans.property.ObjectProperty<LocalDate> datePeremption;

        public Produit(String nom, int stock, LocalDate datePeremption) {
            this.nom = new javafx.beans.property.SimpleStringProperty(nom);
            this.stock = new javafx.beans.property.SimpleIntegerProperty(stock);
            this.datePeremption = new javafx.beans.property.SimpleObjectProperty<>(datePeremption);
        }

        public String getNom() {
            return nom.get();
        }
        public javafx.beans.property.StringProperty nomProperty() {
            return nom;
        }

        public int getStock() {
            return stock.get();
        }
        public javafx.beans.property.IntegerProperty stockProperty() {
            return stock;
        }

        public LocalDate getDatePeremption() {
            return datePeremption.get();
        }
        public javafx.beans.property.ObjectProperty<LocalDate> datePeremptionProperty() {
            return datePeremption;
        }
    }
}
