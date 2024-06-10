package org.project.legm.bl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.legm.db.*;
import org.project.legm.dbpojos.*;
import org.project.legm.pojos.Position;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 21.05.2024
 * Time: 14:15
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class Database {
    private final CountryRepository countryRepo;
    private final GamePlayerRepository gamePlayerRepo;
    private final GameRepository gameRepo;
    private final InjuryRepository injuryRepo;
    private final PlayerRepository playerRepo;
    private final PlayerTeamRepository playerTeamRepo;
    private final TeamRepository teamRepo;
    private final TeamService teamService;

    @PostConstruct
    private void init(){
        //TODO: Find working Countries API
        //countryRepo.saveAll(teamService.fetchCountries());
        teamRepo.saveAll(teamService.fetchTeams());
        playerRepo.saveAll(teamService.fetchPlayers());
        log.info("PlayerTeamList: " + teamService.getPlayerTeamList());
        playerTeamRepo.saveAll(teamService.getPlayerTeamList());
        gameRepo.saveAll(teamService.fetchGames());
    }

    public void getTeamGamesAndGamePlayers(Team team){
        List<Game> gamesList = gameRepo.getGamesByTeam(team.getTeamID());
        gamesList.forEach(game -> {
            System.out.printf("(%s) AwayTeam: %s | HomeTeam: %s\nGame Score: %d\n",
                    game.getGameID().toString(), game.getAwayTeam().getName(), game.getHomeTeam().getName(),
                    game.getGamePlayerList().stream().map(GamePlayer::getPts).mapToInt(Integer::intValue).sum());

            List<GamePlayer> gamePlayerList = gamePlayerRepo.getGamePlayersByGame(game.getGameID());
            gamePlayerList.forEach(gamePlayer -> {
                List<Player> playerTeamList = playerTeamRepo.getPlayerByTeamAndDate(team.getTeamID(), game.getDate());
                if (playerTeamList.contains(gamePlayer.getPlayer())){
                    System.out.printf("%s %d, %d, %d\n", gamePlayer.getPlayer().getLastName(), gamePlayer.getPts(),
                            gamePlayer.getAst(), gamePlayer.getOreb()+gamePlayer.getDreb());
                }
            });
        });
    }
}
