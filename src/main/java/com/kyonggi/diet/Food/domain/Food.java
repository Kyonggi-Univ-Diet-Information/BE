package com.kyonggi.diet.Food.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor(force = true)
public abstract class Food {

    @Column(nullable = false)
    protected String name;

    @Column(name = "name_en")
    protected String nameEn;

    public void updateNameEn(String nameEn) {
        this.nameEn = nameEn;
    }
}
