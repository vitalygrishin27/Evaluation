package app.service.impl;

import app.model.Category;
import app.model.Criterion;
import app.repository.CriterionRepository;
import app.service.CriterionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CriterionServiceImpl implements CriterionService {

    @Autowired
    private CriterionRepository repository;

    @Autowired
    private CategoryServiceImpl categoryService;

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

    @Override
    public List<Category> findAllCategoryWithCriterion(Criterion criterion) {
        List<Category> result=new ArrayList<>();
        for (Category category:categoryService.findAllCategories()
             ) {
            if(category.getCriterions().contains(criterion)) result.add(category);
        }
        return result;
    }
}
