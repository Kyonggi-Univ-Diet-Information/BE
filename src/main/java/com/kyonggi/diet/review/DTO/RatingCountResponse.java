package com.kyonggi.diet.review.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingCountResponse {
    private Map<Integer, Long> result;
}
