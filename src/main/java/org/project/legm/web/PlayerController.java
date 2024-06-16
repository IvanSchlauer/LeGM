package org.project.legm.web;

import lombok.RequiredArgsConstructor;
import org.project.legm.bl.DBAccess;
import org.project.legm.bl.GmService;
import org.project.legm.dbpojos.Player;
import org.project.legm.dbpojos.Team;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
            (@RequestParam(name = "id") Long playerID){
        return dbAccess.getPlayerById(playerID)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).build());
    }

    //TODO Season statistics endpoint
}