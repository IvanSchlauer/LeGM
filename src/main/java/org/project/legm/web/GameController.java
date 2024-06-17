package org.project.legm.web;

import lombok.RequiredArgsConstructor;
import org.project.legm.bl.DBAccess;
import org.project.legm.dbpojos.Game;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    private ResponseEntity<List<Game>> getGamesByTeam
            (@RequestParam(name = "team", required = false) Long teamID,
             @RequestParam(name = "user") Long userID){
        return dbAccess.getGamesOfTeam(teamID, userID)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).build());
    }
}
