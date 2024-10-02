package com.kyonggi.diet.diet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kyonggi.diet.dietContent.DietContent;
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
public class Diet {

    @Id @GeneratedValue
    @Column(name = "diet_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_content_id")
    @JsonIgnore
    private DietContent dietContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_food_id")
    @JsonIgnore
    private DietFood dietFood;
}
