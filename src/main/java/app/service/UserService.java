package app.service;

import app.model.User;

import java.util.List;

public interface UserService {
   User findUserByLogin(String login);

   void addUser(User user);

   void deleteUserById(Long id);

   List<User> findAllJuries();

}
