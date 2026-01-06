package univ.etu.projet.projetbornepaiement.models;

import java.util.HashMap;
import java.util.Map;

public class Carte {
    // Instance unique (Singleton)
    private static Carte instance;

    // Stockage : Produit -> Quantité
    private final Map<Produit, Integer> items = new HashMap<>();

    private Carte() {}

    public static Carte getInstance() {
        if (instance == null) {
            instance = new Carte();
        }
        return instance;
    }

    public void addProduct(Produit product) {
        // Si le produit existe déjà, on augmente la quantité (+1), sinon on le met à 1
        items.merge(product, 1, Integer::sum);
    }

    public void removeProduct(Produit product) {
        if (items.containsKey(product)) {
            int currentQty = items.get(product);
            if (currentQty > 1) {
                items.put(product, currentQty - 1);
            } else {
                items.remove(product);
            }
        }
    }

    public void clear() {
        items.clear();
    }

    public Map<Produit, Integer> getItems() {
        return items;
    }

    public double getTotal() {
        return items.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
    }

    public void deleteProduct(Produit product) {
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