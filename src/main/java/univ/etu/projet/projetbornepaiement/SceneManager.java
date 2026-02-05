/*package univ.etu.projet.projetbornepaiement;

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
   /* public static void setRoot(String fxmlFileName) throws IOException {
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
}*/
package univ.etu.projet.projetbornepaiement;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SceneManager {
    private static Stage stage;
    private static Scene scene;

    public static void setStage(Stage s) {
        stage = s;
    }

    public static void setRoot(String fxmlFileName) throws IOException {
        // 1. On s'assure de l'extension
        if (!fxmlFileName.endsWith(".fxml")) fxmlFileName += ".fxml";

        URL fxmlUrl = null;

        // --- STRATÉGIE DE RECHERCHE ---

        // 1. Relatif à la classe BorneApp (Fonctionne si FXML et Java sont côte à côte)
        fxmlUrl = BorneApplication.class.getResource(fxmlFileName);

        // 2. Chemin Absolu précis (Celui que tu as vu dans le JAR)
        // C'est souvent celui-ci qui sauve la mise dans un JAR modulaire
        if (fxmlUrl == null) {
            fxmlUrl = BorneApplication.class.getResource("/univ/etu/projet/projetbornepaiement/" + fxmlFileName);
        }

        // 3. Au cas où tu aurais un sous-dossier 'views' qui traîne
        if (fxmlUrl == null) {
            fxmlUrl = BorneApplication.class.getResource("views/" + fxmlFileName);
        }

        // --- DIAGNOSTIC ---
        if (fxmlUrl == null) {
            System.err.println("❌ CRITIQUE : Fichier introuvable dans le JAR !");
            System.err.println("   J'ai cherché : " + fxmlFileName);
            System.err.println("   J'ai cherché : /univ/etu/projet/projetbornepaiement/" + fxmlFileName);
            throw new IllegalStateException("Fichier FXML introuvable : " + fxmlFileName);
        } else {
            System.out.println("✅ Vue chargée depuis : " + fxmlUrl);
        }

        // --- CHARGEMENT ---
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        Parent root = fxmlLoader.load();

        if (scene == null) {
            scene = new Scene(root);
            stage.setScene(scene);
        } else {
            scene.setRoot(root);
        }
        stage.show();
    }
}