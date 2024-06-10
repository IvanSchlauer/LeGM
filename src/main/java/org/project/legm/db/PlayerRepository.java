package org.project.legm.db;

import org.project.legm.dbpojos.GamePlayer;
import org.project.legm.dbpojos.Player;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 21.05.2024
 * Time: 14:12
 */
public interface PlayerRepository extends JpaRepository<Player, Long> {
}
