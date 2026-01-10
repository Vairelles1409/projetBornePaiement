package etu.ensi.dao;

import etu.ensi.model.Paiement;
import etu.ensi.util.HibernateUtil;
import org.hibernate.Session;

public class PaiementDAO {
    public void savePaiement(Paiement p) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(p);
            session.getTransaction().commit();
        }
    }
}