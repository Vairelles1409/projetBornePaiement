package etu.ensi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_ticket;

    @Lob
    private String content;

    @OneToOne
    @JoinColumn(name = "id_payment")
    private Paiement paiement;

    public Ticket() {}

    public int getId_ticket() { return id_ticket; }
    public void setId_ticket(int id_ticket) { this.id_ticket = id_ticket; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Paiement getPaiement() { return paiement; }
    public void setPaiement(Paiement paiement) { this.paiement = paiement; }
}