package com.kyonggi.diet.dietContent;

import com.kyonggi.diet.diet.Diet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DietContentRepository extends JpaRepository<DietContent, Long> {

    @Query("select d from DietContent d where d.date Between :start and :end")
    public List<DietContent> findDietsBetweenDates(@Param("start") String startOfWeek, @Param("end") String endOfWeek);
}
