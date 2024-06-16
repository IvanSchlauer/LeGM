package org.project.legm.web;

import lombok.RequiredArgsConstructor;
import org.project.legm.bl.DBAccess;
import org.project.legm.dbpojos.Team;
import org.project.legm.dbpojos.GmUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 11.06.2024
 * Time: 14:23
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {
    private final DBAccess dbAccess;

    @GetMapping
    private ResponseEntity<List<Team>> getTeams(){
        Optional<List<Team>> teamList = dbAccess.getAllTeams();

        return teamList.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(404).build());
    }

    @PostMapping("/start")
    private ResponseEntity<GmUser> startSave(
            @RequestBody GmUser gmUser
    ){
        return dbAccess.initSave(gmUser) ? ResponseEntity.ok(gmUser) : ResponseEntity.internalServerError().build();
    }

    //TODO Games + dates of team
}
