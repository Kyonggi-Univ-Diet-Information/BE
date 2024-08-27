package com.kyonggi.diet.dietFood;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DietFoodRepository {

    private final EntityManager em;

    public void save(DietFood dietFood) {
        em.persist(dietFood);
    }

    public DietFood findOne(Long id) {
        return em.find(DietFood.class, id);
    }

    public List<DietFood> findAll() {
        return em.createQuery("select d from DietFood d", DietFood.class)
                .getResultList();
    }

    public List<DietFood> findRice() {
        return em.createQuery("select d from DietFood d where d.dietFoodType = :dietFoodType", DietFood.class)
                .setParameter("dietFoodType", DietFoodType.RICE)
                .getResultList();
    }

    public List<DietFood> findSide() {
        return em.createQuery("select d from DietFood d where d.dietFoodType = :dietFoodType", DietFood.class)
                .setParameter("dietFoodType", DietFoodType.SIDE)
                .getResultList();
    }

    public List<DietFood> findSoup() {
        return em.createQuery("select d from DietFood d where d.dietFoodType = :dietFoodType", DietFood.class)
                .setParameter("dietFoodType", DietFoodType.SOUP)
                .getResultList();
    }

    public List<DietFood> findDesert() {
        return em.createQuery("select d from DietFood d where d.dietFoodType = :dietFoodType", DietFood.class)
                .setParameter("dietFoodType", DietFoodType.DESERT)
                .getResultList();
    }



}
