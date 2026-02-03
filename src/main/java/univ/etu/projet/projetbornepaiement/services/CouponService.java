package univ.etu.projet.projetbornepaiement.services;

import org.hibernate.Session;
import org.hibernate.Transaction;
import univ.etu.projet.projetbornepaiement.models.Carte;
import univ.etu.projet.projetbornepaiement.models.Coupon;
import univ.etu.projet.projetbornepaiement.utils.HibernateUtil;

import java.time.LocalDate;

public class CouponService {

    public enum ResultatCoupon {
        VALIDE, INCONNU, PERIME, DEJA_UTILISE
    }

    /**
     * Vérifie le code en BDD et l'applique au panier si valide.
     */
    public ResultatCoupon verifierEtAppliquer(String codeSaisi) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Recherche du coupon en base
            Coupon coupon = session.createQuery("FROM Coupon WHERE code = :code", Coupon.class)
                    .setParameter("code", codeSaisi)
                    .uniqueResult();

            // Vérifications
            if (coupon == null) {
                return ResultatCoupon.INCONNU;
            }

            if (!"VALIDE".equalsIgnoreCase(coupon.getStatus())) {
                return ResultatCoupon.DEJA_UTILISE;
            }

            if (coupon.getValidityDate().isBefore(LocalDate.now())) {
                return ResultatCoupon.PERIME;
            }

            // Si tout est OK : On l'applique au Panier
            // On ne change pas le statut en BDD tout de suite !
            // On le fera seulement si le paiement final est accepté.
            Carte.getInstance().applyCoupon(coupon);

            return ResultatCoupon.VALIDE;

        } catch (Exception e) {
            e.printStackTrace();
            return ResultatCoupon.INCONNU;
        }
    }

    /**
     * À appeler UNIQUEMENT après le paiement réussi pour invalider le coupon en BDD
     */
    public void consommerCoupon(int idCoupon) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Coupon c = session.get(Coupon.class, idCoupon);
            if (c != null) {
                c.setStatus("UTILISE");
                session.merge(c);
            }
            tx.commit();
        }
    }
}