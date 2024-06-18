package org.project.legm.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 17.06.2024
 * Time: 10:20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PyPlayer {
    private Long id;
    private String name;
    private Integer offRating;
    private Integer defRating;
}
