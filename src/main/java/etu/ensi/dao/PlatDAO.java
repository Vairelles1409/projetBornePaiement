package etu.ensi.dao;

import etu.ensi.model.Plat;
import etu.ensi.util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;
import java.util.ArrayList;

public class PlatDAO {

    public void savePlat(Plat plat) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(plat);
            session.getTransaction().commit();
        }
    }

    public List<Plat> getAllPlats() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Plat", Plat.class).list();
        }
    }

    public void updateStock(int idPlat, int newQty) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Plat p = session.get(Plat.class, idPlat);
            if (p != null) p.setQty_stock(newQty);
            session.getTransaction().commit();
        }
    }
}