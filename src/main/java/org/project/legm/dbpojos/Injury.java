package org.project.legm.dbpojos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 14.05.2024
 * Time: 14:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Injury {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "injury_id")
    private Long injuryID;

    private String description;
    private int length;
}