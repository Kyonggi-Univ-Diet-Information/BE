package com.kyonggi.diet.diet;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DietRepository {

    private final EntityManager em;

    public void save(Diet diet) {
        em.persist(diet);
    }

    public Diet findOne(Long id) {
        return em.find(Diet.class, id);
    }

    public List<Diet> findAll() {
        return em.createQuery("select d from Diet d", Diet.class)
                .getResultList();
    }

    public void delete(Diet diet) {
        em.remove(diet);
    }

    public List<Diet> findDietsBetweenDates(LocalDate startOfWeek, LocalDate endOfWeek) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String start = startOfWeek.format(formatter);
        String end = endOfWeek.format(formatter);
        return em.createQuery("select d from Diet d where d.date Between :start and :end",Diet.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }
}
