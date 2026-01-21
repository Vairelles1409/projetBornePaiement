package etu.uni.example;
import javax.smartcardio.*;
import java.util.List;


public class CardCommand {

    private static boolean selectok = false;
    public boolean pinok = false;
    private static boolean debitok = false;
    public  int seuilPin = 2;


   // private byte[] Pin = {0x31, 0x32, 0x33, 0x34};
    private short montantDebit = 10;
    // private byte [] statusWord = {0x0, 0x0};
    private static final String statusWordOK = "90 00";
    private CardCommunication comm;


    public void selectApdu() {
        try {
            // Liste des lecteurs
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = factory.terminals().list();

            if (terminals.isEmpty()) {
                System.out.println("Aucun lecteur détecté.");
                return;
            }

            // Premier lecteur
            CardTerminal terminal = terminals.get(0);
            // System.out.println("Premier lecteur : " + terminal.getName());

            // connexion carte
            Card card = terminal.connect("T=0");
            System.out.println("Carte connectée : " + card);
            comm = new CardCommunication(card.getBasicChannel());

            // Sélection de l'applet
            CardCommunication.responseApdu = comm.sendApdu(CardCommunication.SELECT_APDU);
            CardCommunication.statusWord[0] = CardCommunication.responseApdu[CardCommunication.responseApdu.length - 2];
            CardCommunication.statusWord[1] = CardCommunication.responseApdu[CardCommunication.responseApdu.length - 1];

            if (CardCommunication.toHexString(CardCommunication.statusWord).equals(statusWordOK)) {
                selectok = true;
                System.out.println("select ok");

            }

        } catch (CardException e) {
            System.out.println("Erreur lecteur/carte : " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

// verification du pin à condition que le select ai réussi
    public void verifierPin(byte[] data) {
        try {

            if (selectok == true) {

                comm.sendApdu(CardCommunication.VERIFY_PIN, data);
                if (CardCommunication.toHexString(CardCommunication.statusWord).equals(statusWordOK)) {
                    pinok = true;
                }

            }


        } catch (CardException e) {
            throw new RuntimeException(e);
        }


    }

    // debiter le compte à condition que le pin soit vérifié
    public void debiterCarte (short montant){

        try {
            if (pinok==true){

                comm.sendApdu(CardCommunication.DEBIT, montantDebit);

                if (CardCommunication.toHexString(CardCommunication.statusWord).equals(statusWordOK)) {
                    debitok=true;
                    System.out.println("paiement effectué avec succes");

                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //convertir le pin en byte qui sera envoié dans l'apdu
    public static byte[] int2byte(int [] value) {
        byte [] data = new byte[value.length];
        for (int j=0; j<value.length; j++){
            data [j]=(byte) (0x30+value[j]);
        }
        return data;
    }
}
