package com.kyonggi.diet.diet.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class CreateNewDietForm {

    private Long rice;
    private List<Long> side;
    private Long soup;
    private List<Long> desert;
    private String date;
}
