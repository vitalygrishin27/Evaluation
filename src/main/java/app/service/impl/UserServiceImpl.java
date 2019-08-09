package app.service.impl;

import app.model.User;
import app.repository.UserRepository;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository repository;

    @Override
    public void addUser(User user) {
        repository.saveAndFlush(user);
    }

    @Override
    public User findUserByLogin(String login) {
        return repository.findUserByLogin(login);
    }

    @Override
    public void deleteUserById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<User> findAllJuries() {
        return repository.findAllJuries();
    }
}
