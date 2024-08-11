package com.kyonggi.diet.review;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReviewDTO {
    private double rating;
    private String title;
    private String content;
}
