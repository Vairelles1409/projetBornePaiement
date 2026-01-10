package univ.etu.projet.projetbornepaiement.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import univ.etu.projet.projetbornepaiement.SceneManager;
import univ.etu.projet.projetbornepaiement.models.Carte;
import univ.etu.projet.projetbornepaiement.utils.PinPadService;

import java.io.IOException;

public class PaymentPinController {

    @FXML private Label amountLabel;
    @FXML private Label pinDisplayLabel;
    @FXML private Label statusLabel;

    private StringBuilder currentPin = new StringBuilder();

    @FXML
    public void initialize() {
        // 1. Récupérer et afficher le montant du panier
        double total = Carte.getInstance().getTotal();
        amountLabel.setText(String.format("%.2f €", total));

        // 2. Démarrer l'écoute du PinPad (COM5)
        // On utilise ton Service existant
        boolean started = PinPadService.getInstance().startListening("COM5", this::handlePinInput);

        if (!started) {
            statusLabel.setText("ERREUR : PinPad non détecté !");
            statusLabel.setStyle("-fx-text-fill: red;");
        }

        updatePinDisplay();
    }

    // Méthode appelée quand le PinPad envoie une touche
    private void handlePinInput(String rawData) {
        String key = rawData.trim();
        if (key.isEmpty()) return;

        // --- Logique Métier ---

        // 1. CHIFFRES (0-9)
        if (key.matches("[0-9]")) {
            if (currentPin.length() < 4) {
                currentPin.append(key);
                updatePinDisplay();
                statusLabel.setText("Saisie en cours...");
            }
        }
        // 2. CORRECTION (Touche Jaune / Backspace)
        // Vérifie si ton PinPad envoie '\b' ou autre chose pour la correction
        else if (key.equals("\b") || key.contains("CORR")) {
            if (currentPin.length() > 0) {
                currentPin.deleteCharAt(currentPin.length() - 1);
                updatePinDisplay();
            }
        }
        // 3. VALIDATION (Touche Verte / Entrée)
        else if (key.equals("\r") || key.equals("\n")) {
            if (currentPin.length() == 4) {
                processPayment();
            } else {
                Platform.runLater(() -> {
                    statusLabel.setText("Code incomplet (4 chiffres requis)");
                    statusLabel.setStyle("-fx-text-fill: red;");
                });
            }
        }
    }

    private void updatePinDisplay() {
        // Affiche des ronds noirs
        StringBuilder visual = new StringBuilder();
        for (int i = 0; i < currentPin.length(); i++) {
            visual.append("●");
        }
        Platform.runLater(() -> pinDisplayLabel.setText(visual.toString()));
    }

    private void processPayment() {
        // Arrêt de l'écoute du clavier
        PinPadService.getInstance().stop();

        Platform.runLater(() -> {
            statusLabel.setText("Vérification du code PIN...");
            statusLabel.setStyle("-fx-text-fill: blue;");
        });

        String finalPin = currentPin.toString();
        System.out.println("PIN saisi pour transaction de " + amountLabel.getText() + " : " + finalPin);

        // --- C'est ICI qu'on appellera JavaCard plus tard ---
        // boolean success = JavaCardService.verifyPin(finalPin);

        // Simulation pour l'instant :
        new Thread(() -> {
            try { Thread.sleep(1500); } catch (InterruptedException e) {}
            Platform.runLater(() -> {
                // Si Code Bon :
                System.out.println("Paiement Accepté !");
                // SceneManager.setRoot("ticket-view.fxml");
                statusLabel.setText("PAIEMENT ACCEPTÉ !");
                statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 18px;");
            });
        }).start();
    }

    @FXML
    private void handleCancel() throws IOException {
        PinPadService.getInstance().stop(); // Toujours arrêter le service !
        SceneManager.setRoot("welcome-view.fxml"); // Retour accueil ou panier
    }
}