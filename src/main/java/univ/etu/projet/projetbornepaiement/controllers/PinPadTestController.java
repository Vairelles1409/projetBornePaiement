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

    // Méthode appelée quand le PinPad envoie une valeur
   /* private void afficherDonnees(String data) {
        // On affiche brut ce qui arrive
        logArea.appendText("Reçu : " + data + "\n");  }*/
        private void afficherDonnees(String data) {
            System.out.print("TOUCHE REÇUE -> ");

            // On convertit la chaîne en tableau de caractères pour voir le code de chacun
            for (char c : data.toCharArray()) {
                System.out.print("Symbole: [" + c + "] Code ASCII: " + (int)c + "  |  ");
            }
            System.out.println(""); // Retour à la ligne



        System.out.println("DEBUG COM5 : " + data);
    }

    @FXML
    private void handleClear() {
        logArea.clear();
        logArea.appendText("Zone effacée. Prêt.\n");
    }

    @FXML
    private void handleBack() throws IOException {
        // Arrêt de service
        PinPadService.getInstance().stop();
        SceneManager.setRoot("welcome-view.fxml");
    }
}