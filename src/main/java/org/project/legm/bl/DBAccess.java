package org.project.legm.bl;

import jakarta.annotation.PostConstruct;
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
}
