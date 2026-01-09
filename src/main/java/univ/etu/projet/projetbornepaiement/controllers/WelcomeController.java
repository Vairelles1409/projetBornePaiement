package univ.etu.projet.projetbornepaiement.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import univ.etu.projet.projetbornepaiement.SceneManager;

import java.io.IOException;

public class WelcomeController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onStartButtonClick() {

        //welcomeText.setText("Welcome to JavaFX Application!");
        //System.out.println("Clic sur Démarrer !");
        try {
            SceneManager.setRoot("menu-view.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToTestMode() throws IOException {
        SceneManager.setRoot("pinpad-test-view.fxml");
    }

}
