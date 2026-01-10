package etu.ensi.util;

import etu.ensi.model.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/*This is to avoid repeating the connection code in every single DAO*/
public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Personne.class)
                    .addAnnotatedClass(Administrateur.class)
                    .addAnnotatedClass(Cuisinier.class)
                    .addAnnotatedClass(Client.class)
                    .addAnnotatedClass(Plat.class)
                    .addAnnotatedClass(Commande.class)
                    .addAnnotatedClass(Ligne_commande.class)
                    .addAnnotatedClass(Paiement.class)
                    .addAnnotatedClass(Coupon_reduction.class)
                    .addAnnotatedClass(Ticket.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}