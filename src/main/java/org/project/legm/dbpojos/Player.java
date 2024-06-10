package org.project.legm.dbpojos;

import jakarta.persistence.*;
import lombok.*;
import org.project.legm.pojos.Position;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: LeGM
 * Created by: IS
 * Date: 14.05.2024
 * Time: 14:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Player {
    @Id
    @Column(name = "player_id")
    @EqualsAndHashCode.Include
    private Long playerId;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private LocalDate birthdate;
    private Double height;
    private Double weight;
    private int handles;
    private int passing;
    private int rebounding;
    @Column(name = "three_pointer")
    private int threePointer;
    @Column(name = "mid_range")
    private int midRange;
    private int post;
    private int finishing;
    private int speed;
    private int stamina;
    private int offIQ;
    private int defIQ;
    private int intangibles;
    private Position position;
    private String college;

    @ManyToOne
    @JoinColumn(name = "injury_id")
    private Injury injury;
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;
    /*@OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<PlayerTeam> playerTeamList = new ArrayList<>();*/
    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<GamePlayer> gamesPlayedList = new ArrayList<>();


}
