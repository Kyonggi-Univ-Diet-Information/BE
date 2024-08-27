package com.kyonggi.diet.review.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReviewDTO {
    private double rating;
    private String title;
    private String content;
    private String memberName;
}
