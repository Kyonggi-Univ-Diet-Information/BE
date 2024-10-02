package com.kyonggi.diet.member;

import com.kyonggi.diet.review.domain.DietFoodReview;
import com.kyonggi.diet.review.domain.RestaurantReview;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteDietFoodReview;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteRestaurantReview;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "tbl_members")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    private String name;

    private String profileUrl;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "member")
    private List<RestaurantReview> restaurantReviews;

    @OneToMany(mappedBy = "member")
    private List<DietFoodReview> dietFoodReviews;

    @OneToMany(mappedBy = "member")
    private List<FavoriteDietFoodReview> favoriteDietFoodReviews;

    @OneToMany(mappedBy = "member")
    private List<FavoriteRestaurantReview> favoriteRestaurantReviews;
}
