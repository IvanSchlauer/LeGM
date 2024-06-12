package org.project.legm.bl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.legm.db.*;
import org.project.legm.dbpojos.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Optional;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 21.05.2024
 * Time: 14:15
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DBAccess {
    private final CountryRepository countryRepo;
    private final GmUserRepository gmUserRepo;
    private final GamePlayerRepository gamePlayerRepo;
    private final GameRepository gameRepo;
    private final InjuryRepository injuryRepo;
    private final PlayerRepository playerRepo;
    private final PlayerTeamRepository playerTeamRepo;
    private final TeamRepository teamRepo;
    private final GmService gmService;

    public Boolean initSave(GmUser gmUser){
        //TODO: Find working Countries API
        //countryRepo.saveAll(gmService.fetchCountries());
        try {
            gmUser = gmUserRepo.save(gmUser);
            teamRepo.saveAll(gmService.fetchTeams(gmUser));
            playerRepo.saveAll(gmService.fetchPlayers(gmUser));
            playerTeamRepo.saveAll(gmService.getPlayerTeamList());
            gameRepo.saveAll(gmService.fetchGames(gmUser));
            gamePlayerRepo.saveAll(gmService.fetchGamePlayers());
        } catch (WebClientResponseException e){
            return false;
        }
        return true;
    }

    public void getTeamGamesAndGamePlayers(Team team){
        List<Game> gamesList = gameRepo.getGamesByTeam(team.getTeamID());
        gamesList.forEach(game -> {
            System.out.printf("(%s) AwayTeam: %s | HomeTeam: %s\nGame Score: %f\n",
                    game.getGameID().toString(), game.getAwayTeam().getName(), game.getHomeTeam().getName(),
                    game.getGamePlayerList().stream().map(GamePlayer::getPts).mapToDouble(Double::doubleValue).sum());

            List<GamePlayer> gamePlayerList = gamePlayerRepo.getGamePlayersByGame(game.getGameID());
            log.info("gplist: " + gamePlayerList.size());
            gamePlayerList.forEach(gamePlayer -> {
                List<Player> playerTeamList = playerTeamRepo.getPlayerByTeamAndDate(team.getTeamID(), game.getDate());
                log.info("ptlist: " + playerTeamList.size());
                if (playerTeamList.contains(gamePlayer.getPlayer())){
                    System.out.printf("%s %f, %f, %f\n", gamePlayer.getPlayer().getLastName(), gamePlayer.getPts(),
                            gamePlayer.getAst(), gamePlayer.getOreb()+gamePlayer.getDreb());
                }
            });
        });
    }

    public Optional<List<Team>> getAllTeams(){
        return Optional.ofNullable(teamRepo.getTeams()).filter(list -> !list.isEmpty());
    }

}
