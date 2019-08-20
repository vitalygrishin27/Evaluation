package app.service.impl;

import app.model.UserContact;
import app.repository.UserContactRepository;
import app.service.UserContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserContactServiceImpl implements UserContactService {

    @Autowired
    private UserContactRepository repository;

    @Override
    public void update(UserContact userContact) {
        repository.update(userContact.getId(), userContact.getLastName(), userContact.getFirstName(), userContact.getSecondName(), userContact.getOffice());
    }
}
