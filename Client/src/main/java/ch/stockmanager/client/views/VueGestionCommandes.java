package ch.stockmanager.client.views;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Vue permettant de gérer les commandes de réapprovisionnement.
 * - Liste des produits à commander (quantité, coût unitaire, coût total)
 * - Possibilité d'activer/désactiver l'automatisation des commandes
 * - Formulaire pour commander manuellement un produit donné.
 */
public class VueGestionCommandes extends BorderPane {

    // CheckBox pour l'automatisation
    private CheckBox checkBoxAutomatisation;

    // Table de produits à commander
    private TableView<CommandeProduit> tableCommandes;

    // Formulaire d'ajout manuel d'une commande
    private TextField champNomProduit;
    private TextField champQuantite;
    private TextField champPrixUnitaire;

	public VueGestionCommandes() throws IOException {
        // Mise en forme générale
        this.setPadding(new Insets(15));

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setSpacing(10);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // Titre
        Label titre = new Label("Gestion des commandes");
        titre.setFont(new Font("Arial", 24));

        Button boutonRetour = new Button("Retour Dashboard");
        boutonRetour.setOnAction(e -> Navigator.goToDashboard());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(titre, spacer, boutonRetour);
        this.setTop(topBar);

        BorderPane.setMargin(titre, new Insets(0, 0, 10, 0));

        // Section haute : automatisation
        checkBoxAutomatisation = new CheckBox("Activer l'automatisation des commandes");
        // ICI, vous pourrez faire un appel HTTP pour stocker la valeur (activée/désactivée)
        checkBoxAutomatisation.setOnAction(e -> {
            boolean active = checkBoxAutomatisation.isSelected();
            // ex: api.setAutomatisationCommandes(active);
            System.out.println("Automatisation des commandes : " + (active ? "Activée" : "Désactivée"));
        });

        // Partie centrale : tableau des produits à commander
        tableCommandes = new TableView<>();
        TableColumn<CommandeProduit, String> colProduit = new TableColumn<>("Produit");
        colProduit.setCellValueFactory(c -> c.getValue().nomProduitProperty());

        TableColumn<CommandeProduit, Number> colQuantite = new TableColumn<>("Quantité");
        colQuantite.setCellValueFactory(c -> c.getValue().quantiteProperty());

        TableColumn<CommandeProduit, BigDecimal> colPrixUnitaire = new TableColumn<>("Prix Unitaire");
        colPrixUnitaire.setCellValueFactory(c -> c.getValue().prixUnitaireProperty());

        TableColumn<CommandeProduit, BigDecimal> colPrixTotal = new TableColumn<>("Coût Total");
        colPrixTotal.setCellValueFactory(c -> c.getValue().coutTotalProperty());

        tableCommandes.getColumns().addAll(colProduit, colQuantite, colPrixUnitaire, colPrixTotal);
        tableCommandes.setPrefHeight(300);

        // Section basse : formulaire pour commander manuellement
        champNomProduit = new TextField();
        champNomProduit.setPromptText("Nom du produit");

        champQuantite = new TextField();
        champQuantite.setPromptText("Quantité");

        champPrixUnitaire = new TextField();
        champPrixUnitaire.setPromptText("Prix unitaire");

		Button boutonAjouterCommande = new Button("Ajouter commande");
        boutonAjouterCommande.setOnAction(e -> {
            // ICI, appel HTTP pour ajouter une commande
            // ex: api.ajouterCommande(nomProduit, qte, prixUnit);
            String nom = champNomProduit.getText().trim();
            String qte = champQuantite.getText().trim();
            String prixUnit = champPrixUnitaire.getText().trim();

            if (!nom.isEmpty() && !qte.isEmpty() && !prixUnit.isEmpty()) {
                try {
                    int quantiteInt = Integer.parseInt(qte);
                    BigDecimal pu = new BigDecimal(prixUnit);
                    CommandeProduit cp = new CommandeProduit(nom, quantiteInt, pu);
                    tableCommandes.getItems().add(cp);
                    champNomProduit.clear();
                    champQuantite.clear();
                    champPrixUnitaire.clear();
                } catch (NumberFormatException nfe) {
                    // Mauvais format pour la quantité
                    System.err.println("Quantité ou prix invalide.");
                }
            }
        });

        HBox hBoxForm = new HBox(10, champNomProduit, champQuantite, champPrixUnitaire, boutonAjouterCommande);
        hBoxForm.setPadding(new Insets(10, 0, 0, 0));

        // Mise en page verticale
        VBox vboxCenter = new VBox(10, checkBoxAutomatisation, tableCommandes, hBoxForm);

        this.setCenter(vboxCenter);

        // Charger la liste initiale (simulée)
        chargerCommandesParDefaut();
    }

    /**
     * Simulation de chargement initial des produits à commander.
     * À remplacer par un appel HTTP pour récupérer la liste des commandes en cours.
     */
    private void chargerCommandesParDefaut() throws IOException {
        //Exemple de connection à la bd pour récupérer les produits bientôt expirés
        URL urlCommande = new URL("http://localhost:25565/api/???");
        HttpURLConnection conCommande = (HttpURLConnection) urlCommande.openConnection();
        //GET : Pour la lecture
        //POST: Création d'un nouvel élément ou paramètres complexes
        //PUT: Mise à jour d'une donnée
        //Le mieux pour simplement afficher les commandes est GET (juste pour la lecture)
        conCommande.setRequestMethod("GET");
        //https://www.baeldung.com/java-http-request



        List<CommandeProduit> commandes = new ArrayList<>();
        commandes.add(new CommandeProduit("Lait entier", 50, new BigDecimal("0.85")));
        commandes.add(new CommandeProduit("Pâte à pizza", 30, new BigDecimal("1.20")));

        tableCommandes.getItems().addAll(commandes);
    }

    /**
     * Classe interne représentant un produit à commander (quantité, prix unitaire...).
     */
    public static class CommandeProduit {
        private final SimpleStringProperty nomProduit;
        private final SimpleIntegerProperty quantite;
        private final SimpleObjectProperty<BigDecimal> prixUnitaire;
        private final SimpleObjectProperty<BigDecimal> coutTotal;

        public CommandeProduit(String nomProduit, int quantite, BigDecimal prixUnitaire) {
            this.nomProduit = new SimpleStringProperty(nomProduit);
            this.quantite = new SimpleIntegerProperty(quantite);
            this.prixUnitaire = new SimpleObjectProperty<>(prixUnitaire);
            this.coutTotal = new SimpleObjectProperty<>(prixUnitaire.multiply(BigDecimal.valueOf(quantite)));
        }

        public String getNomProduit() {
            return nomProduit.get();
        }
        public SimpleStringProperty nomProduitProperty() {
            return nomProduit;
        }

        public int getQuantite() {
            return quantite.get();
        }
        public SimpleIntegerProperty quantiteProperty() {
            return quantite;
        }

        public BigDecimal getPrixUnitaire() {
            return prixUnitaire.get();
        }
        public SimpleObjectProperty<BigDecimal> prixUnitaireProperty() {
            return prixUnitaire;
        }

        public BigDecimal getCoutTotal() {
            return coutTotal.get();
        }
        public SimpleObjectProperty<BigDecimal> coutTotalProperty() {
            return coutTotal;
        }
    }
}

