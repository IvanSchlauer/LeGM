package org.project.legm.db;

import org.project.legm.dbpojos.Country;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 21.05.2024
 * Time: 14:12
 */
public interface CountryRepository extends JpaRepository<Country, Long> {
}
