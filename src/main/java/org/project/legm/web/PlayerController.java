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

    private ResponseEntity<List<Player>> getPlayersOfTeam(){
        return null;
    }
}
