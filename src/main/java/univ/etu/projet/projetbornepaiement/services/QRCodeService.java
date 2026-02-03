package univ.etu.projet.projetbornepaiement.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class QRCodeService {

    public static Image generateQRCode(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            // Création de la matrice de bits (Noir/Blanc)
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

            // Conversion en Image Java standard (BufferedImage)
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Conversion en Image JavaFX pour l'affichage
            return SwingFXUtils.toFXImage(bufferedImage, null);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}