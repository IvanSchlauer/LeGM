package org.project.legm.db;

import org.project.legm.dbpojos.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 21.05.2024
 * Time: 14:12
 */
public interface PlayerTeamRepository extends JpaRepository<PlayerTeam, PlayerTeamKey> {
    @Query("SELECT pt " +
            "FROM PlayerTeam pt " +
            "WHERE pt.endDate = NULL AND pt.teamID = :teamID")
    public List<GamePlayer> getActivePlayerByTeam(Long teamID);

    @Query("SELECT pt " +
            "FROM PlayerTeam pt " +
            "WHERE pt.teamID = :teamID")
    public List<GamePlayer> getPlayerByTeam(Long teamID);

    @Query("""
            SELECT p
            FROM PlayerTeam pt INNER JOIN Player p ON pt.playerID=p.playerId
                               INNER JOIN Team t ON pt.teamID = t.teamID
            WHERE pt.startDate <= :date AND pt.endDate >= :date OR pt.endDate = NULL AND pt.teamID = :teamID AND t.userID = :userID""")
    public List<Player> getPlayerByTeamAndDate(Long teamID, LocalDate date, Long userID);

    @Query("""
            SELECT DISTINCT(pt.teamID)
            FROM PlayerTeam pt INNER JOIN Team t ON pt.teamID=t.teamID
            WHERE pt.playerID = :playerID AND t.userID = :userID""")
    public List<Team> getTeamsPlayedFor(Long playerID, Long userID);


}
