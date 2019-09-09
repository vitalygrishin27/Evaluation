package app.controller;

import app.model.*;
import app.service.UserService;
import app.service.impl.PerformanceServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

@Controller
public class OnlineController {

    @Autowired
    PerformanceServiceImpl performanceService;

    @Autowired
    UserService userService;


    @RequestMapping(value = "/online", method = RequestMethod.GET)
    public String online(Model model, Principal principal) {
        PerformanceWrapperListByCategory performanceWrapperListByCategory = new PerformanceWrapperListByCategory(new ArrayList<PerformancesWrapperList>());
        List<Performance> sortedListPerformanceByTurnNumber=performanceService.findAllPerformances();
        List<Category> sortedListCategory = new LinkedList<>();
        for (Performance perfomance: sortedListPerformanceByTurnNumber
        ) {
            if(!sortedListCategory.contains(perfomance.getMember().getCategory())){
                sortedListCategory.add(perfomance.getMember().getCategory());
            }
        }

        for (Category category : sortedListCategory
        ) {
            List<PerformancesWrapper> list = new ArrayList<>();
            for (Performance performance : performanceService.findPerformancesByCategory(category)
            ) {
                list.add(new PerformancesWrapper(performance.getPerformanceId(),
                        performance.getPerformanceName(),
                        performance.getMember().getLastName() + " " + performance.getMember().getName() + " " + performance.getMember().getSecondName(),
                        performance.getTurnNumber(),getSummaryMarks(performance)
                ));
            }
            PerformancesWrapperList performancesWrapperList = new PerformancesWrapperList(category.getCategoryName(), list);
            performanceWrapperListByCategory.addToPWLBC(performancesWrapperList);
        }

        model.addAttribute("performancesWrapperListByCategory", performanceWrapperListByCategory);
        return "online/online";
    }

    private Map<User, Integer> getSummaryMarks(Performance performance) {
        Map<app.model.User, Integer> result = new HashMap<>();
        for (app.model.User user : userService.findAllUsers()
        ) {
            result.put(user, 0);
        }

        for (Mark mark : performance.getMarks()
        ) {
            result.put(mark.getUser(), result.get(mark.getUser()) + mark.getValue());
        }

        return result;
    }

    @RequestMapping(value = "/online/send", method = RequestMethod.POST)
    public void onlineSend(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PerformanceServiceImpl.setCURRENT_ID_PERFORMANCE_IN_EVALUATION(Integer.valueOf(req.getParameter("performanceID")));
    }

    @RequestMapping(value = "/online/saveTurnNumber", method = RequestMethod.POST)
    public String saveTurnNumber(String json) {
        List<String> splitList = Arrays.asList(json.split("\\s*,\\s*"));
        List<Integer> queue = new LinkedList<>();
        for (String element : splitList
        ) {
            try {
                queue.add(Integer.valueOf(element));
            } finally {
                continue;
            }
        }
        int turn = 1;
        for (int element : queue
        ) {
            Performance performance = performanceService.findPerformanceById(element);
            performance.setTurnNumber(turn++);
            performanceService.update(performance);
        }
        System.out.println(json);
        return "redirect:/online?lang=" + Locale.getDefault();
    }

}
