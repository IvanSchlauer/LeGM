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
@Entity(name = "game_player")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "gameplayer_id")
    private Long gamePlayerID;

    @Column(name = "score_position")
    private int scorePosition;
    private int pts;
    private int ast;
    private int oreb;
    private int dreb;
    private int stl;
    private int turno;
    private int fga;
    private int fgm;
    private int threepa;
    private int threepm;
    private int fta;
    private int ftm;

    @ManyToOne
    private Player player;
    @ManyToOne
    private Game game;
}
