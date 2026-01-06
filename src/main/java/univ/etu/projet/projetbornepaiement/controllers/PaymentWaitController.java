package univ.etu.projet.projetbornepaiement.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import univ.etu.projet.projetbornepaiement.SceneManager;
import univ.etu.projet.projetbornepaiement.models.Carte;

import javax.smartcardio.*;
import java.io.IOException;
import java.util.List;

public class PaymentWaitController {

    @FXML private Label amountLabel;
    @FXML private Label statusLabel;

    private boolean keepChecking = true; // Pour arrêter le thread si on change de page

    @FXML
    public void initialize() {
        // 1. Afficher le montant
        amountLabel.setText(String.format("%.2f €", Carte.getInstance().getTotal()));

        // 2. Lancer la surveillance du lecteur dans un autre Thread
        Thread cardDetector = new Thread(this::waitForCardTask);
        cardDetector.setDaemon(true); // S'arrête si l'appli ferme
        cardDetector.start();
    }

    private void waitForCardTask() {
        try {
            // Récupérer la factory de terminaux
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = factory.terminals().list();

            if (terminals.isEmpty()) {
                updateStatus("Aucun lecteur de carte détecté !", true);
                return;
            }

            // On prend le premier lecteur trouvé
            CardTerminal terminal = terminals.get(0);
            updateStatus("Lecteur prêt : " + terminal.getName(), false);

            // Boucle d'attente
            while (keepChecking) {
                // Cette méthode bloque le thread jusqu'à ce qu'une carte soit mise (ou retirée)
                if (terminal.waitForCardPresent(1000)) { // Vérifie chaque seconde
                    if (terminal.isCardPresent()) {

                        // CARTE DÉTECTÉE !
                        updateStatus("Carte détectée ! Lecture en cours...", false);

                        // On laisse un petit délai pour l'effet visuel
                        Thread.sleep(1000);

                        // On connecte la carte (On fera les échanges APDU plus tard)
                        // Card card = terminal.connect("*");

                        // On passe à l'écran suivant (Code PIN)
                        Platform.runLater(() -> goToPinScreen());
                        break; // On sort de la boucle
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            updateStatus("Erreur lecteur : " + e.getMessage(), true);
        }
    }

    // Méthode utilitaire pour mettre à jour l'interface depuis le Thread secondaire
    private void updateStatus(String msg, boolean isError) {
        Platform.runLater(() -> {
            statusLabel.setText(msg);
            if (isError) statusLabel.setStyle("-fx-text-fill: red;");
            else statusLabel.setStyle("-fx-text-fill: green;");
        });
    }

    // --- Navigation ---

    private void goToPinScreen() {
        try {
            // TODO: Créer la vue pin-entry-view.fxml plus tard
            System.out.println("Navigation vers PIN...");
            // SceneManager.setRoot("pin-entry-view.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() throws IOException {
        keepChecking = false; // Arrêter le thread
        SceneManager.setRoot("carte-view.fxml");
    }

    @FXML
    private void forceSimulation() {
        // Pour tester sans lecteur réel
        keepChecking = false;
        goToPinScreen();
    }
}