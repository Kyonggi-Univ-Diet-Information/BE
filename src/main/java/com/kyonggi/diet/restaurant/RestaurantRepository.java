package com.kyonggi.diet.restaurant;

import com.kyonggi.diet.review.Review;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RestaurantRepository {

    private final EntityManager em;

    public void save(Restaurant restaurant) { em.persist(restaurant);}

    public Restaurant findOne(Long id) {

        return em.find(Restaurant.class, id);
    }

    public List<Restaurant> findAll() {
        return em.createQuery("select r from Restaurant r ", Restaurant.class)
                .getResultList();
    }

    public Restaurant findDormitory() {
        return em.createQuery("select r from Restaurant r where r.restaurantType = 'DORMITORY'", Restaurant.class)
                .getSingleResult();
    }

}
