package org.project.legm.bl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.project.legm.db.GameRepository;
import org.project.legm.db.GmUserRepository;
import org.project.legm.db.PlayerRepository;
import org.project.legm.db.TeamRepository;
import org.project.legm.dbpojos.*;
import org.project.legm.pojos.Position;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 10.04.2024
 * Time: 09:51
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class GmService {
    private final GmUserRepository gmUserRepo;
    private final TeamRepository teamRepo;
    private final PlayerRepository playerRepo;
    private final GameRepository gameRepo;
    private final WebClientConfig webClientConfig;

    private WebClient webClient;

    @PostConstruct
    public void initClient() {
        this.webClient = webClientConfig.getHighLoadWebClient();
    }

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    private final List<Team> teamList = new ArrayList<>();
    private final List<Country> countryList = new ArrayList<>();
    private final List<Player> playerList = new ArrayList<>();
    private final List<Game> gamesList = new ArrayList<>();
    private final List<GamePlayer> gamePlayerList = new ArrayList<>();
    @Getter
    private final List<PlayerTeam> playerTeamList = new ArrayList<>();

    private final String host = "api-nba-v1.p.rapidapi.com";
    private final String apiKey = "4586bc71c3mshe11dd4e50c5c982p190748jsnaa79f581dbb7";

    public List<Team> fetchTeams(GmUser gmUser) {
        log.info("Accessing Teams API Endpoint");
        List<Mono<String>> responseList = new ArrayList<>();
        String teamEastUri = "https://api-nba-v1.p.rapidapi.com/teams?conference=East";
        Mono<String> responseEastTeams = webClient.get()
                .uri(teamEastUri)
                .header("X-RapidAPI-Host", host)
                .header("X-RapidAPI-Key", apiKey)
                .retrieve()
                .bodyToMono(String.class);
        String teamWestUri = "https://api-nba-v1.p.rapidapi.com/teams?conference=West";
        Mono<String> responseWestTeams = webClient.get()
                .uri(teamWestUri)
                .header("X-RapidAPI-Host", host)
                .header("X-RapidAPI-Key", apiKey)
                .retrieve()
                .bodyToMono(String.class);
        responseList.add(responseEastTeams);
        responseList.add(responseWestTeams);
        log.info("Finished Teams API Access");

        for (Mono<String> responseTeam : responseList) {
            try {
                JsonNode rootNode = mapper.readTree(responseTeam.block());
                JsonNode responseNode = rootNode.get("response");
                if (responseNode.isArray()) {
                    for (JsonNode itemNode : responseNode) {
                        Long id = itemNode.get("id").asLong();
                        String name = itemNode.get("name").asText();
                        String code = itemNode.get("code").asText();
                        String city = itemNode.get("city").asText();
                        String logoUrl = itemNode.get("logo").asText();
                        Boolean nbaFranchise = itemNode.get("nbaFranchise").asBoolean();
                        //log.info("TeamID after: " + id);

                        Team nodeTeam = new Team(id, gmUser.getUserID(), name, code, city, logoUrl,
                                0.0, 0.0, 0, 0, 0,
                                new ArrayList<>(), new ArrayList<>());
                        //log.info("Team " + nodeTeam.getTeamID() + " :" + nodeTeam.getName());
                        if (nbaFranchise && !name.equals("Home Team Stephen A")) {
                            teamList.add(nodeTeam);
                        }
                        //log.info("Team object: " + nodeTeam);
                    }
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return teamList;
    }

    public List<Player> fetchPlayers(GmUser gmUser) {
        int rateLimit = 0;
        //log.info("All Teams: " + teamList.stream().map(Team::getTeamID).toList());
        List<Team> lTeamList = teamRepo.findAll();
        for (Team team : lTeamList) {
            //log.info("Accessing Players API with Team: " + team.getTeamID());
            while (rateLimit < 1) {
                log.info("Accessing Players API");
                String playerUri = "https://api-nba-v1.p.rapidapi.com/players?season=2023&team=";
                Mono<String> responsePlayers = webClient.get()
                        .uri(playerUri + team.getTeamID())
                        .header("X-RapidAPI-Host", host)
                        .header("X-RapidAPI-Key", apiKey)
                        .retrieve()
                        .bodyToMono(String.class);

                try {
                    JsonNode rootNode = mapper.readTree(responsePlayers.block());
                    log.info("Finished Players API Access: " + rootNode);
                    JsonNode responseNode = rootNode.get("response");
                    if (responseNode.isArray()) {
                        for (JsonNode itemNode : responseNode) {
                            Long id = itemNode.get("id").asLong();
                            String firstName = itemNode.get("firstname").asText();
                            String lastName = itemNode.get("lastname").asText();
                            String birthdate = itemNode.get("birth").get("date").asText();
                            String country = itemNode.get("birth").get("country").asText();
                            Double heightFeet = itemNode.get("height").get("feets").asDouble();
                            Double heightInches = itemNode.get("height").get("inches").asDouble();
                            Double weightPounds = itemNode.get("weight").get("pounds").asDouble();
                            String college = itemNode.get("college").asText();

                            if (birthdate == null || birthdate.equals("null")) {
                                birthdate = "0404-04-04";
                            }

                            Player nodePlayer = new Player(id, firstName, lastName, LocalDate.parse(birthdate, DTF),
                                    heightFeet * 12 + heightInches, weightPounds, 0, 0, 0,
                                    0, 0, 0, 0, 0, 0, 0, 0, 0,
                                    Position.F, college, null, null, new ArrayList<>());
                            InputStream is;


                            PlayerTeam nodePlayerTeam = new PlayerTeam(team.getTeamID(), LocalDate.of(2023, 1, 1), nodePlayer.getPlayerId(), null);
                            //log.info("nodePlayer: " + nodePlayer);
                            //log.info("nodePlayerTeam: " + nodePlayerTeam);
                            playerList.add(nodePlayer);
                            playerTeamList.add(nodePlayerTeam);
                        }
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                rateLimit++;
            }
        }
        //log.info("PlayerList: " + playerList);
        //log.info("PlayerTeamList: " + playerTeamList);
        return playerList;
    }

    public List<Game> fetchGames(GmUser gmUser) {
        log.info("Accessing Games API Endpoint");
        String gamesUri = "https://api-nba-v1.p.rapidapi.com/games?season=2023";
        Mono<String> responseTeams = webClient.get()
                .uri(gamesUri)
                .header("X-RapidAPI-Host", host)
                .header("X-RapidAPI-Key", apiKey)
                .retrieve()
                .bodyToMono(String.class);

        try {
            JsonNode rootNode = mapper.readTree(responseTeams.block());
            log.info("Finished Games API Access");
            JsonNode responseNode = rootNode.get("response");
            if (responseNode.isArray()) {
                for (JsonNode itemNode : responseNode) {
                    Long id = itemNode.get("id").asLong();
                    String date = itemNode.get("date").get("start").asText().substring(0, 10);
                    String location = itemNode.get("arena").get("name") + ", " + itemNode.get("arena").get("city");
                    Long awayTeamId = itemNode.get("teams").get("visitors").get("id").asLong();
                    Long homeTeamId = itemNode.get("teams").get("home").get("id").asLong();

                    //log.info("Away ID: " + awayTeamId);
                    //log.info("Home ID: " + homeTeamId);

                    Optional<Team> awayTeam = teamRepo.findById(new TeamKey(awayTeamId, gmUser.getUserID()));
                    Optional<Team> homeTeam = teamRepo.findById(new TeamKey(homeTeamId, gmUser.getUserID()));

                    if (awayTeam.isPresent() && homeTeam.isPresent()) {
                        Game nodeGame = new Game(id, awayTeam.get(), homeTeam.get(), LocalDate.parse(date, DTF), location, new ArrayList<>());
                        gamesList.add(nodeGame);
                    }
                }
            }
        } catch (JsonProcessingException | NoSuchElementException e) {
            throw new RuntimeException(e);
        }

        return gamesList;
    }

    public List<GamePlayer> fetchGamePlayers() {
        int rateLimit = 0;
        List<Player> lPlayerList = playerRepo.findAll();
        for (Player player : lPlayerList) {
            if (rateLimit < 6) {
                log.info("Accessing statistics endpoint for Player: " + player.getFirstName() + " " + player.getPlayerId());
                String statisticsUri = "https://api-nba-v1.p.rapidapi.com/players/statistics?season=2023&id=";
                Mono<String> responseTeams = webClient.get()
                        .uri(statisticsUri + player.getPlayerId())
                        .header("X-RapidAPI-Host", host)
                        .header("X-RapidAPI-Key", apiKey)
                        .retrieve()
                        .bodyToMono(String.class);

                try {
                    JsonNode rootNode = mapper.readTree(responseTeams.block());
                    log.info("Finished statistics API Access");
                    JsonNode responseNode = rootNode.get("response");
                    if (responseNode.isArray()) {
                        for (JsonNode itemNode : responseNode) {
                            Long gameID = itemNode.get("game").get("id").asLong();
                            Double min = itemNode.get("min").asDouble();
                            Double pts = itemNode.get("points").asDouble();
                            Double ast = itemNode.get("assists").asDouble();
                            Double oreb = itemNode.get("offReb").asDouble();
                            Double dreb = itemNode.get("defReb").asDouble();
                            Double stl = itemNode.get("steals").asDouble();
                            Double turno = itemNode.get("turnovers").asDouble();
                            Double fga = itemNode.get("fga").asDouble();
                            Double fgm = itemNode.get("fgm").asDouble();
                            Double threepa = itemNode.get("tpa").asDouble();
                            Double threepm = itemNode.get("tpm").asDouble();
                            Double fta = itemNode.get("fta").asDouble();
                            Double ftm = itemNode.get("ftm").asDouble();

                            Optional<Game> game = gameRepo.findById(gameID);

                            if (game.isPresent()) {
                                GamePlayer gp = new GamePlayer(null, min, pts, ast, oreb, dreb, stl, turno, fga, fgm,
                                        threepa, threepm, fta, ftm, player, game.get());
                                //log.info("Gameplayer: " + gp);
                                gamePlayerList.add(gp);
                            }
                        }
                    }
                    Thread.sleep(3000);
                } catch (JsonProcessingException | NoSuchElementException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

                rateLimit++;
            }
        }
        return gamePlayerList;
    }
}