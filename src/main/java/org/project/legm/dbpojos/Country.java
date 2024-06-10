package org.project.legm.dbpojos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 14.05.2024
 * Time: 14:27
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "country_id")
    private Long countryID;

    private String name;
}
