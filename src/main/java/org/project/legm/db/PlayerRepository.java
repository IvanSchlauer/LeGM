package org.project.legm.db;

import org.project.legm.dbpojos.GamePlayer;
import org.project.legm.dbpojos.Player;
import org.project.legm.dbpojos.PlayerKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 21.05.2024
 * Time: 14:12
 */
public interface PlayerRepository extends JpaRepository<Player, PlayerKey> {
    @Query("""
            SELECT p
            FROM PlayerTeam pt INNER JOIN Player p ON pt.playerID = p.playerID INNER JOIN Team t ON pt.teamID = :teamID
            WHERE pt.endDate IS NULL AND t.userID = :userID""")
    public List<Player> getPlayersOfTeam(Long teamID, Long userID);
}
