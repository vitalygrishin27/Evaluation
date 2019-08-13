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
    public void save(User user) {
        repository.saveAndFlush(user);
    }

    @Override
    public User findUserByLogin(String login) {
        return repository.findUserByLogin(login);
    }

    @Override
    public User findUserById(long id) {
        return repository.findUserById(id);
    }

    @Override
    public void delete(User user) {
        repository.delete(user);
    }

    @Override
    public List<User> findAllJuries() {
        return repository.findAllJuries();
    }

    @Override
    public void update(User user) {
        repository.saveAndFlush(user);
    }

    @Override
    public List<User> findAllUsers() {
        return repository.findAll();
    }
}
