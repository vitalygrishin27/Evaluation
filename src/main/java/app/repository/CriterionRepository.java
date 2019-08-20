package app.repository;

import app.model.Criterion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CriterionRepository extends JpaRepository<Criterion,Long> {

    @Query("Select c from Criterion c where c.name = :name")
    Criterion findCriterionByName(@Param("name") String name);

    @Query("Select c from Criterion c where c.id = :id")
   Criterion findCriterionById(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Criterion c SET c.name = ?2 WHERE c.id = ?1")
    void update(Long id, String name);
}
