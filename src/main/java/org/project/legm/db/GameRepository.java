package org.project.legm.db;

import org.project.legm.dbpojos.Game;
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
    @Query("""
                SELECT game
                FROM Game game
                WHERE game.awayTeam.teamID = :teamID OR game.homeTeam.teamID = :teamID AND game.awayTeam.userID = :userID""")
    List<Game> getGamesByTeam(Long teamID, Long userID);

    @Query("""
                SELECT game
                FROM Game game
                WHERE game.awayTeam.userID = :userID""")
    List<Game> getGames(Long userID);

    @Query("""
                SELECT MAX(game.gameID)
                FROM Game game
                """)
    Long getNextID();
}
