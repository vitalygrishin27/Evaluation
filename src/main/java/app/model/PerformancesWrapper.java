package app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformancesWrapper {

    private long idPerformance;
    private String namePerformance;
    private String nameMember;
    private int turnNumber;
    private Map<User,Integer> marks;
}
