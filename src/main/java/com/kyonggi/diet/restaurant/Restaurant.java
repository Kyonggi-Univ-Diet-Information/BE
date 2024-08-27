package com.kyonggi.diet.restaurant;

import com.kyonggi.diet.review.Review;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
public class Restaurant {

    @Id @GeneratedValue
    @Column(name = "restaurant_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "restaurant")
    private List<Review> reviews;

    @Enumerated(value = EnumType.STRING)
    private RestaurantType restaurantType;

}
