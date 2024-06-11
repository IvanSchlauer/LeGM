package org.project.legm.db;

import org.project.legm.dbpojos.GamePlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 21.05.2024
 * Time: 14:11
 */
public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {
    @Query("SELECT gp FROM game_player gp WHERE gp.game.gameID = :gameID")
    public List<GamePlayer> getGamePlayersByGame(Long gameID);

    @Query("SELECT gp\n" +
            "FROM game_player gp\n" +
            "WHERE gp.player.playerId = :playerID")
    public List<GamePlayer> getGamePlayerByPlayer(Long playerID);

    @Query("SELECT SUM(gp.pts)/COUNT(gp.pts)\n" +
            "FROM game_player gp\n" +
            "WHERE gp.player.playerId = :playerID")
    public Double getCareerAvgPts(Long playerID);
}
