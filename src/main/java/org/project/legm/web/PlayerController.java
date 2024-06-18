package org.project.legm.web;

import lombok.RequiredArgsConstructor;
import org.project.legm.bl.DBAccess;
import org.project.legm.dbpojos.Player;
import org.project.legm.dbpojos.PlayerKey;
import org.project.legm.dbpojos.PlayerStatistics;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 11.06.2024
 * Time: 14:17
 */
@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/players")
public class PlayerController {
    private final DBAccess dbAccess;

    @GetMapping
    private ResponseEntity<List<Player>> getPlayersOfTeam
            (@RequestParam(name = "team") Long teamID, @RequestParam(name = "user") Long userID){
        return dbAccess.getPlayersOfTeam(teamID, userID)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).build());
    }

    @GetMapping("/byId")
    private ResponseEntity<Player> getPlayerByID
            (@RequestParam(name = "player") Long playerID,
             @RequestParam(name = "user") Long userID){
        return dbAccess.getPlayerById(new PlayerKey(playerID, userID))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).build());
    }

    @GetMapping("/byGP")
    private ResponseEntity<Player> getPlayerByGamePlayer
            (@RequestParam(name = "gameplayer") Long gamePlayerID){
        return dbAccess.getPlayerByGamePlayer(gamePlayerID)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).build());
    }

    @GetMapping("/stats")
    private ResponseEntity<PlayerStatistics> getPlayerStatistics
            (@RequestParam(name = "player") Long playerID,
             @RequestParam(name = "user") Long userID){
        return dbAccess.getPlayerStatistics(playerID, userID)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).build());
    }
}