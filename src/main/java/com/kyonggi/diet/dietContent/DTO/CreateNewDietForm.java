package com.kyonggi.diet.dietContent.DTO;

import lombok.Data;

import java.util.List;

@Data
public class CreateNewDietForm {

    private Long rice;
    private List<Long> side;
    private Long soup;
    private List<Long> desert;
    private String date;
}
