package univ.etu.projet.projetbornepaiement.utils;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;

import java.io.InputStream;
import java.util.function.Consumer;

public class PinPadService {

    private static final PinPadService instance = new PinPadService();
    private SerialPort port;
    private volatile boolean isRunning = false; // Pour contrôler la boucle

    public static PinPadService getInstance() { return instance; }
    private PinPadService() {}

    /**
     * Lance l'écoute.
     * @param portName "COM5"
     * @param onDataReceived Fonction appelée pour afficher le texte
     */
    public boolean startListening(String portName, Consumer<String> onDataReceived) {
        // Si déjà en marche, on ne fait rien
        if (isRunning) return true;

        // Configuration du port (Identique à ton code qui marche)
        port = SerialPort.getCommPort(portName);
        port.setBaudRate(9600);

        // Timeout important pour éviter de bloquer le processeur
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

        if (!port.openPort()) {
            System.err.println("ERREUR : Impossible d'ouvrir le port " + portName);
            return false;
        }

        isRunning = true;
        System.out.println("Service démarré sur " + portName);

        // Lancement du Thread (Tâche de fond)
        Thread listenerThread = new Thread(() -> {
            try (InputStream in = port.getInputStream()) {
                byte[] buffer = new byte[1024];

                while (isRunning) {
                    if (in.available() > 0) {
                        int numRead = in.read(buffer);

                        // Conversion des octets
                        String receivedData = new String(buffer, 0, numRead);

                        // Envoi vers l'interface graphique (JavaFX Thread)
                        Platform.runLater(() -> onDataReceived.accept(receivedData));
                    }
                    // Petite pause pour ne pas surcharger le CPU (10ms)
                    Thread.sleep(10);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                port.closePort();
                System.out.println("Port fermé.");
            }
        });

        listenerThread.setDaemon(true); // S'arrête si tu fermes la fenêtre
        listenerThread.start();
        return true;
    }

    public void stop() {
        isRunning = false;
    }
}