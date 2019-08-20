package app.repository;

import app.model.UserContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserContactRepository extends JpaRepository<UserContact,Long> {

    @Transactional
    @Modifying
    @Query("UPDATE UserContact uc SET uc.lastName = ?2, uc.firstName = ?3, uc.secondName = ?4, uc.office = ?5 WHERE uc.id = ?1")
    void update(Long id, String lastName, String firstName, String secondName, String office);
}
