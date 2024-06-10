package org.project.legm.dbpojos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 14.05.2024
 * Time: 14:26
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "game_id")
    private Long gameID;

    @ManyToOne
    @JoinColumn(name = "away_team")
    private Team awayTeam;
    @ManyToOne
    @JoinColumn(name = "home_team")
    private Team homeTeam;

    private LocalDate date;
    private String location;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<GamePlayer> gamePlayerList = new ArrayList<>();
}
