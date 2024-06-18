package org.project.legm.web;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.project.legm.bl.DBAccess;
import org.project.legm.dbpojos.Game;
import org.project.legm.dbpojos.GamePlayer;
import org.project.legm.dbpojos.Player;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 17.06.2024
 * Time: 09:09
 */
@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/games")
public class GameController {
    private final DBAccess dbAccess;
    private final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping
    private ResponseEntity<List<Game>> getGamesByTeam
            (@RequestParam(name = "team", required = false) Long teamID,
             @RequestParam(name = "user") Long userID){
        return dbAccess.getGamesOfTeam(teamID, userID)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).build());
    }

    @GetMapping("/gameplayers")
    private ResponseEntity<List<GamePlayer>> getGamePlayers
            (@RequestParam(name = "game") Long gameID,
             @RequestParam(name = "team") Long teamID,
             @RequestParam(name = "user") Long userID){
        return dbAccess.getPlayersByGame(gameID, teamID, userID)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).build());
    }

    @PostMapping("/sim")
    private ResponseEntity<Game> postSimulation
            (@RequestParam(name = "awayTeam") Long awayTeamID,
             @RequestParam(name = "homeTeam") Long homeTeamID,
             @RequestParam(name = "user") Long userID,
             @RequestParam(name = "date") String date,
             @RequestParam(name = "location") String location){
        LocalDate gameDate = LocalDate.parse(date, DTF);

        return dbAccess.simulateGame(awayTeamID, homeTeamID, userID, gameDate, location)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).build());
    }
}
