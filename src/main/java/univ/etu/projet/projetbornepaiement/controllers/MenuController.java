package univ.etu.projet.projetbornepaiement.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import univ.etu.projet.projetbornepaiement.SceneManager;
import univ.etu.projet.projetbornepaiement.models.Carte;
import univ.etu.projet.projetbornepaiement.models.ProductType;
import univ.etu.projet.projetbornepaiement.models.Produit;
import univ.etu.projet.projetbornepaiement.models.ProductType; // Import de l'Enum

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;

public class MenuController {

    // !!! ATTENTION : Dans ton FXML (menu-view.fxml), assure-toi que l'ID
    // de la VBox principale est bien fx:id="menuContainer" et non plus productsGrid
    @FXML private VBox menuContainer;

    // Nouveaux éléments pour le Mini-Panier
    @FXML private VBox miniCartContainer;
    @FXML private Label miniTotalLabel;

    private final List<Produit> productList = new ArrayList<>();

    @FXML
    public void initialize() {
        // Initialisation des produits
        loadFakeProducts();

        // Affichage par blocs (Menus, Plats, Boissons...)
        generateMenuBlocks();

        // Afficher le panier actuel dès l'ouverture
        refreshMiniCart();
    }

    private void loadFakeProducts() {
        // PLATS
        productList.add(new Produit("Koki", 15.00, ProductType.PLAT, "/images/koki.png"));
        productList.add(new Produit("Taro Sauce Jaune", 15.00, ProductType.PLAT, "taro.jpg"));
        productList.add(new Produit("Kondre", 15.00, ProductType.PLAT, "kondre.jpg"));
        productList.add(new Produit("2 Oeufs Spagetti", 15.00, ProductType.PLAT, "spagetti.png"));

        // BOISSONS
        productList.add(new Produit("Petite Guiness", 3.00, ProductType.BOISSON, "guiness.png"));
        productList.add(new Produit("Top Pamplemousse", 2.00, ProductType.BOISSON, "top.png"));

        // DESSERT
        productList.add(new Produit("BHB", 5.00, ProductType.DESSERT, "beignets.png"));
        productList.add(new Produit("Kossam Dakere", 5.00, ProductType.DESSERT, "dakere.png"));
    }

    /**
     * Génère l'affichage dynamique avec des Titres et des Grilles par catégorie.
     */
    private void generateMenuBlocks() {
        menuContainer.getChildren().clear();

        // 1. Grouper les produits par Type
        Map<ProductType, List<Produit>> productsByCategory = productList.stream()
                .collect(Collectors.groupingBy(Produit::getType));

        // 2. Parcourir l'Enum pour garder un ordre logique
        for (ProductType type : ProductType.values()) {

            if (productsByCategory.containsKey(type)) {
                List<Produit> productsInThisCategory = productsByCategory.get(type);

                // A. Titre de section
                Label sectionTitle = new Label(type.getLabel());
                sectionTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-padding: 0 0 10 0;");

                // B. Grille pour cette section
                GridPane sectionGrid = new GridPane();
                sectionGrid.setHgap(15);
                sectionGrid.setVgap(15);

                int col = 0;
                int row = 0;
                for (Produit p : productsInThisCategory) {
                    sectionGrid.add(createProductBox(p), col, row);
                    col++;
                    if (col == 3) { // 3 colonnes
                        col = 0;
                        row++;
                    }
                }

                // C. Ajout au conteneur principal
                VBox sectionBox = new VBox(5);
                sectionBox.getChildren().addAll(sectionTitle, sectionGrid);
                menuContainer.getChildren().add(sectionBox);

                // Espacement
                Region separator = new Region();
                separator.setPrefHeight(20);
                menuContainer.getChildren().add(separator);
            }
        }
    }

    private VBox createProductBox(Produit product) {
        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0); -fx-background-radius: 10; -fx-padding: 15;");
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(200);

