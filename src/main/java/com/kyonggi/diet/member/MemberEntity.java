package com.kyonggi.diet.member;

import com.kyonggi.diet.review.domain.DietFoodReview;
import com.kyonggi.diet.review.domain.ESquareFoodReview;
import com.kyonggi.diet.review.domain.KyongsulFoodReview;
import com.kyonggi.diet.review.domain.SallyBoxFoodReview;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteDietFoodReview;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteESquareFoodReview;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteKyongsulFoodReview;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteSallyBoxFoodReview;
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

    @Column(nullable = true)
    private String password;

    private String name;

    private String profileUrl;

    // 애플 유저 영구 식별자 (sub)
    @Column(name = "apple_sub", unique = true)
    private String appleSub;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DietFoodReview> dietFoodReviews;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KyongsulFoodReview> kyongsulFoodReviews;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ESquareFoodReview> esquareFoodReviews;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SallyBoxFoodReview> sallyBoxFoodReviews;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoriteDietFoodReview> favoriteDietFoodReviews;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoriteKyongsulFoodReview> favoriteKyongsulFoodReviews;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoriteESquareFoodReview> favoriteESquareFoodReviews;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoriteSallyBoxFoodReview> favoriteSallyBoxFoodReviews;

}
