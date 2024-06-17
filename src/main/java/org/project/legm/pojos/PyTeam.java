package org.project.legm.pojos;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 17.06.2024
 * Time: 10:22
 */
public class PyTeam {
    private String name;
    private String abbr;
    private Integer wins;
    private Integer losses;
    private List<PyPlayer> players = new ArrayList<>();
}
