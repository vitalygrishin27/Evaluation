package app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformancesWrapper {

    private long idPerformance;
    private String namePerformance;
    private String nameMember;
    private String nameCategory;
    private int turnNumber;

}
