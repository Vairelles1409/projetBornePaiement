package univ.etu.projet.projetbornepaiement;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneManager {
    private static Stage stage;
    private static Scene scene;

    public static void setStage(Stage s) {
        stage = s;
    }

    /**
     * Change l'interface actuelle.
     *  @param fxmlFileName (le nom du fichier FXML)
     */
    public static void setRoot(String fxmlFileName) throws IOException {
        // On charge depuis le dossier de ressources correspondant au package
        FXMLLoader fxmlLoader = new FXMLLoader(BorneApplication.class.getResource(fxmlFileName));
        Parent root = fxmlLoader.load();

        if (scene == null) {
            scene = new Scene(root); // définition de la taille
            stage.setScene(scene);
        } else {
            scene.setRoot(root);
        }
        stage.show();
    }
}