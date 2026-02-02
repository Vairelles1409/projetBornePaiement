package univ.etu.projet.projetbornepaiement.services;

import io.nayuki.qrcodegen.QrCode;
import javafx.scene.image.Image;
import javafx.embed.swing.SwingFXUtils;

import java.awt.image.BufferedImage;


/**
 * Service pour générer des QR Codes en JavaFX.
 */
public class QR_codeService {

    public static Image generateQRCode(String text, int size) {
        // Génération du QR code
        QrCode qr = QrCode.encodeText(text, QrCode.Ecc.MEDIUM);

        int qrSize = qr.size; // nombre de modules
        int scale = size / qrSize;
        int border = 4;
        int imageSize = (qrSize + border * 2) * scale;

        BufferedImage buffered = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < imageSize; y++) {
            for (int x = 0; x < imageSize; x++) {
                int qrX = (x / scale) - border;
                int qrY = (y / scale) - border;
                boolean black = qrX >= 0 && qrY >= 0 && qrX < qrSize && qrY < qrSize && qr.getModule(qrX, qrY);
                buffered.setRGB(x, y, black ? 0x000000 : 0xFFFFFF);
            }
        }

        // Conversion en Image JavaFX
        return SwingFXUtils.toFXImage(buffered, null);
    }
}
