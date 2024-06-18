package org.project.legm.bl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.legm.db.*;
import org.project.legm.dbpojos.*;
import org.project.legm.pojos.PyRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.swing.text.html.Option;
import java.io.*;
import java.nio.file.Path;
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
    private final PlayerTeamRepository playerTeamRepo;
    private final GameRepository gameRepo;
    private final GamePlayerRepository gamePlayerRepo;
    private final WebClientConfig webClientConfig;

    private WebClient webClient;

    @PostConstruct
    public void initClient() {
        this.webClient = webClientConfig.getHighLoadWebClient();
    }

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final GmMapping gmMapping;

    private final List<Team> teamList = new ArrayList<>();
    @Getter
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
        InputStream is = GmService.class.getResourceAsStream("/playerRatings.json");
        InputStream is2k = GmService.class.getResourceAsStream("/2kRatings.json");
        //log.info("All Teams: " + teamList.stream().map(Team::getTeamID).toList());
        List<Team> lTeamList = teamRepo.findAll();
        try {
            JsonNode rootNode = mapper.readTree(is);
            if (rootNode.isArray()) {
                for (JsonNode itemNode : rootNode) {
                    Player nodePlayer = gmMapping.mapPlayer(itemNode);
                    nodePlayer.setUserID(gmUser.getUserID());
                    //log.info("nodePlayer: " + nodePlayer);
                    //log.info("nodePlayerTeam: " + nodePlayerTeam);
                    if (nodePlayer.getWeight() != 0 && !nodePlayer.getBirthdate().toString().equals("0404-04-04")
                            && !nodePlayer.getCollege().equals("null")){
                        playerList.add(nodePlayer);
                    }
                }
            }
            JsonNode rootNode2k = mapper.readTree(is2k);
            if (rootNode2k.isArray()){
                for (JsonNode itemNode : rootNode2k){
                    String firstName = itemNode.get("FIRST_NAME").asText();
                    String lastName = itemNode.get("LAST_NAME").asText();
                    Optional<Player> nodePlayer = playerList.stream()
                            .filter(p -> p.getFirstName().equals(firstName) && p.getLastName().equals(lastName)).findFirst();

                    if (nodePlayer.isPresent()){
                        playerList.remove(nodePlayer.get());
                        if (gmMapping.mapPlayer2k(nodePlayer.get(), itemNode).getFinishing() == null){
                            log.info("player: " + gmMapping.mapPlayer2k(nodePlayer.get(), itemNode));
                        }
                        playerList.add(gmMapping.mapPlayer2k(nodePlayer.get(), itemNode));
                    }
                }
            }

            for (Team team : lTeamList) {
                String playerUri = "https://api-nba-v1.p.rapidapi.com/players?season=2023&team=";
                Mono<String> responsePlayers = webClient.get()
                        .uri(playerUri + team.getTeamID())
                        .header("X-RapidAPI-Host", host)
                        .header("X-RapidAPI-Key", apiKey)
                        .retrieve()
                        .bodyToMono(String.class);

                JsonNode rootNodeAPI = mapper.readTree(responsePlayers.block());
                JsonNode responseNode = rootNodeAPI.get("response");
                if (responseNode.isArray()){
                    for (JsonNode itemNode : responseNode){
                        Long id = itemNode.get("id").asLong();
                        Optional<Player> resultPlayer =  playerList.stream()
                                .filter(p -> p.getPlayerID().equals(id)).findFirst();
                        if (resultPlayer.isPresent()){
                            LocalDate startDate = LocalDate.of(2023, 1, 1);
                            PlayerTeam nodePlayerTeam =
                                    new PlayerTeam(team.getTeamID(), team.getUserID(),
                                            startDate,
                                            resultPlayer.get().getPlayerID(), null);
                            if (playerTeamList.stream().
                                    filter(p -> p.getPlayerID().equals(resultPlayer.get().getPlayerID()) && p.getStartDate().equals(startDate)).findFirst().isEmpty()){
                                playerTeamList.add(nodePlayerTeam);
                            }
                        }
                    }
                }
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

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
                log.info("Accessing statistics endpoint for Player: " + player.getFirstName() + " " + player.getPlayerID());
                String statisticsUri = "https://api-nba-v1.p.rapidapi.com/players/statistics?season=2023&id=";
                Mono<String> responseTeams = webClient.get()
                        .uri(statisticsUri + player.getPlayerID())
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
                                Team team = playerTeamRepo.getTeamPlayedForByDate(player.getPlayerID(), game.get().getDate());
                                GamePlayer gp = new GamePlayer(null, player.getLastName(),min, pts, ast, oreb, dreb, stl, turno, fga, fgm,
                                        threepa, threepm, fta, ftm, player, team, game.get());
                                //log.info("Gameplayer: " + gp);
                                gamePlayerList.add(gp);
                            }
                        }
                    }
                } catch (JsonProcessingException | NoSuchElementException e) {
                    throw new RuntimeException(e);
                }

                rateLimit++;
            }
        }
        return gamePlayerList;
    }

    public Game fetchSimulation(PyRequest requestBody, Long userID, LocalDate gameDate, String location){
        List<GamePlayer> gamePlayerList = new ArrayList<>();
        String statisticsUri = "http://127.0.0.1:5000/predict";
        Mono<String> responseSim = webClient.post()
                .uri(statisticsUri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(String.class);

        Game game;
        try {
            JsonNode rootNode = mapper.readTree(responseSim.block());
            log.info("Finished statistics API Access");
            game = new Game(gameRepo.getNextID()+1L, requestBody.getAway_team().getDbTeam(), requestBody.getHome_team().getDbTeam(),
                    gameDate, location, new ArrayList<>());
            log.info("Game: " + game);
            game = gameRepo.save(game);
            if (rootNode.get("away_team").get("stats").isArray()){
                for (JsonNode statNode : rootNode.get("away_team").get("stats")){
                    Long playerID = statNode.get("id").asLong();
                    Double minute = statNode.get("minutes_played").asDouble();
                    Double pts = statNode.get("points").asDouble();
                    Double ast = statNode.get("assists").asDouble();
                    Double oreb = statNode.get("off_reb").asDouble();
                    Double dreb = statNode.get("def_reb").asDouble();
                    Double stl = statNode.get("steals").asDouble();
                    Double turno = statNode.get("turnovers").asDouble();
                    Double fga = statNode.get("fga").asDouble();
                    Double fgm = statNode.get("fgm").asDouble();
                    Double threepa = statNode.get("3pt_att").asDouble();
                    Double threepm = statNode.get("3pt_made").asDouble();
                    Double fta = statNode.get("fta").asDouble();
                    Double ftm = statNode.get("ftm").asDouble();


                    Optional<Player> nodePlayer = playerRepo.findById(new PlayerKey(playerID, userID));

                    if (nodePlayer.isPresent()){
                        Team team = playerTeamRepo.getTeamPlayedForByDate(
                                nodePlayer.get().getPlayerID(), game.getDate());
                        GamePlayer nodeGamePlayer = new GamePlayer(null, nodePlayer.get().getLastName(),minute, pts, ast, oreb, dreb, stl,
                                turno, fga, fgm, threepa, threepm, fta, ftm, nodePlayer.get(), team, game);
                        gamePlayerList.add(nodeGamePlayer);
                    }
                }
            }

            if (rootNode.get("home_team").get("stats").isArray()){
                for (JsonNode statNode : rootNode.get("home_team").get("stats")){
                    Long playerID = statNode.get("id").asLong();
                    Double minute = statNode.get("minutes_played").asDouble();
                    Double pts = statNode.get("points").asDouble();
                    Double ast = statNode.get("assists").asDouble();
                    Double oreb = statNode.get("off_reb").asDouble();
                    Double dreb = statNode.get("def_reb").asDouble();
                    Double stl = statNode.get("steals").asDouble();
                    Double turno = statNode.get("turnovers").asDouble();
                    Double fga = statNode.get("fga").asDouble();
                    Double fgm = statNode.get("fgm").asDouble();
                    Double threepa = statNode.get("3pt_att").asDouble();
                    Double threepm = statNode.get("3pt_made").asDouble();
                    Double fta = statNode.get("fta").asDouble();
                    Double ftm = statNode.get("ftm").asDouble();


                    Optional<Player> nodePlayer = playerRepo.findById(new PlayerKey(playerID, userID));

                    if (nodePlayer.isPresent()){
                        Team team = playerTeamRepo.getTeamPlayedForByDate(
                                nodePlayer.get().getPlayerID(), game.getDate());
                        GamePlayer nodeGamePlayer = new GamePlayer(null, nodePlayer.get().getLastName(),minute, pts, ast, oreb, dreb, stl,
                                turno, fga, fgm, threepa, threepm, fta, ftm, nodePlayer.get(), team, game);
                        gamePlayerList.add(nodeGamePlayer);
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        gamePlayerRepo.saveAll(gamePlayerList);
        return game;
    }
}