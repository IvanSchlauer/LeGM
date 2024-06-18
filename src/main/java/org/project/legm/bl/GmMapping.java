package org.project.legm.bl;

import com.fasterxml.jackson.databind.JsonNode;
import org.project.legm.dbpojos.Player;
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
        Double heightFeet = itemNode.get("height").asDouble();
        Double heightInches = itemNode.get("height").asDouble();
        Double weightPounds = itemNode.get("weight").asDouble();
        String college = itemNode.get("college").asText();

        if (birthdate.equals("null")){
            birthdate = "0404-04-04";
        }

        return new Player(id, null, firstName, lastName, LocalDate.parse(birthdate, DTF),
                heightFeet * 12 + heightInches, weightPounds, null, null,
                null, null, null, null, null, null, null, null, null, null,
                null, college, null, null, null, null, new ArrayList<>());
    }

    public Player mapPlayer2k(Player player, JsonNode itemNode){
        String image = itemNode.get("IMAGE_URL").asText();
        Integer midRange = itemNode.get("MIDRANGESHOT").asInt();
        Integer threePointer = itemNode.get("THREEPOINTSHOT").asInt();
        Integer offIq = itemNode.get("OFFENSIVECONSISTENCY").asInt();
        Integer finishing = itemNode.get("LAYUP").asInt();
        Integer post = itemNode.get("POSTCONTROL").asInt();
        Integer defIq = itemNode.get("DEFENSIVECONSISTENCY").asInt();
        Integer speed = itemNode.get("SPEED").asInt();
        Integer stamina = itemNode.get("STAMINA").asInt();
        Integer intangibles = itemNode.get("HUSTLE").asInt();
        Integer handles = itemNode.get("BALLHANDLE").asInt();
        Integer passing = itemNode.get("PASSIQ").asInt();
        Integer ofReb = itemNode.get("OFFENSIVEREBOUND").asInt();
        Integer defReb = itemNode.get("DEFENSIVEREBOUND").asInt();
        String jersey = itemNode.get("JERSEY_NUM").asText();
        String position = itemNode.get("POSITION").asText();
        String country = itemNode.get("COUNTRY").asText();

        Integer rebounding = (ofReb+defReb)/2;

        player.setImage(image);
        player.setMidRange(midRange);
        player.setThreePointer(threePointer);
        player.setOffIQ(offIq);
        player.setFinishing(finishing);
        player.setPost(post);
        player.setDefIQ(defIq);
        player.setSpeed(speed);
        player.setStamina(stamina);
        player.setIntangibles(intangibles);
        player.setHandles(handles);
        player.setPassing(passing);
        player.setRebounding(rebounding);
        player.setJersey(jersey);
        player.setPosition(position);
        player.setCountry(country);

        return player;
    }
}
