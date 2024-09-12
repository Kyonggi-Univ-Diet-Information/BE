package com.kyonggi.diet.dietContent;

import com.kyonggi.diet.diet.Diet;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietContent {

    @Id @GeneratedValue
    @Column(name = "diet_content_id")
    private Long id;

    private String date;

    @Enumerated(value = EnumType.STRING)
    private DietTime time; // [BREAKFAST, LUNCH, DINNER]

    @OneToMany(mappedBy = "dietContent")
    private List<Diet> contents;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

}
