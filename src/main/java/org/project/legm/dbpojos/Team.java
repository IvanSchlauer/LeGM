package org.project.legm.dbpojos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cascade;

import java.util.ArrayList;
import java.util.List;

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
public class Team {
    @Id
    @Column(name = "team_id")
    private Long teamID;

    private String name;
    private String code;
    private String city;
    private String logo;
    @Column(name = "off_rating")
    private Double offRating;
    @Column(name = "def_rating")
    private Double defRating;
    private int coachingLvl;
    private int medicalLvl;
    private int scoutingLvl;

    @OneToMany(mappedBy = "awayTeam")
    @ToString.Exclude
    private List<Game> awayGameList = new ArrayList<>();
    @OneToMany(mappedBy = "homeTeam")
    @ToString.Exclude
    private List<Game> homeGameList = new ArrayList<>();
}
