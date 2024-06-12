package org.project.legm.dbpojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 12.06.2024
 * Time: 08:44
 */
@AllArgsConstructor
@NoArgsConstructor
public class TeamKey implements Serializable {
    private Long teamID;
    private Long userID;
}
