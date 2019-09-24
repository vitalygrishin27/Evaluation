package app.service;

import app.model.Configuration;

public interface ConfigurationService {
    Configuration getConfiguration();

    void update(Configuration configuration);
}
