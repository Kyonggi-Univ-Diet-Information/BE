package com.kyonggi.diet.diet;

import com.kyonggi.diet.dietFood.DietFood;
import com.kyonggi.diet.review.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Diet {

    @Id @GeneratedValue
    @Column(name = "diet_id")
    private Long id;

    private String date;

    @Enumerated(value = EnumType.STRING)
    private DietTime time;

    @OneToMany
    private List<DietFood> contents;


    @OneToMany
    @JoinColumn(name = "review_id")
    private List<Review> reviews;

    public Diet createDiet(String date, DietTime time, List<DietFood> contents) {
        Diet diet = new Diet();
        diet.setDate(date);
        diet.setTime(time);
        diet.setContents(contents);
        return diet;
    }
}
