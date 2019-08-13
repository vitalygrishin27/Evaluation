package app.service;

import app.model.User;

import java.util.List;

public interface UserService {
   User findUserByLogin(String login);

   User findUserById(long id);

   void update(User user);

   void save(User user);

   void delete(User user);

   List<User> findAllUsers();

   List<User> findAllJuries();

}
