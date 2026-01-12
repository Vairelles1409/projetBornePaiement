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
     * @param portName "COM6"
     * @param onDataReceived Fonction appelée pour afficher le texte
     */
    public boolean startListening(String portName, Consumer<String> onDataReceived) {
        // Si déjà en marche, on ne fait rien
        if (isRunning) return true;

        // Configuration du port
        port = SerialPort.getCommPort(portName);
        port.setBaudRate(9600);

        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

        if (!port.openPort()) {
            System.err.println("ERREUR : Impossible d'ouvrir le port " + portName);
            return false;
        }

        isRunning = true;
        System.out.println("Service démarré sur " + portName);

        // Lancement du Thread
        Thread listenerThread = new Thread(() -> {
            try (InputStream in = port.getInputStream()) {
                byte[] buffer = new byte[1024];

                while (isRunning) {
                    if (in.available() > 0) {
                        int numRead = in.read(buffer);

                        // Conversion des octets
                        String receivedData = new String(buffer, 0, numRead);

                        // Envoi vers l'interface graphique
                        Platform.runLater(() -> onDataReceived.accept(receivedData));
                    }
                    Thread.sleep(10);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                port.closePort();
                System.out.println("Port fermé.");
            }
        });

        listenerThread.setDaemon(true);
        listenerThread.start();
        return true;
    }

    public void stop() {
        isRunning = false;
    }
}