package app.service.impl;

import app.model.Criterion;
import app.repository.CriterionRepository;
import app.service.CriterionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CriterionServiceImpl implements CriterionService {

    @Autowired
    private CriterionRepository repository;

    @Override
    public List<Criterion> findAllCriterions() {
        return repository.findAll();
    }

    @Override
    public Criterion findCriterionById(Long id) {
        return repository.findCriterionById(id);
    }

    @Override
    public Criterion findCriterionByName(String name) {
        return repository.findCriterionByName(name);
    }

    @Override
    public void save(Criterion criterion) {
        repository.saveAndFlush(criterion);
    }

    @Override
    public void delete(Criterion criterion) {
        repository.delete(criterion);
    }

    @Override
    public void update(Criterion criterion) {
        repository.update(criterion.getId(),criterion.getName());
    }
}
