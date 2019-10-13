package app;

import app.service.PerformanceService;
import app.service.impl.PerformanceServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
               SpringApplication.run(Application.class,args);
               PerformanceServiceImpl.setCURRENT_ID_PERFORMANCE_IN_EVALUATION(-1);
    }


    //Меню https://html5book.ru/gorizontalnoe-menu/
}
