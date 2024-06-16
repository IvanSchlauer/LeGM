package org.project.legm.dbpojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStatistics {
    private Double minute;
    private Double pts;
    private Double ast;
    private Double treb;
    private Double oreb;
    private Double dreb;
    private Double stl;
    private Double turno;
    private Double fgper;
    private Double threepper;
    private Double ftper;
}
