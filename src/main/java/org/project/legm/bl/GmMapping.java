package org.project.legm.bl;

import com.fasterxml.jackson.databind.JsonNode;
import org.project.legm.dbpojos.Player;
import org.project.legm.pojos.Position;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 18.06.2024
 * Time: 21:10
 */
@Component
public class GmMapping {
    private final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public Player mapPlayer(JsonNode itemNode){
        Long id = itemNode.get("player_id").asLong();
        String firstName = itemNode.get("first_name").asText();
        String lastName = itemNode.get("last_name").asText();
        String birthdate = itemNode.get("birthdate").asText();
        Long country = itemNode.get("country_id").asLong();
        Double heightFeet = itemNode.get("height").asDouble();
        Double heightInches = itemNode.get("height").asDouble();
        Double weightPounds = itemNode.get("weight").asDouble();
        String college = itemNode.get("college").asText();
        Integer defIQ = itemNode.get("defiq").asInt();
        Integer finishing = itemNode.get("finishing").asInt();
        Integer handles = itemNode.get("handles").asInt();
        Integer intangibles = itemNode.get("intangibles").asInt();
        Integer midrange = itemNode.get("midrange").asInt();
        Integer offiq = itemNode.get("offiq").asInt();
        Integer passing = itemNode.get("passing").asInt();
        Integer post = itemNode.get("post").asInt();
        Integer rebounding = itemNode.get("rebounding").asInt();
        Integer speed = itemNode.get("speed").asInt();
        Integer stamina = itemNode.get("stamina").asInt();
        Integer three_pointer = itemNode.get("three_pointer").asInt();

        if (birthdate == null || birthdate.equals("null")) {
            birthdate = "0404-04-04";
        }

        return new Player(id, null, firstName, lastName, LocalDate.parse(birthdate, DTF),
                heightFeet * 12 + heightInches, weightPounds, handles, passing,
                rebounding, three_pointer, midrange, post, finishing, speed, stamina, offiq, defIQ, intangibles,
                Position.F, college, null, null, new ArrayList<>());
    }
}
