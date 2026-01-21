package univ.etu.projet.projetbornepaiement.services;

import javax.smartcardio.*;
import java.util.List;

public class CardCommand {

    // --- SINGLETON ---
    private static final CardCommand instance = new CardCommand();
    public static CardCommand getInstance() { return instance; }
    private CardCommand() {}
    // -----------------

    // Variables globales
    private Card card; // Garder la carte en mémoire
    private CardCommunication comm;

    public boolean selectok = false;
    public boolean pinok = false;
    public boolean debitok = false;
    public int seuilPin = 3; // Initialisé à 3 par défaut

    private static final String statusWordOK = "90 00";

    // Etape 1 : Connexion et Sélection
    public boolean selectApdu() {
        try {
            // Si déjà connecté, on retourne true direct
            if (card != null && selectok) return true;

            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = factory.terminals().list();

            if (terminals.isEmpty()) {
                System.out.println("Aucun lecteur détecté.");
                return false;
            }

            CardTerminal terminal = terminals.get(0);

            // Connexion T=0
            card = terminal.connect("T=0");
            System.out.println("Carte connectée : " + card);

            comm = new CardCommunication(card.getBasicChannel());

            // Sélection de l'applet
            CardCommunication.responseApdu = comm.sendApdu(CardCommunication.SELECT_APDU);
            CardCommunication.statusWord[0] = CardCommunication.responseApdu[CardCommunication.responseApdu.length - 2];
            CardCommunication.statusWord[1] = CardCommunication.responseApdu[CardCommunication.responseApdu.length - 1];

            if (CardCommunication.toHexString(CardCommunication.statusWord).equals(statusWordOK)) {
                selectok = true;
                System.out.println("select ok");
                return true;
            }
            return false;

        } catch (CardException e) {
            System.out.println("Erreur lecteur/carte : " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Etape 2 : Vérification du PIN (Adapté pour recevoir le String du PinPad)
    public boolean verifierPin(String pinString) {
        try {
            if (!selectok) return false;

            // Conversion String ("1234") -> byte[]
            // '1' vaut 0x31 en ASCII, donc getBytes() fait exactement ce que faisait ton int2byte
            byte[] data = pinString.getBytes();

            comm.sendApdu(CardCommunication.VERIFY_PIN, data);

            if (CardCommunication.toHexString(CardCommunication.statusWord).equals(statusWordOK)) {
                pinok = true;
                seuilPin = 3; // Reset essais
                return true;
            } else {
                seuilPin--;
                return false;
            }

        } catch (CardException e) {
            throw new RuntimeException(e);
        }
    }

    // Etape 3 : Débit
    public boolean debiterCarte(double montantDouble) {
        try {
            if (!pinok) return false;

            // Conversion double -> short (selon ta logique métier)
            short montant = (short) montantDouble;

            comm.sendApdu(CardCommunication.DEBIT, montant);

            if (CardCommunication.toHexString(CardCommunication.statusWord).equals(statusWordOK)) {
                debitok = true;
                System.out.println("paiement effectué avec succes");
                return true;
            }
            return false;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        if (card != null) {
            try { card.disconnect(false); } catch (Exception e) {}
            card = null;
            comm = null;
            selectok = false;
            pinok = false;
        }
    }
}