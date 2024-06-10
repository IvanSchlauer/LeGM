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
import java.util.stream.Collectors;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 10.04.2024
 * Time: 09:51
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final WebClientConfig webClientConfig;

    private WebClient webClient;
    private Map<String, Long> teamIdMap;
    @PostConstruct
    public void initClient(){
        this.webClient = webClientConfig.getHighLoadWebClient();
        InputStream is;
        try {
            is = new FileInputStream(new File("src/main/resources/teamid.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.teamIdMap = new BufferedReader(new InputStreamReader(is)).lines()
                .map(line-> line.split(";"))
                .collect(Collectors.toMap(
                        parts -> parts[1],
                        parts -> Long.parseLong(parts[0])
                ));
    }

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    private List<Team> teamList = new ArrayList<>();
    private List<Country> countryList = new ArrayList<>();
    private List<Player> playerList = new ArrayList<>();
    private List<Game> gamesList = new ArrayList<>();
    @Getter
    private List<PlayerTeam> playerTeamList = new ArrayList<>();

    private final String teamEastUri = "https://api-nba-v1.p.rapidapi.com/teams?conference=East";
    private final String teamWestUri = "https://api-nba-v1.p.rapidapi.com/teams?conference=West";
    private final String countryUri = "https://restcountries.com/v3.1/all";
    private final String playerUri = "https://api-nba-v1.p.rapidapi.com/players?season=2023&team=";
    private final String gamesUri = "https://api-nba-v1.p.rapidapi.com/games?season=2023";
    private final String statisticsUri = "https://api-nba-v1.p.rapidapi.com/players/statistics?season=2023&id=";

    private final String host = "api-nba-v1.p.rapidapi.com";
    private final String apiKey = "49d56d91d9msh30c88cca5bff686p16e614jsn5e9b7e5a986e";

    public List<Country> fetchCountries() {
        log.info("Accessing Countries API Endpoint");
        Mono<String> responseTeams = webClient.get()
                .uri(countryUri)
                .retrieve()
                .bodyToMono(String.class);

        try {
            JsonNode rootNode = mapper.readTree(responseTeams.block());
            log.info("Finished Countries API Access");
            JsonNode responseNode = rootNode.get("response");
            if (responseNode.isArray()) {
                for (JsonNode itemNode : responseNode) {
                    String name = itemNode.get("name").get("common").asText();
                    Country nodeCountry = new Country(null, name);
                    countryList.add(nodeCountry);
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return countryList;
    }

    public List<Team> fetchTeams() {
        log.info("Accessing Teams API Endpoint");
        List<Mono<String>> responseList = new ArrayList<>();
        Mono<String> responseEastTeams = webClient.get()
                .uri(teamEastUri)
                .header("X-RapidAPI-Host", host)
                .header("X-RapidAPI-Key", apiKey)
                .retrieve()
                .bodyToMono(String.class);
        Mono<String> responseWestTeams = webClient.get()
                .uri(teamWestUri)
                .header("X-RapidAPI-Host", host)
                .header("X-RapidAPI-Key", apiKey)
                .retrieve()
                .bodyToMono(String.class);
        responseList.add(responseEastTeams);
        responseList.add(responseWestTeams);
        log.info("Finished Teams API Access");

        for (Mono<String> responseTeam : responseList){
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

                        Team nodeTeam = new Team(id, name, code, city, logoUrl,
                                0.0, 0.0, 0, 0, 0,
                                new ArrayList<>(), new ArrayList<>());
                        log.info("Team " + nodeTeam.getTeamID() + " :" + nodeTeam.getName());
                        log.info("Nba Franchise: " + nbaFranchise);
                        if (nbaFranchise){
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

    public List<Player> fetchPlayers() {
        int rateLimit = 0;
        //log.info("All Teams: " + teamList.stream().map(Team::getTeamID).toList());
        for (Team team : teamList) {
            //log.info("Accessing Players API with Team: " + team.getTeamID());
            while (rateLimit <= 0) {
                log.info("Accessing Players API");
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

                            if ( birthdate == null || birthdate.equals("null")){
                                birthdate = "0404-04-04";
                            }

                            Player nodePlayer = new Player(id, firstName, lastName, LocalDate.parse(birthdate, DTF),
                                    heightFeet * 12 + heightInches, weightPounds, 0, 0, 0,
                                    0, 0, 0, 0, 0, 0, 0, 0, 0,
                                    Position.F, college, null, null, new ArrayList<>());
                            InputStream is;


                            PlayerTeam nodePlayerTeam = new PlayerTeam(id, LocalDate.now(), nodePlayer.getPlayerId(),null);
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

    public List<Game> fetchGames(){
        log.info("Accessing Games API Endpoint");
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

                    log.info("Away ID: " + awayTeamId);
                    log.info("Home ID: " + homeTeamId);

                    Optional<Team> awayTeam = teamRepository.findById(awayTeamId);
                    Optional<Team> homeTeam = teamRepository.findById(homeTeamId);

                    if (awayTeam.isPresent() && homeTeam.isPresent()){
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

    public List<GamePlayer> fetchGamePlayers (){
        for (Player player : playerList){
            log.info("Accessing statistics endpoint");
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
                        //TODO den scheiß oida
                    }
                }
            } catch (JsonProcessingException | NoSuchElementException e) {
                throw new RuntimeException(e);
            }
        }
    }
}