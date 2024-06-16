package org.project.legm.db;

import org.project.legm.dbpojos.GamePlayer;
import org.project.legm.dbpojos.PlayerStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.lang.reflect.Field;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 21.05.2024
 * Time: 14:11
 */
public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {
    @Query("SELECT gp FROM game_player gp WHERE gp.game.gameID = :gameID AND gp.game.awayTeam.userID = :userID")
    public List<GamePlayer> getGamePlayersByGame(Long gameID, Long userID);

    @Query("SELECT gp\n" +
            "FROM game_player gp\n" +
            "WHERE gp.player.playerID = :playerID AND gp.player.userID = :userID")
    public List<GamePlayer> getGamePlayerByPlayer(Long playerID, Long userID);

    @Query("""
            SELECT CAST(SUM(gp.minute) / COUNT(gp.minute) AS double), CAST(SUM(gp.pts) / COUNT(gp.pts) AS double),
                CAST(SUM(gp.ast) / COUNT(gp.ast) AS double), CAST((SUM(gp.oreb) + SUM(gp.dreb)) / COUNT(gp.dreb) AS double),
                CAST(SUM(gp.oreb) / COUNT(gp.oreb) AS double), CAST(SUM(gp.dreb) / COUNT(gp.dreb) AS double),
                CAST(SUM(gp.stl) / COUNT(gp.stl) AS double), CAST(SUM(gp.turno) / COUNT(gp.turno) AS double),
                CAST(SUM(CASE WHEN gp.fga != 0 THEN gp.fgm / gp.fga ELSE 0 END) / COUNT(gp.fga) AS double),
                CAST(SUM(CASE WHEN gp.threepa != 0 THEN gp.threepm / gp.threepa ELSE 0 END) / COUNT(gp.threepa) AS double),
                CAST(SUM(CASE WHEN gp.fta != 0 THEN gp.ftm / gp.fta ELSE 0 END) / COUNT(gp.fta) AS double)
            FROM game_player gp
            WHERE gp.player.playerID = :playerID AND gp.player.userID = :userID""")
    Object getRawSeasonStats(@Param("playerID") Long playerID, @Param("userID") Long userID);

    default PlayerStatistics getSeasonStats(Long playerID, Long userID) {
        Object result = getRawSeasonStats(playerID, userID);
        return convertToPlayerStatistics(result);
    }

    private PlayerStatistics convertToPlayerStatistics(Object result) {
        List<Double> doubles = new ArrayList<>();
        if (result.getClass().isArray()) {
            var values = (Object[]) result;
            for (Object item : values) {
                if (item.getClass().equals(Double.class)) {
                    Double doubleValue = (Double) item;
                    doubles.add(doubleValue);
                }
            }
        }
        return new PlayerStatistics(
                    doubles.get(0),
                    doubles.get(1),
                    doubles.get(2),
                    doubles.get(3),
                    doubles.get(4),
                    doubles.get(5),
                    doubles.get(6),
                    doubles.get(7),
                    doubles.get(8),
                    doubles.get(9),
                    doubles.get(10)
        );
    }

}
