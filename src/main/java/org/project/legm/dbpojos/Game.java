package org.project.legm.dbpojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Column(name = "game_id")
    private Long gameID;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "home_team_id", referencedColumnName = "team_id"),
            @JoinColumn(name = "home_team_user_id", referencedColumnName = "user_id")
    })
    private Team awayTeam;
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "away_team_id", referencedColumnName = "team_id"),
            @JoinColumn(name = "away_team_user_id", referencedColumnName = "user_id")
    })
    private Team homeTeam;

    private LocalDate date;
    private String location;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    @ToString.Exclude
    @JsonIgnore
    private List<GamePlayer> gamePlayerList = new ArrayList<>();
}
