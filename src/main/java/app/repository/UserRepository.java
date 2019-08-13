package app.repository;

import app.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

@Query("Select u from User u where u.login = :login")
User findUserByLogin(@Param("login") String login);

@Query("Select u from User u where u.id = :id")
User findUserById(@Param("id") long id);

@Query("Select u from User u where u.role= 'user'")
List<User> findAllJuries();



 //   public static final String SQL_UPDATE = "UPDATE " + Item.TABLE_NAME + " SET " + Item.NAME_COLUMN + " = ?" + COMMA + Item.WAREHOUSE_ID_COLUMN + " = ?" + " WHERE " + Item.ID_COLUMN + " = ?"
//@Query("update users set login = :login, password = :password, role = :role where id = :id")
//void update(@Param("login") String login, @Param("password") String password, @Param("role") String role, @Param("id") long id);

//@Query("delete from users u where u.id= :id")
 ///   void delete(@Param("id") long id);


//void addUser(User user);

  //  @Query("insert into ")
 //   void save

}
