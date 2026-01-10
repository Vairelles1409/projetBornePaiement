package etu.ensi.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "commandes")
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_order;

    @Temporal(TemporalType.TIMESTAMP)
    private Date order_date;

    private double total_price;

    @ManyToOne
    @JoinColumn(name = "id_client")
    private Client client;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    private List<Ligne_commande> plat_list = new ArrayList<>();

    public Commande() {
        this.order_date = new Date();
        this.total_price = 0.0;
    }


    // This links the line to the order correctly for Hibernate
    public void addPlat(Plat plat, int quantity) {
        Ligne_commande ligne = new Ligne_commande(plat, quantity);
        ligne.setCommande(this);
        this.plat_list.add(ligne);
        this.total_price += (plat.getUnit_price() * quantity);
    }

    public int getId_order() { return id_order; }
    public void setId_order(int id_order) { this.id_order = id_order; }
    public Date getOrder_date() { return order_date; }
    public void setOrder_date(Date order_date) { this.order_date = order_date; }
    public double getTotal_price() { return total_price; }
    public void setTotal_price(double total_price) { this.total_price = total_price; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public List<Ligne_commande> getPlat_list() { return plat_list; }
    public void setPlat_list(List<Ligne_commande> plat_list) { this.plat_list = plat_list; }
}