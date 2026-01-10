package etu.ensi.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "paiements")
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_payment;

    private String card_network;

    @Temporal(TemporalType.TIMESTAMP)
    private Date payment_date;

    private double final_amount;
    private String status; // "SUCCESS", "FAILED" , "ARCHIVED

    @OneToOne
    @JoinColumn(name = "id_order")
    private Commande commande;

    @ManyToOne
    @JoinColumn(name = "id_client")
    private Client client;

    public Paiement() {
        this.payment_date = new Date();
    }

    public int getId_payment() { return id_payment; }
    public void setId_payment(int id_payment) { this.id_payment = id_payment; }
    public String getCard_network() { return card_network; }
    public void setCard_network(String card_network) { this.card_network = card_network; }
    public Date getPayment_date() { return payment_date; }
    public void setPayment_date(Date payment_date) { this.payment_date = payment_date; }
    public double getFinal_amount() { return final_amount; }
    public void setFinal_amount(double final_amount) { this.final_amount = final_amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Commande getCommande() { return commande; }
    public void setCommande(Commande commande) { this.commande = commande; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
}