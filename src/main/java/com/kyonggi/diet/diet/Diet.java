package com.kyonggi.diet.diet;

import com.kyonggi.diet.dietContent.DietContent;
import com.kyonggi.diet.dietFood.DietFood;
import com.kyonggi.diet.review.Review;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Diet {

    @Id @GeneratedValue
    @Column(name = "diet_id")
    private Long id;

    private String date;

    @Enumerated(value = EnumType.STRING)
    private DietTime time;

    @OneToMany(mappedBy = "diet")
    private List<DietContent> contents;

}
