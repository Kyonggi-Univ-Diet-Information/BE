package com.kyonggi.diet.dietContent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kyonggi.diet.diet.Diet;
import com.kyonggi.diet.dietFood.DietFood;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietContent {

    @Id @GeneratedValue
    @Column(name = "diet_content_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_id")
    @JsonIgnore
    private Diet diet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_food_id")
    @JsonIgnore
    private DietFood dietFood;
}
