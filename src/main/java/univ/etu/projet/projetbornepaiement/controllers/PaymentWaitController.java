package univ.etu.projet.projetbornepaiement.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import univ.etu.projet.projetbornepaiement.SceneManager;
import univ.etu.projet.projetbornepaiement.models.Carte;
import univ.etu.projet.projetbornepaiement.services.CardCommand; // Ton service monétique

import javax.smartcardio.*;
import java.io.IOException;
import java.util.List;

public class PaymentWaitController {

    @FXML private Label amountLabel;
    @FXML private Label statusLabel;
    private boolean keepChecking = true;

    @FXML
    public void initialize() {
        amountLabel.setText(String.format("%.2f €", Carte.getInstance().getTotal()));
        Thread cardDetector = new Thread(this::waitForCardTask);
        cardDetector.setDaemon(true);
        cardDetector.start();
    }

    private void waitForCardTask() {
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = factory.terminals().list();

            if (terminals.isEmpty()) {
                updateStatus("Aucun lecteur détecté", true);
                return;
            }

            CardTerminal terminal = terminals.get(0);
            updateStatus("Lecteur prêt : " + terminal.getName(), false);

            while (keepChecking) {
                if (terminal.waitForCardPresent(500)) {
                    if (terminal.isCardPresent()) {
                        updateStatus("Carte détectée ! Connexion...", false);

                        // --- INTEGRATION : Appel du Singleton ---
                        boolean success = CardCommand.getInstance().selectApdu();

                        if (success) {
                            updateStatus("Applet sélectionnée. Passage au PIN...", false);
                            keepChecking = false;
                            Thread.sleep(500);
                            Platform.runLater(this::goToPinScreen);
                            break;
                        } else {
                            updateStatus("Carte invalide ou Applet introuvable.", true);
                            Thread.sleep(2000);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateStatus(String msg, boolean isError) {
        Platform.runLater(() -> {
            statusLabel.setText(msg);
            statusLabel.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        });
    }

    private void goToPinScreen() {
        try {
            SceneManager.setRoot("payment-pin-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() throws IOException {
        keepChecking = false; // Arrêter le thread
        CardCommand.getInstance().disconnect(); // On libère la carte si on annule
        SceneManager.setRoot("carte-view.fxml"); // Retour panier
    }

    @FXML
    private void forceSimulation() {
        // Pour tester sans lecteur réel
        keepChecking = false;
        goToPinScreen();
    }
}