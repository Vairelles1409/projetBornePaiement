package etu.ensi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "plats")
public class Plat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_plat;

    @Column(nullable = false)
    private String name;

    private int qty_stock;

    private double unit_price;

    //Empty Constructor (réquis par Hibernate)
    public Plat() {
    }

    //Constructor
    public Plat(String name, int qty_stock, double unit_price) {
        this.name = name;
        this.qty_stock = qty_stock;
        this.unit_price = unit_price;
    }

    public int getId_plat() { return id_plat; }
    public void setId_plat(int id_plat) { this.id_plat = id_plat; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQty_stock() { return qty_stock; }
    public void setQty_stock(int qty_stock) { this.qty_stock = qty_stock; }

    public double getUnit_price() { return unit_price; }
    public void setUnit_price(double unit_price) { this.unit_price = unit_price; }
}