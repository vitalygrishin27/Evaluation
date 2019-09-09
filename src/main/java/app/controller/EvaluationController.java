package app.controller;

import app.model.Performance;
import app.service.impl.PerformanceServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;


@Controller
public class EvaluationController {

    @Autowired
    PerformanceServiceImpl performanceService;

    @RequestMapping(value = "/evaluation", method = RequestMethod.GET)
    public String getCurrentPerformance(Model model) {
        if (PerformanceServiceImpl.getCURRENT_ID_PERFORMANCE_IN_EVALUATION() != 0) {
            model.addAttribute("performance", performanceService.findPerformanceById(PerformanceServiceImpl.getCURRENT_ID_PERFORMANCE_IN_EVALUATION()));
        }
        return "evaluation/evaluation";

    }
    @RequestMapping(value = "/evaluation", method = RequestMethod.POST)
    public String setMarks(Model model, Principal principal) {
        System.out.println("ewfewfw");
        return "evaluation/evaluation";

    }
}
