package com.kyonggi.diet.review.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "음식 이름들 DTO")
public class FoodNamesDTO {

    @Schema(description = "음식 아이디")
    private Long id;

    @Schema(description = "음식 한국 이름")
    private String name;

    @Schema(description = "음식 영어 이름")
    private String nameEn;
}
