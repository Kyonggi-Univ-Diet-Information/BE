package com.kyonggi.diet.dietFood;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class DietFood {

    @Id @GeneratedValue
    @Column(name = "diet_food_id")
    private Long id;
    private String name;
}
