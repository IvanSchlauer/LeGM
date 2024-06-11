package org.project.legm.dbpojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Entity(name = "game_player")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "gameplayer_id")
    private Long gamePlayerID;

    private Double minute;
    private Double pts;
    private Double ast;
    private Double oreb;
    private Double dreb;
    private Double stl;
    private Double turno;
    private Double fga;
    private Double fgm;
    private Double threepa;
    private Double threepm;
    private Double fta;
    private Double ftm;


    @ManyToOne
    @JoinColumn(name = "player_id")
    @JsonIgnore
    private Player player;
    @ManyToOne
    @JoinColumn(name = "game_id")
    @JsonIgnore
    private Game game;
}
