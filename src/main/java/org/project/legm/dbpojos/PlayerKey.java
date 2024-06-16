package org.project.legm.dbpojos;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 16.06.2024
 * Time: 12:20
 */
@NoArgsConstructor
@AllArgsConstructor
public class PlayerKey implements Serializable {
    private Long playerID;
    private Long userID;
}
