package app.service;

import app.model.Performance;
import org.springframework.stereotype.Service;

import java.util.List;


public interface PerformanceService {
    Performance findPerformanceById(long performanceId);

    void update(Performance performance);

    void save(Performance performance);

    int findLastTurnNumber();

    void delete(Performance performance);

    List<Performance> findAllPerformances();
}
