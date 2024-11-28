package com.kyonggi.diet.review.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "리뷰 DTO")
public class ReviewDTO {

    @Schema(description = "리뷰 아이디")
    private Long id;

    @Schema(description = "리뷰 별점")
    private double rating;

    @Schema(description = "리뷰 제목")
    private String title;

    @Schema(description = "리뷰 내용")
    private String content;

    @Schema(description = "작성자 이름")
    private String memberName;

    @Schema(description = "생성 일자")
    private String createdAt;

    @Schema(description = "수정 일자")
    private String updatedAt;
}
