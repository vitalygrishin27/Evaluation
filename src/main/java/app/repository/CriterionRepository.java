package app.repository;

import app.model.Criterion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CriterionRepository extends JpaRepository<Criterion,Long> {

    @Query("Select c from Criterion c where c.name = :name")
    Criterion findCriterionByName(@Param("name") String name);

    @Query("Select c from Criterion c where c.id = :id")
   Criterion findCriterionById(@Param("id") Long id);
}
