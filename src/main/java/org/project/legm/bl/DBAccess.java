package org.project.legm.bl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.legm.db.*;
import org.project.legm.dbpojos.*;
import org.project.legm.pojos.PyPlayer;
import org.project.legm.pojos.PyRequest;
import org.project.legm.pojos.PyTeam;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.ArrayList;
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
        try {
            //TODO: Find working Countries API
            //countryRepo.saveAll(gmService.fetchCountries());
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

    public Optional<List<Team>> getAllTeams(Long userID){
        return Optional.ofNullable(teamRepo.getTeams(userID)).filter(list -> !list.isEmpty());
    }

    public Optional<Player> getPlayerById(PlayerKey playerKey){
        return playerRepo.findById(playerKey);
    }

    public Optional<List<Player>> getPlayersOfTeam(Long teamID, Long userID){
        return Optional.ofNullable(playerRepo.getPlayersOfTeam(teamID, userID)).filter(list -> !list.isEmpty());
    }

    public Optional<PlayerStatistics> getPlayerStatistics(Long playerID, Long userID){
        return Optional.ofNullable(gamePlayerRepo.getSeasonStats(playerID, userID));
    }

    public Optional<List<Game>> getGamesOfTeam(Long teamID, Long userID){
        return Optional.ofNullable(teamID != null ? gameRepo.getGamesByTeam(teamID, userID) : gameRepo.getGames(userID))
                .filter(list -> !list.isEmpty());
    }

    public Optional<Player> getPlayerByGamePlayer(Long gamePlayerID){
        return Optional.ofNullable(playerRepo.getPlayerByGamePlayer(gamePlayerID));
    }

    public Optional<Game> simulateGame(Long awayTeamID, Long homeTeamID, Long userID, LocalDate gameDate, String location){
        List<Player> awayTeamPlayers = playerTeamRepo.getActivePlayerByTeam(awayTeamID, userID);
        List<Player> homeTeamPlayers = playerTeamRepo.getActivePlayerByTeam(homeTeamID, userID);

        Optional<Team> awayTeam = teamRepo.findById(new TeamKey(awayTeamID, userID));
        Optional<Team> homeTeam = teamRepo.findById(new TeamKey(homeTeamID, userID));

        if (awayTeam.isPresent() && homeTeam.isPresent() && !awayTeamPlayers.isEmpty() && !homeTeamPlayers.isEmpty()){
            List<PyPlayer> awayPyPlayers = new ArrayList<>();
            List<PyPlayer> homePyPlayers = new ArrayList<>();

            awayTeamPlayers.forEach(p -> {
                awayPyPlayers.add(
                        new PyPlayer(p.getPlayerID() ,p.getFirstName()+p.getLastName(), getOffRating(p), getDefRating(p)));
            });
            homeTeamPlayers.forEach(p -> {
                homePyPlayers.add(
                        new PyPlayer(p.getPlayerID() ,p.getFirstName()+p.getLastName(), getOffRating(p), getDefRating(p)));
            });

            PyRequest requestBody = new PyRequest(
                    new PyTeam(homeTeam.get() ,homeTeam.get().getName(), homeTeam.get().getCode(), homePyPlayers),
                    new PyTeam(homeTeam.get() ,awayTeam.get().getName(), awayTeam.get().getCode(), awayPyPlayers));

            Game game = gmService.fetchSimulation(requestBody, userID, gameDate, location);


            return Optional.ofNullable(game);
        }

        log.info("empty optional");
        return Optional.empty();
    }

    public Integer getOffRating(Player p){
        return p.getFinishing()+p.getOffIQ()+p.getHandles()+p.getMidRange()+p.getPassing()+p.getPost()
                +p.getPassing()+p.getThreePointer();
    }

    public Integer getDefRating(Player p){
        return p.getDefIQ()+p.getIntangibles()+p.getRebounding();
    }

    public Optional<List<GamePlayer>> getPlayersByGame(Long gameID, Long teamID, Long userID){
        return Optional.ofNullable(gamePlayerRepo.getGamePlayersByGame(gameID, teamID, userID)).filter(list -> !list.isEmpty());
    }
}
