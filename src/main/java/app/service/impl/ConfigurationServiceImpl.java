package app.service.impl;

import app.model.Configuration;
import app.repository.ConfigurationRepository;
import app.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

    @Autowired
    ConfigurationRepository repository;

    @Override
    public Configuration getConfiguration() {
     return repository.findAll().get(0);
    }

    @Override
    public void update(Configuration configuration) {
        repository.update(configuration.getId(),configuration.getContestName(),configuration.getIsSortable());
    }
}
