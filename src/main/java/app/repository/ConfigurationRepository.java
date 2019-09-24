package app.repository;

import app.model.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ConfigurationRepository extends JpaRepository<Configuration,Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Configuration c SET c.contestName = ?2 WHERE c.id = ?1")
    void update(Long id, String configurationName);
}
