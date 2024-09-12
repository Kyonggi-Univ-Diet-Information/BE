package com.kyonggi.diet.restaurant;

import com.kyonggi.diet.review.domain.RestaurantReview;
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
    private String description; //소개
    private String time; //운영시간

    //private String menu;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantReview> restaurantReviews;

    @Enumerated(value = EnumType.STRING)
    @Column(unique = true)
    private RestaurantType restaurantType;

}
