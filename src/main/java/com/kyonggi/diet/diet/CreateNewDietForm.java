package com.kyonggi.diet.diet;

import com.kyonggi.diet.dietFood.DietFood;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class CreateNewDietForm {

    private Long rice;
    private List<Long> side;
    private Long soup;
    private List<Long> desert;
    private String date;
}
