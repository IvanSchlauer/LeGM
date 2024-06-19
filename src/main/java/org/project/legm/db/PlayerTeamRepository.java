package org.project.legm.db;

import org.project.legm.dbpojos.*;
import org.springframework.cglib.core.Local;
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
    @Query("""
            SELECT p
            FROM PlayerTeam pt INNER JOIN Player p ON pt.playerID = p.playerID
            WHERE pt.endDate IS NULL AND pt.teamID = :teamID AND pt.userID = :userID""")
    List<Player> getActivePlayerByTeam(Long teamID, Long userID);

    @Query("""
            SELECT pt \
            FROM PlayerTeam pt INNER JOIN Player p ON pt.playerID = p.playerID
            WHERE pt.teamID = :teamID AND pt.userID = :userID""")
    List<Player> getPlayerByTeam(Long teamID, Long userID);

    @Query("""
            SELECT p
            FROM PlayerTeam pt INNER JOIN Player p ON pt.playerID=p.playerID
                               INNER JOIN Team t ON pt.teamID = t.teamID
            WHERE pt.startDate <= :date AND pt.endDate >= :date OR pt.endDate = NULL AND pt.teamID = :teamID AND t.userID = :userID""")
    List<Player> getPlayerByTeamAndDate(Long teamID, LocalDate date, Long userID);

    @Query("""
            SELECT DISTINCT(pt.teamID)
            FROM PlayerTeam pt INNER JOIN Team t ON pt.teamID=t.teamID
            WHERE pt.playerID = :playerID AND t.userID = :userID""")
    List<Team> getTeamsPlayedFor(Long playerID, Long userID);

    @Query("""
            SELECT t
            FROM PlayerTeam pt INNER JOIN Team t ON pt.teamID = t.teamID
            WHERE pt.playerID = :playerID AND pt.startDate <= :date AND pt.endDate IS NULL OR pt.endDate >= :date 
                  AND pt.userID = :userID
            """)
    Team getTeamPlayedForByDate(Long playerID, LocalDate date, Long userID);

    @Query("""
            SELECT pt
            FROM PlayerTeam pt
            WHERE pt.playerID = :playerID AND pt.startDate = :date
            """)
    Team getTeamOnJoinDate(Long playerID, LocalDate date);
}
