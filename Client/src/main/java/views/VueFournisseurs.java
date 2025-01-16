package views;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Vue purement informative permettant de consulter :
 * - la liste des fournisseurs
 * - les détails du fournisseur sélectionné (contact, délai de livraison)
 * - les produits associés à ce fournisseur
 * - l'historique des commandes passées auprès de ce fournisseur
 */
public class VueFournisseurs extends BorderPane {

    // Liste de fournisseurs (ListView)
    private ListView<Fournisseur> listViewFournisseurs;

    // Zones d'affichage d'informations sur le fournisseur sélectionné
    private Label labelNomFournisseur;
    private Label labelContact;
    private Label labelDelaiLivraison;

    // Tables pour les produits et l'historique de commandes
    private TableView<ProduitFournisseur> tableProduits;
    private TableView<Commande> tableHistorique;

    public VueFournisseurs() throws IOException {
        this.setPadding(new Insets(15));

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setSpacing(10);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // Titre principal
        Label titre = new Label("Vue des fournisseurs");
        titre.setFont(new Font("Arial", 24));

        Button boutonRetour = new Button("Retour Dashboard");
        boutonRetour.setOnAction(e -> Navigator.goToDashboard());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(titre, spacer, boutonRetour);
        this.setTop(topBar);

        //this.setTop(titre);
        BorderPane.setMargin(titre, new Insets(0, 0, 10, 0));

        // ---------------------------------------------------------
        // PANE GAUCHE : Liste des fournisseurs
        // ---------------------------------------------------------
        listViewFournisseurs = new ListView<>();
        listViewFournisseurs.setPrefWidth(200);

        // Listener pour mettre à jour la zone de détails quand un fournisseur est sélectionné
        listViewFournisseurs.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                afficherDetailsFournisseur(newVal);
            }
        });

        // ---------------------------------------------------------
        // PANE DROITE : Détails du fournisseur
        // ---------------------------------------------------------
        VBox vboxDetails = new VBox(10);
        vboxDetails.setPadding(new Insets(10));

        // Labels pour afficher le nom, le contact et le délai de livraison
        labelNomFournisseur = new Label("Nom : ");
        labelContact = new Label("Contact : ");
        labelDelaiLivraison = new Label("Délai de livraison : ");

        // Table des produits proposés par le fournisseur sélectionné
        tableProduits = new TableView<>();
        TableColumn<ProduitFournisseur, String> colProduit = new TableColumn<>("Produit");
        colProduit.setCellValueFactory(c -> c.getValue().nomProperty());

        TableColumn<ProduitFournisseur, String> colCategorie = new TableColumn<>("Catégorie");
        colCategorie.setCellValueFactory(c -> c.getValue().categorieProperty());

        TableColumn<ProduitFournisseur, BigDecimal> colPrix = new TableColumn<>("Prix");
        colPrix.setCellValueFactory(c -> c.getValue().prixProperty());

        tableProduits.getColumns().addAll(colProduit, colCategorie, colPrix);
        tableProduits.setPrefHeight(150);

        // Table de l'historique des commandes
        tableHistorique = new TableView<>();
        TableColumn<Commande, LocalDate> colDateCommande = new TableColumn<>("Date");
        colDateCommande.setCellValueFactory(c -> c.getValue().dateProperty());

        TableColumn<Commande, String> colProduitCommande = new TableColumn<>("Produit");
        colProduitCommande.setCellValueFactory(c -> c.getValue().produitProperty());

        TableColumn<Commande, Number> colQuantite = new TableColumn<>("Quantité");
        colQuantite.setCellValueFactory(c -> c.getValue().quantiteProperty());

        TableColumn<Commande, BigDecimal> colMontant = new TableColumn<>("Montant");
        colMontant.setCellValueFactory(c -> c.getValue().montantProperty());

        tableHistorique.getColumns().addAll(colDateCommande, colProduitCommande, colQuantite, colMontant);
        tableHistorique.setPrefHeight(150);

        // On ajoute tout dans le vbox
        vboxDetails.getChildren().addAll(
                labelNomFournisseur,
                labelContact,
                labelDelaiLivraison,
                new Label("Produits du fournisseur :"),
                tableProduits,
                new Label("Historique de commandes :"),
                tableHistorique
        );

        // SplitPane pour séparer la liste des fournisseurs à gauche
        // et les détails du fournisseur sélectionné à droite
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(listViewFournisseurs, vboxDetails);
        splitPane.setDividerPositions(0.3); // 30% / 70%

        this.setCenter(splitPane);

        // ---------------------------------------------------------
        // Chargement initial des données (simulé)
        // ---------------------------------------------------------
        chargerFournisseursParDefaut();
    }

    /**
     * Affiche les détails d'un fournisseur dans la zone de droite :
     * - Met à jour les labels (nom, contact, délai)
     * - Alimente la table des produits
     * - Alimente la table de l'historique de commandes
     */
    private void afficherDetailsFournisseur(Fournisseur fournisseur){
        labelNomFournisseur.setText("Nom : " + fournisseur.getNom());
        labelContact.setText("Contact : " + fournisseur.getContact());
        labelDelaiLivraison.setText("Délai de livraison : " + fournisseur.getDelaiLivraison() + " jours");

        // Produits proposés par ce fournisseur
        // ICI, on ferait un appel HTTP pour récupérer la liste des produits du fournisseur
        tableProduits.getItems().setAll(fournisseur.getProduits());

        // Historique des commandes passées auprès de ce fournisseur
        // ICI, on ferait un appel HTTP pour récupérer l'historique
        tableHistorique.getItems().setAll(fournisseur.getHistoriqueCommandes());
    }

    /**
     * Charge la liste des fournisseurs (en dur ici).
     * À remplacer par un appel HTTP pour récupérer la liste depuis l'API.
     */
    private void chargerFournisseursParDefaut() throws IOException {
        //Exemple de connection à la bd pour récupérer les produits bientôt expirés
        URL urlFournisseur = new URL("http://localhost:25565/api/???");
        HttpURLConnection conFournisseur = (HttpURLConnection) urlFournisseur.openConnection();
        //GET : Pour la lecture
        //POST: Création d'un nouvel élément ou paramètres complexes
        //PUT: Mise à jour d'une donnée
        //Le mieux pour simplement afficher les fournisseurs est GET
        conFournisseur.setRequestMethod("GET");
        //https://www.baeldung.com/java-http-request




        List<Fournisseur> fournisseurs = new ArrayList<>();

        // SIMULATION 1
        Fournisseur f1 = new Fournisseur(
                "Fournisseur A",
                "contactA@exemple.com",
                3  // délai de livraison en jours
        );
        // Produits du fournisseur A
        f1.getProduits().add(new ProduitFournisseur("Yaourt Bio", "Produits laitiers", new BigDecimal("1.20")));
        f1.getProduits().add(new ProduitFournisseur("Fromage blanc", "Produits laitiers", new BigDecimal("1.80")));
        // Historique
        f1.getHistoriqueCommandes().add(new Commande(LocalDate.now().minusDays(10), "Yaourt Bio", 50, new BigDecimal("60.00")));
        f1.getHistoriqueCommandes().add(new Commande(LocalDate.now().minusDays(20), "Fromage blanc", 30, new BigDecimal("54.00")));

        // SIMULATION 2
        Fournisseur f2 = new Fournisseur(
                "Fournisseur B",
                "contactB@exemple.com",
                5
        );
        f2.getProduits().add(new ProduitFournisseur("Pommes", "Fruits & Légumes", new BigDecimal("0.50")));
        f2.getProduits().add(new ProduitFournisseur("Poires", "Fruits & Légumes", new BigDecimal("0.60")));
        // Historique
        f2.getHistoriqueCommandes().add(new Commande(LocalDate.now().minusDays(5), "Pommes", 100, new BigDecimal("50.00")));
        f2.getHistoriqueCommandes().add(new Commande(LocalDate.now().minusDays(8), "Poires", 80, new BigDecimal("48.00")));

        // SIMULATION 3
        Fournisseur f3 = new Fournisseur(
                "Fournisseur C",
                "contactC@exemple.com",
                7
        );
        f3.getProduits().add(new ProduitFournisseur("Soda X", "Boissons", new BigDecimal("1.30")));
        // Historique
        f3.getHistoriqueCommandes().add(new Commande(LocalDate.now().minusDays(1), "Soda X", 60, new BigDecimal("78.00")));

        fournisseurs.add(f1);
        fournisseurs.add(f2);
        fournisseurs.add(f3);

        // On affecte la liste au ListView
        listViewFournisseurs.getItems().setAll(fournisseurs);
    }

    // -------------------------------------------------------------------------
    // Classes internes de modèles (fournisseurs, produits, commandes)
    // -------------------------------------------------------------------------

    /**
     * Représente un fournisseur avec :
     * - nom
     * - contact (email / téléphone)
     * - délai de livraison (en jours)
     * - liste de produits (simulée)
     * - historique de commandes (simulé)
     */
    public static class Fournisseur {
        private final String nom;
        private final String contact;
        private final int delaiLivraison;

        private final List<ProduitFournisseur> produits;
        private final List<Commande> historiqueCommandes;

        public Fournisseur(String nom, String contact, int delaiLivraison) {
            this.nom = nom;
            this.contact = contact;
            this.delaiLivraison = delaiLivraison;
            this.produits = new ArrayList<>();
            this.historiqueCommandes = new ArrayList<>();
        }

        public String getNom() {
            return nom;
        }

        public String getContact() {
            return contact;
        }

        public int getDelaiLivraison() {
            return delaiLivraison;
        }

        public List<ProduitFournisseur> getProduits() {
            return produits;
        }

        public List<Commande> getHistoriqueCommandes() {
            return historiqueCommandes;
        }
    }

    /**
     * Représente un produit fourni par un fournisseur :
     * - nom du produit
     * - catégorie
     * - prix (BigDecimal)
     */
    public static class ProduitFournisseur {
        private final SimpleStringProperty nom;
        private final SimpleStringProperty categorie;
        private final SimpleObjectProperty<BigDecimal> prix;

        public ProduitFournisseur(String nom, String categorie, BigDecimal prix) {
            this.nom = new SimpleStringProperty(nom);
            this.categorie = new SimpleStringProperty(categorie);
            this.prix = new SimpleObjectProperty<>(prix);
        }

        public String getNom() {
            return nom.get();
        }
        public SimpleStringProperty nomProperty() {
            return nom;
        }

        public String getCategorie() {
            return categorie.get();
        }
        public SimpleStringProperty categorieProperty() {
            return categorie;
        }

        public BigDecimal getPrix() {
            return prix.get();
        }
        public SimpleObjectProperty<BigDecimal> prixProperty() {
            return prix;
        }
    }

    /**
     * Représente une commande (dans l'historique) :
     * - date
     * - produit
     * - quantité
     * - montant total (BigDecimal)
     */
    public static class Commande {
        private final SimpleObjectProperty<LocalDate> date;
        private final SimpleStringProperty produit;
        private final SimpleIntegerProperty quantite;
        private final SimpleObjectProperty<BigDecimal> montant;

        public Commande(LocalDate date, String produit, int quantite, BigDecimal montant) {
            this.date = new SimpleObjectProperty<>(date);
            this.produit = new SimpleStringProperty(produit);
            this.quantite = new SimpleIntegerProperty(quantite);
            this.montant = new SimpleObjectProperty<>(montant);
        }

        public LocalDate getDate() {
            return date.get();
        }
        public SimpleObjectProperty<LocalDate> dateProperty() {
            return date;
        }

        public String getProduit() {
            return produit.get();
        }
        public SimpleStringProperty produitProperty() {
            return produit;
        }

        public int getQuantite() {
            return quantite.get();
        }
        public SimpleIntegerProperty quantiteProperty() {
            return quantite;
        }

        public BigDecimal getMontant() {
            return montant.get();
        }
        public SimpleObjectProperty<BigDecimal> montantProperty() {
            return montant;
        }
    }
}

