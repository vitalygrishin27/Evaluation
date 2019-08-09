package app.repository;

import app.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

@Query("Select u from User u where u.login = :login")
User findUserByLogin(@Param("login") String login);

@Query("Select u from User u where u.role= 'user'")
List<User> findAllJuries();
//void addUser(User user);
}
