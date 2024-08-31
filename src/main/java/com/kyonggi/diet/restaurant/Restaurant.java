package com.kyonggi.diet.restaurant;

import com.kyonggi.diet.review.Review;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    @Id @GeneratedValue
    @Column(name = "restaurant_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    @Enumerated(value = EnumType.STRING)
    @Column(unique = true)
    private RestaurantType restaurantType;

}
