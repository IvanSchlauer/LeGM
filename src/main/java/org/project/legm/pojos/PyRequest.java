package org.project.legm.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.legm.dbpojos.Team;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 17.06.2024
 * Time: 10:20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PyRequest {
    private PyTeam home_team;
    private PyTeam away_team;
}
