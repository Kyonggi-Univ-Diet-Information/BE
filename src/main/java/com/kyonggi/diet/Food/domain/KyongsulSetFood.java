package com.kyonggi.diet.Food.domain;

import com.kyonggi.diet.Food.eumer.KyongsulCategory;
import com.kyonggi.diet.Food.eumer.SetType;
import com.kyonggi.diet.Food.eumer.SubRestaurant;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class KyongsulSetFood extends Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kyongsul_food_set_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private SetType setType;  // SET / COMBO

    @Enumerated(EnumType.STRING)
    private SubRestaurant subRestaurant;

    @Enumerated(EnumType.STRING)
    private KyongsulCategory category;

    @Column(name = "category_kr")
    private String categoryKorean;

    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "base_food_id",
        foreignKey = @ForeignKey(name = "fk_foodset_base_food")
    )
    private KyongsulFood baseFood;
}
