package app.repository;

import app.model.Category;
import app.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    @Query("Select m from Member m where m.id = :id")
    Member findMemberById(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Member m SET m.lastName = ?2, m.name = ?3, m.secondName = ?4, m.office = ?5, m.boss = ?6, m.category = ?7 WHERE m.id = ?1")
    void update(Long id, String lastName, String name, String secondName, String office, String boss, Category category);
}
