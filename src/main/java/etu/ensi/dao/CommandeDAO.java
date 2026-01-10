package etu.ensi.dao;

import etu.ensi.model.Commande;
import etu.ensi.util.HibernateUtil;
import org.hibernate.Session;

public class CommandeDAO {

    public void saveCommande(Commande commande) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            // Cascading
            session.persist(commande);
            session.getTransaction().commit();
        }
    }
    public Commande getCommandeById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Commande.class, id);
        }
    }
}