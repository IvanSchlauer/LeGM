package org.project.legm.db;

import org.project.legm.dbpojos.Game;
import org.project.legm.dbpojos.GamePlayer;
import org.project.legm.dbpojos.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 21.05.2024
 * Time: 14:11
 */
public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("SELECT game FROM Game game WHERE game.awayTeam.teamID = :teamid OR game.homeTeam.teamID = :teamid")
    List<Game> getGamesByTeam(Long teamid);
}
