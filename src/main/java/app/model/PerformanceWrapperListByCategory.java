package app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceWrapperListByCategory {
    private List<PerformancesWrapperList> performancesWrapperListByCategories;

    public void addToPWLBC(PerformancesWrapperList performancesWrapperList){
        performancesWrapperListByCategories.add(performancesWrapperList);
    }
}
