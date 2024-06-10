package org.project.legm.dbpojos;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 14.05.2024
 * Time: 14:26
 */
@Entity
@IdClass(PlayerTeamKey.class)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PlayerTeam {
    @Id
    @Column(name = "team_id")
    private Long teamID;
    @Id
    @Column(name = "start_date")
    private LocalDate startDate;
    @Id
    @Column(name = "player_id")
    private Long playerID;

    @Column(name = "end_date", nullable = true)
    @Nullable
    private LocalDate endDate;


}