        // NOM
        Label nameLabel = new Label(product.getName());
        nameLabel.setWrapText(true);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-alignment: center;");

        // IMAGE
        ImageView imageView = new ImageView();
        imageView.setFitHeight(100); // Taille fixe hauteur
        imageView.setFitWidth(150);  // Taille fixe largeur
        imageView.setPreserveRatio(true); // Garder les proportions

        // Chargement sécurisé de l'image
        try {
            String path = "/images/" + product.getImage();
            InputStream is = getClass().getResourceAsStream(path);

            if (is != null) {
                imageView.setImage(new Image(is));
            } else {
                // Si l'image n'est pas trouvée, on met une image par défaut ou rien
                // System.out.println("Image introuvable : " + path);
                // Tu pourrais mettre une image "placeholder.png" ici
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // PRIX
        Label priceLabel = new Label(product.getPrice() + " €");
        priceLabel.setStyle("-fx-text-fill: #E91E63; -fx-font-weight: bold; -fx-font-size: 14px;");

        // 4. LE BOUTON
        Button addButton = new Button("Ajouter");
        addButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand;");
        addButton.setOnAction(e -> {
            Carte.getInstance().addProduct(product);
            refreshMiniCart();
        });

        // Ordre d'ajout : Nom -> Image -> Prix -> Bouton
        box.getChildren().addAll(nameLabel, imageView, priceLabel, addButton);
        return box;
    }

    // --- LOGIQUE DU MINI PANIER (identique à ton code) ---

    private void refreshMiniCart() {
        miniCartContainer.getChildren().clear();
        Map<Produit, Integer> items = Carte.getInstance().getItems();

        for (Map.Entry<Produit, Integer> entry : items.entrySet()) {
            miniCartContainer.getChildren().add(createMiniCartRow(entry.getKey(), entry.getValue()));
        }

        miniTotalLabel.setText(String.format("%.2f €", Carte.getInstance().getTotal()));
    }

    private HBox createMiniCartRow(Produit p, int qty) {
        HBox row = new HBox(5);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-border-color: #eee; -fx-border-width: 0 0 1 0; -fx-padding: 5;");

        // Nom du produit
        VBox infoBox = new VBox(2);
        Label nameLbl = new Label(p.getName());
        nameLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        // Limite taille nom
        nameLbl.setMaxWidth(120);

        Label priceLbl = new Label(String.format("%.2f €", p.getPrice() * qty));
        priceLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
        infoBox.getChildren().addAll(nameLbl, priceLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button minusBtn = new Button("-");
        minusBtn.setStyle("-fx-font-size: 10px; -fx-min-width: 25px;");
        minusBtn.setOnAction(e -> {
            Carte.getInstance().removeProduct(p);
            refreshMiniCart();
        });

        Label qtyLbl = new Label(String.valueOf(qty));
        qtyLbl.setMinWidth(20);
        qtyLbl.setAlignment(Pos.CENTER);
        qtyLbl.setStyle("-fx-font-size: 12px;");

        Button plusBtn = new Button("+");
        plusBtn.setStyle("-fx-font-size: 10px; -fx-min-width: 25px;");
        plusBtn.setOnAction(e -> {
            Carte.getInstance().addProduct(p);
            refreshMiniCart();
        });

        // CROIX DE SUPPRESSION
        Button deleteBtn = new Button("✕");
        deleteBtn.setStyle("-fx-text-fill: red; -fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 12px;");
        deleteBtn.setOnAction(e -> {
            Carte.getInstance().deleteProduct(p);
            refreshMiniCart();
        });

        row.getChildren().addAll(infoBox, spacer, minusBtn, qtyLbl, plusBtn, deleteBtn);
        return row;
    }

    @FXML
    private void handleBack() throws IOException {
        SceneManager.setRoot("welcome-view.fxml");
    }

    @FXML
    private void handleGoToCart() throws IOException {
        SceneManager.setRoot("carte-view.fxml");
    }
}