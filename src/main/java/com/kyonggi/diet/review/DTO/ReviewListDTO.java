package com.kyonggi.diet.review.DTO;

import com.kyonggi.diet.review.Review;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReviewListDTO {
    private Long id;
    private double rating;
    private String title;
    private String memberName;

    public ReviewListDTO(Review review) {
        this.id = review.getId();
        this.rating = review.getRating();
        this.title = review.getTitle();
        this.memberName = review.getMember().getName();
    }
}
