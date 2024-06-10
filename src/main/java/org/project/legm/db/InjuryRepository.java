package org.project.legm.db;

import org.project.legm.dbpojos.GamePlayer;
import org.project.legm.dbpojos.Injury;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 21.05.2024
 * Time: 14:11
 */
public interface InjuryRepository extends JpaRepository<Injury, Long> {
}
