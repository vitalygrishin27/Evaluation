package app.repository;

import app.model.Criterion;
import app.model.Mark;
import app.model.Performance;
import app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MarkRepository extends JpaRepository<Mark,Long> {

    @Query("Select m from Mark m where m.user = :user and m.performance = :performance")
    List<Mark> findMarkByUserAndPerformance(@Param("performance") Performance performance, @Param("user") User user);

    @Query("Select m from Mark m where m.performance = :performance")
    List<Mark> findMarksByPerformance(@Param("performance") Performance performance);

    @Query("Select m from Mark m where m.criterion = :criterion")
    List<Mark> findAllMarkByCriterion(@Param("criterion") Criterion criterion);

}
