package etu.uni.example;

import javax.smartcardio.*;
import java.util.List;

public class CardCommunication {
    public static final byte[] SELECT_APDU = {
            0x00, (byte)0xA4, 0x04, 0x00, 0x08,
            (byte)0xF0, 0x45, 0x4E, 0x53, 0x49, 0x00, 0x07,	0x01
    };

    public static final byte[] GET_SOLDE = { (byte)0x80, 0x50, 0x00, 0x00, 0x02};
    public static final byte[] VERIFY_PIN = { (byte)0x80, 0x20, 0x00, 0x00, 0x04};
    public static final byte[] CREDIT = { (byte)0x80, 0x30, 0x00, 0x00, 0x02};
    public static final byte[] DEBIT = { (byte)0x80, 0x40, 0x00, 0x00, 0x02};

    public static byte[] responseApdu;
    public static byte[] statusWord= new byte [2];


    private final CardChannel channel;
    public CardCommunication(CardChannel channel) {
        this.channel = channel;
    }

    public byte[] sendApdu(byte[] apdu) throws CardException {
        // Création de la commande APDU
        CommandAPDU command = new CommandAPDU(apdu);
        System.out.printf("Envoi APDU : %s%n", toHexString(command.getBytes()));

        // Envoi de la commande et réception de la réponse
        ResponseAPDU response = channel.transmit(command);
        System.out.printf("Réponse APDU : %s%n", toHexString(response.getBytes()));
        return response.getBytes();
    }

    public void sendApdu(byte[] apdu, byte data) throws CardException {
        byte [] dataArr = new byte[1];
        dataArr[0] = data;
        sendApdu(apdu, dataArr);
    }

    public void sendApdu(byte[] apdu, byte [] data) throws CardException {
        byte [] fullApdu = new byte[apdu.length + data.length];
        System.arraycopy(apdu, 0, fullApdu, 0, apdu.length);
        System.arraycopy(data, 0, fullApdu, apdu.length, data.length);

        responseApdu=sendApdu(fullApdu);
        statusWord[0]=responseApdu[responseApdu.length-2];
        statusWord[1]=responseApdu[responseApdu.length-1];
    }

    public void sendApdu(byte[] apdu, short value) throws CardException {
        sendApdu(apdu, short2byte(value));
    }

    public static byte[] short2byte(short value) {
        byte [] data = new byte[2];
        data[0] = (byte)((value >> 8) & 0xFF);
        data[1] = (byte)(value & 0xFF);
        return data;
    }


    public static short byte2short(byte[] value) {
        return (short)(((short)(value[0] & 0xFF) << 8) | (value[1] & 0xFF));
    }

    public static String toHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }

    public static byte[] int2byte(int [] value) {
        byte [] data = new byte[value.length];
        for (int j=0; j<value.length; j++){
            data [j]=(byte) (value[j]+0x30);
        }
        return data;
    }

}
