package app.repository;

import app.model.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PerformanceRepository extends JpaRepository<Performance,Long> {
    @Query("Select p from Performance p where p.performanceId = :performanceId")
    Performance findPerformanceById(@Param("performanceId") Long performanceId);

    @Transactional
    @Modifying
    @Query("UPDATE Performance p SET p.performanceName = ?2 WHERE p.performanceId = ?1")
    void update(Long performanceId, String performanceName);


    @Query("select MAX(p.performanceId) from Performance p")
    int findLastTurnNumber();
}
