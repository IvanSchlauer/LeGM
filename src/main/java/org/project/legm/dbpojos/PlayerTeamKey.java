package org.project.legm.dbpojos;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 21.05.2024
 * Time: 14:28
 */
@NoArgsConstructor
@AllArgsConstructor
public class PlayerTeamKey implements Serializable {
    private Long teamID;
    private Long userID;
    private LocalDate startDate;
    private Long playerID;
}
