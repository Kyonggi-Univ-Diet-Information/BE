package com.kyonggi.diet.diet;

import com.kyonggi.diet.dietFood.DietFood;
import com.kyonggi.diet.review.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
public class Diet {

    @Id @GeneratedValue
    @Column(name = "diet_id")
    private Long id;
    private String date;
    private DietTime time;

    @OneToMany
    @JoinColumn(name = "diet_food_id")
    private List<DietFood> contents;

    @OneToMany
    @JoinColumn(name = "review_id")
    private List<Review> reviews;
}
