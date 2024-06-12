package org.project.legm.db;

import org.project.legm.dbpojos.GamePlayer;
import org.project.legm.dbpojos.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 21.05.2024
 * Time: 14:12
 */
public interface PlayerRepository extends JpaRepository<Player, Long> {
    @Query("SELECT p\n" +
            "FROM PlayerTeam pt INNER JOIN Player p ON pt.playerID = p.playerId\n" +
            "WHERE pt.endDate IS NULL AND pt.teamID = :teamID")
    public List<Player> getPlayersOfTeam(Long teamID);
}
