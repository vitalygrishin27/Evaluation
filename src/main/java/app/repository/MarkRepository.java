package app.repository;

import app.model.Mark;
import app.model.Performance;
import app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MarkRepository extends JpaRepository<Mark,Long> {

    @Query("Select m from Mark m where m.user = :user and m.performance = :performance")
    List<Mark> findMarkByUserAndCriterion(@Param("performance") Performance performance, @Param("user") User user);
}
