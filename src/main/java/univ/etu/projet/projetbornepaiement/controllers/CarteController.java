package univ.etu.projet.projetbornepaiement.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import univ.etu.projet.projetbornepaiement.SceneManager;
import univ.etu.projet.projetbornepaiement.models.Carte;
import univ.etu.projet.projetbornepaiement.models.Plat;
import univ.etu.projet.projetbornepaiement.services.CouponService; // Ton service BDD
import univ.etu.projet.projetbornepaiement.utils.PinPadService; // Ton service Matériel

import java.io.IOException;
import java.util.Map;

public class CarteController {

    @FXML private VBox cartContainer;
    @FXML private Label totalLabel;
    @FXML private TextField couponField; // Affichera la saisie du PinPad

    // Buffer pour stocker les chiffres tapés sur le PinPad (ex: "123")
    private final StringBuilder codePromoBuffer = new StringBuilder();

    // Le service qui parle à la base de données
    private final CouponService couponService = new CouponService();

    @FXML
    public void initialize() {
        //  Afficher le panier
        refreshCartDisplay();

        //  Vérifier si un coupon est DÉJÀ appliqué (cas où on revient du menu)
        if (Carte.getInstance().getAppliedCoupon() != null) {
            double val = Carte.getInstance().getDiscountAmount();
            couponField.setText("REDUC -" + val + "€");
            couponField.setStyle("-fx-border-color: green; -fx-text-fill: green; -fx-font-weight: bold;");
        }

        // Démarrer l'écoute du PinPad sur COM5
        PinPadService.getInstance().startListening("COM5", this::handlePinPadInput);
    }

    /**
     * Méthode appelée à chaque touche du PinPad
     */
    private void handlePinPadInput(String rawData) {
        if (rawData == null || rawData.isEmpty()) return;

        int ascii = (int) rawData.charAt(0);

        // Mises à jour graphiques obligatoires dans le thread JavaFX
        Platform.runLater(() -> {

            //  CHIFFRES (0-9)
            if (Character.isDigit(rawData.charAt(0))) {
                // On limite le code à 3 chiffres comme demandé
                if (codePromoBuffer.length() < 3) {
                    codePromoBuffer.append(rawData.trim());
                    couponField.setText(codePromoBuffer.toString());
                    couponField.setStyle(""); // Reset style
                }
            }

            // CORRIGER (Touche Jaune / ASCII 8)
            else if (ascii == 8) {
                if (codePromoBuffer.length() > 0) {
                    codePromoBuffer.deleteCharAt(codePromoBuffer.length() - 1);
                    couponField.setText(codePromoBuffer.toString());
                }
            }

            // VALIDER (Touche Verte / ASCII 13)
            else if (ascii == 13) {
                verifierCodePromo();
            }
        });
    }

    /**
     * Logique de vérification via Hibernate
     */
    private void verifierCodePromo() {
        String code = codePromoBuffer.toString();

        // Appel BDD
        CouponService.ResultatCoupon resultat = couponService.verifierEtAppliquer(code);

        switch (resultat) {
            case VALIDE:
                couponField.setStyle("-fx-border-color: green; -fx-text-fill: green; -fx-font-weight: bold;");
                couponField.setText("REDUC -" + Carte.getInstance().getDiscountAmount() + "€");
                updateTotal(); // Met à jour le total en bas

                // On vide le buffer pour éviter de retaper par dessus
                codePromoBuffer.setLength(0);
                break;

            case PERIME:
                couponField.setStyle("-fx-border-color: red; -fx-text-fill: red;");
                couponField.setText("COUPON EXPIRÉ");
                codePromoBuffer.setLength(0);
                break;

            case DEJA_UTILISE:
                couponField.setStyle("-fx-border-color: orange; -fx-text-fill: orange;");
                couponField.setText("DÉJÀ UTILISÉ");
                codePromoBuffer.setLength(0);
                break;

            default: // INCONNU
                couponField.setStyle("-fx-border-color: red; -fx-text-fill: red;");
                couponField.setText("CODE INCONNU");
                codePromoBuffer.setLength(0);
        }
    }

    private void refreshCartDisplay() {
        cartContainer.getChildren().clear();
        Map<Plat, Integer> items = Carte.getInstance().getItems();

        if (items.isEmpty()) {
            cartContainer.getChildren().add(new Label("Votre panier est vide."));
        } else {
            for (Map.Entry<Plat, Integer> entry : items.entrySet()) {
                cartContainer.getChildren().add(createCartRow(entry.getKey(), entry.getValue()));
            }
        }
        updateTotal();
    }

    private HBox createCartRow(Plat p, int quantity) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");

        Label nameLbl = new Label(p.getName());
        nameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label qtyLbl = new Label("x " + quantity);
        Label priceLbl = new Label(String.format("%.2f €", p.getPrice() * quantity));

        Button removeBtn = new Button("-");
        removeBtn.setOnAction(e -> {
            Carte.getInstance().removeProduct(p);
            refreshCartDisplay();
        });

        row.getChildren().addAll(nameLbl, spacer, qtyLbl, priceLbl, removeBtn);
        return row;
    }

    private void updateTotal() {
        // Carte.getInstance().getTotal() calcule déjà le total MOINS la réduction
        totalLabel.setText(String.format("%.2f €", Carte.getInstance().getTotal()));
    }

    // Bouton Interface (Optionnel si tu utilises le PinPad, mais on le garde)
    @FXML
    private void handleApplyCoupon() {
        // Simule l'appui sur "Entrée" avec le texte du champ
        codePromoBuffer.setLength(0);
        codePromoBuffer.append(couponField.getText());
        verifierCodePromo();
    }

    @FXML
    private void handleBackToMenu() throws IOException {
        // IMPORTANT : Arrêter le PinPad avant de partir
        PinPadService.getInstance().stop();
        SceneManager.setRoot("menu-view.fxml");
    }

    @FXML
    private void handleClearCart() {
        Carte.getInstance().clear();
        refreshCartDisplay();
        couponField.setText("");
        couponField.setStyle("");
    }

    @FXML
    private void handlePayment() throws IOException {
        if (Carte.getInstance().getItems().isEmpty()) return;

        // IMPORTANT : Arrêter le PinPad avant de partir
        PinPadService.getInstance().stop();

        System.out.println("Passage au paiement...");
        // Navigation vers ton écran "Sur Place / Emporter"
        SceneManager.setRoot("emporterCommande.fxml");
    }
}