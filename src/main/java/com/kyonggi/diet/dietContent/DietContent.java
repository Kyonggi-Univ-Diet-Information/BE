package com.kyonggi.diet.dietContent;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietContent {

    @Id @GeneratedValue
    @Column(name = "diet_id")
    private Long id;

    private String date;

    @Enumerated(value = EnumType.STRING)
    private DietTime time;

    @OneToMany(mappedBy = "dietContent")
    private List<com.kyonggi.diet.diet.Diet> contents;

}
