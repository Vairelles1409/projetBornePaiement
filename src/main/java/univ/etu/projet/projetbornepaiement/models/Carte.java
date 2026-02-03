package univ.etu.projet.projetbornepaiement.models;

import java.util.HashMap;
import java.util.Map;

public class Carte {
    // Instance unique (Singleton)
    private static Carte instance;

    // Stockage : Produit -> Quantité
    private final Map<Plat, Integer> items = new HashMap<>();
    // Gestion de la réduction
    private double discountAmount = 0.0;
    private Coupon appliedCoupon = null; // Pour pouvoir le marquer comme utilisé plus tard

    private Carte() {}

    public static Carte getInstance() {
        if (instance == null) {
            instance = new Carte();
        }
        return instance;
    }

    public void addProduct(Plat product) {
        // Si le produit existe déjà, on augmente la quantité (+1), sinon on le met à 1
        items.merge(product, 1, Integer::sum);
    }

    public void removeProduct(Plat product) {
        if (items.containsKey(product)) {
            int currentQty = items.get(product);
            if (currentQty > 1) {
                items.put(product, currentQty - 1);
            } else {
                items.remove(product);
            }
        }
    }


    /*public void clear() {
        items.clear();
    }*/
    public void clear() {
        items.clear();
        discountAmount = 0.0;
        appliedCoupon = null;
    }

    public Map<Plat, Integer> getItems() {
        return items;
    }

    /*public double getTotal() {
        return items.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
    }*/
    public double getTotal() {
        double subTotal = items.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();

        double finalTotal = subTotal - discountAmount;
        return Math.max(finalTotal, 0); // On ne peut pas avoir un total négatif
    }

    public void applyCoupon(Coupon coupon) {
        this.appliedCoupon = coupon;
        this.discountAmount = coupon.getValeur();
    }

    public Coupon getAppliedCoupon() { return appliedCoupon; }
    public double getDiscountAmount() { return discountAmount; }


        public void deleteProduct(Plat product) {
        items.remove(product);
    }

    private boolean aEmporter = false; // false = Sur Place, true = A Emporter

    public void setTakeAway(boolean takeAway) {
        this.aEmporter = takeAway;
    }

    public boolean isTakeAway() {
        return aEmporter;
    }

}