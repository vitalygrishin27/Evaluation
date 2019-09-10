package app.service.impl;

import app.model.Mark;
import app.model.Performance;
import app.model.User;
import app.repository.MarkRepository;
import app.service.MarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarkServiceImpl implements MarkService {

    @Autowired
    private MarkRepository repository;

    @Override
    public void save(Mark mark) {
        repository.saveAndFlush(mark);
    }

    @Override
    public List<Mark> findMarkByUserAndCriterion(Performance performance, User user) {
        return repository.findMarkByUserAndCriterion(performance,user);
    }
}
