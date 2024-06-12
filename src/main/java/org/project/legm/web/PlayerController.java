package org.project.legm.web;

import lombok.RequiredArgsConstructor;
import org.project.legm.bl.DBAccess;
import org.project.legm.bl.GmService;
import org.project.legm.dbpojos.Player;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            (@RequestParam(name = "team") Long teamID){
        Optional<List<Player>> playerList = dbAccess.getPlayersOfTeam(teamID);
        return dbAccess.getPlayersOfTeam(teamID)
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
}
