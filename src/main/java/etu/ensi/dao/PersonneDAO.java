package etu.ensi.dao;

import etu.ensi.model.Personne;
import etu.ensi.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class PersonneDAO {

    public Personne login(String email, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Personne WHERE email = :email AND password = :password";
            Query<Personne> query = session.createQuery(hql, Personne.class);
            query.setParameter("email", email);
            query.setParameter("password", password);
            return query.uniqueResult(); // Returns the user if found, or null if wrong credentials
        }
    }

    public void save(Personne p) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(p);
            session.getTransaction().commit();
        }
    }
}