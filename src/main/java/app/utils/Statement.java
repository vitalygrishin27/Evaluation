package app.utils;

import app.service.impl.ConfigurationServiceImpl;
import app.service.impl.POIServiceImpl;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class Statement implements Runnable{
    @Autowired
    ConfigurationServiceImpl configurationService;

    @Autowired
    POIServiceImpl poiService;

    @Getter
    private boolean processIsAlreadyRun;

    @Setter
    @Getter
    private boolean needToCreateNewOne=true;
    @Transactional
    @Override
    public void run() {
        if(!processIsAlreadyRun){
            processIsAlreadyRun=true;
            poiService.createNewDocument(configurationService.getConfiguration().getContestName());
            processIsAlreadyRun=false;
        }

    }
}
