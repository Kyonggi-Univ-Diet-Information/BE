package com.kyonggi.diet.diet;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public interface DietRepository  extends JpaRepository<Diet, Long> {

    @Query("select d from Diet d where d.date Between :start and :end")
    public List<Diet> findDietsBetweenDates(@Param("start") String startOfWeek, @Param("end") String endOfWeek);
}
