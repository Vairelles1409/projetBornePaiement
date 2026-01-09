package univ.etu.projet.projetbornepaiement.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import univ.etu.projet.projetbornepaiement.SceneManager;
import univ.etu.projet.projetbornepaiement.utils.PinPadService;

import java.io.IOException;

public class PinPadTestController {

    @FXML private TextArea logArea;

    @FXML
    public void initialize() {
        logArea.setText("Initialisation du test sur COM5...\n");

        // Démarrage du service sur le port COM5
        boolean success = PinPadService.getInstance().startListening("COM5", this::afficherDonnees);

        if (success) {
            logArea.appendText(">> Port COM5 OUVERT avec succès.\n");
            logArea.appendText(">> Appuyez sur les touches du PinPad pour tester.\n");
        } else {
            logArea.appendText(">> ERREUR : Impossible d'ouvrir COM5.\n");
            logArea.appendText(">> Vérifiez qu'aucun autre logiciel (Putty, etc.) n'utilise le port.\n");
        }
    }

    // Méthode appelée quand le PinPad envoie quelque chose
    private void afficherDonnees(String data) {
        // On affiche brut ce qui arrive
        logArea.appendText("Reçu : " + data + "\n");

        // Debug dans la console IntelliJ aussi
        System.out.println("DEBUG COM5 : " + data);
    }

    @FXML
    private void handleClear() {
        logArea.clear();
        logArea.appendText("Zone effacée. Prêt.\n");
    }

    @FXML
    private void handleBack() throws IOException {
        // On arrête le service proprement avant de quitter
        PinPadService.getInstance().stop();
        SceneManager.setRoot("welcome-view.fxml");
    }
}