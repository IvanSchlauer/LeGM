package org.project.legm.dbpojos;

import jakarta.persistence.Embeddable;
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
public class PlayerTeamKey implements Serializable {
    private Long teamID;
    private LocalDate startDate;
    private Long playerID;
}
