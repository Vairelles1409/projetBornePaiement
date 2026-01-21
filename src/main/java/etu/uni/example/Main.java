package etu.uni.example;
import javax.smartcardio.*;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {


    public static void main(String[] args) {

        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.

         int []pin2={1,2,3,4};
         short m=10;


         CardCommand communication= new CardCommand();
         communication.selectApdu();
        communication.verifierPin(communication.int2byte(pin2));

        while ((communication.pinok==false) && (communication.seuilPin > 0)){

            System.out.println("erreur: mauvais code");
            System.out.println("apres " + communication.seuilPin + "essaies, la carte sera blockée");

            //il faut get le nouveau pin saisie et l'appeller la fonction ci-dessous
            communication.verifierPin(communication.int2byte(pin2));

            communication.seuilPin--;
        }
        if (communication.seuilPin == 0) {
            //Commande bloquer la carte
        }

        communication.debiterCarte(m);

    }

}