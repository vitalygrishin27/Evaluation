package app.service;

import app.model.Mark;
import app.model.Performance;
import app.model.User;

import java.util.List;

public interface MarkService {
    void save(Mark mark);

    List<Mark> findMarkByUserAndCriterion(Performance performance, User user);
}
