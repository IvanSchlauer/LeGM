package org.project.legm.dbpojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@IdClass(PlayerKey.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Player {
    @Id
    @Column(name = "player_id")
    @EqualsAndHashCode.Include
    private Long playerID;
    @Id
    @Column(name = "user_id")
    @EqualsAndHashCode.Include
    private Long userID;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private LocalDate birthdate;
    private Double height;
    private Double weight;
    private Integer handles;
    private Integer passing;
    private Integer rebounding;
    @Column(name = "three_pointer")
    private Integer threePointer;
    @Column(name = "mid_range")
    private Integer midRange;
    private Integer post;
    private Integer finishing;
    private Integer speed;
    private Integer stamina;
    private Integer offIQ;
    private Integer defIQ;
    private Integer Intangibles;
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
    @JsonIgnore
    private List<GamePlayer> gamesPlayedList = new ArrayList<>();


}
