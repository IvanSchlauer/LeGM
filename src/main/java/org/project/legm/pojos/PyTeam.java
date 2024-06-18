package org.project.legm.pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.legm.dbpojos.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 17.06.2024
 * Time: 10:22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PyTeam {
    @JsonIgnore
    private Team dbTeam;
    private String name;
    private String abbr;
    private List<PyPlayer> players = new ArrayList<>();
}
