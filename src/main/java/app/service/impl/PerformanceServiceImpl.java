package app.service.impl;

import app.model.Performance;
import app.repository.PerformanceRepository;
import app.service.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PerformanceServiceImpl implements PerformanceService {

    @Autowired
    PerformanceRepository repository;

    @Override
    public Performance findPerformanceById(long performanceId) {
       return repository.findPerformanceById(performanceId);
    }

    @Override
    public void update(Performance performance) {
        repository.update(performance.getPerformanceId(),performance.getPerformanceName());
    }

    @Override
    public void save(Performance performance) {
        repository.saveAndFlush(performance);
    }

    @Override
    public int findLastTurnNumber() {
        return repository.findLastTurnNumber();
    }

    @Override
    public void delete(Performance performance) {
        repository.delete(performance);
    }

    @Override
    public List<Performance> findAllPerformances() {
        return repository.findAll();
    }
}
