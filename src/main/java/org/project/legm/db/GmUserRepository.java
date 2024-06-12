package org.project.legm.db;

import org.project.legm.dbpojos.GmUser;
import org.project.legm.dbpojos.Injury;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 12.06.2024
 * Time: 10:12
 */
public interface GmUserRepository extends JpaRepository<GmUser, Long> {
}
