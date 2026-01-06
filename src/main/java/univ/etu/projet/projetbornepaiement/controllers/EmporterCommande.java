package univ.etu.projet.projetbornepaiement.controllers;

import javafx.fxml.FXML;
import univ.etu.projet.projetbornepaiement.SceneManager;
import univ.etu.projet.projetbornepaiement.models.Carte;

import java.io.IOException;

public class EmporterCommande {

    @FXML
    private void handleEatIn() throws IOException {
        selectOption(false);
    }

    @FXML
    private void handleTakeAway() throws IOException {
        selectOption(true);
    }

    private void selectOption(boolean isTakeAway) throws IOException {
        // 1. On enregistre le choix dans le modèle
        Carte.getInstance().setTakeAway(isTakeAway);
        System.out.println("Mode choisi : " + (isTakeAway ? "À Emporter" : "Sur Place"));

        // 2. On passe (enfin) à l'écran de paiement
        SceneManager.setRoot("paiement.fxml");
    }

    @FXML
    private void handleBack() throws IOException {
        SceneManager.setRoot("carte-view.fxml");
    }
}