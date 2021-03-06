package app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformancesWrapperList {
    private String nameCategory;
    private List<PerformancesWrapper> performancesWrapperList;
}
