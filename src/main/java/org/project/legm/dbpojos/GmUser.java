package org.project.legm.dbpojos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 12.06.2024
 * Time: 08:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GmUser {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "user_id")
    private Long userID;

    private String email;
    private String password;
}
