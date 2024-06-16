package org.project.legm.db;

import org.project.legm.dbpojos.Team;
import org.project.legm.dbpojos.TeamKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 21.05.2024
 * Time: 14:12
 */
public interface TeamRepository extends JpaRepository<Team, TeamKey> {
    @Query("""
            SELECT team \
            FROM Team team LEFT JOIN team.awayGameList \
                           LEFT JOIN team.homeGameList
            WHERE team.userID = :userID""")
    List<Team> getTeams(Long userID);
}
