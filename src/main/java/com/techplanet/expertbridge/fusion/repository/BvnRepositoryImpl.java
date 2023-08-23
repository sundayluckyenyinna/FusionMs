package com.techplanet.expertbridge.fusion.repository;

import com.techplanet.expertbridge.fusion.model.Bvn;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Daniel Ofoleta
 */
@Repository
@Transactional
public class BvnRepositoryImpl implements BvnRepository {

    @PersistenceContext
    EntityManager em;

    @Override
    public Bvn createBvn(Bvn oBvn) {
        try {
            em.persist(oBvn);
            em.flush();
            return oBvn;
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public Bvn getBvnDetails(String bvn) {
        TypedQuery<Bvn> query = em.createQuery("SELECT t FROM Bvn t WHERE t.bvn = :bvn", Bvn.class)
                .setParameter("bvn", bvn);
        List<Bvn> record = query.getResultList();
        if (record.isEmpty()) {
            return null;
        }
        return record.get(0);
    }
}
