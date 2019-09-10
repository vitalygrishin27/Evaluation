package app.controller;

import app.model.*;
import app.service.impl.CriterionServiceImpl;
import app.service.impl.MarkServiceImpl;
import app.service.impl.PerformanceServiceImpl;
import app.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static app.utils.WebUtils.*;


@Controller
public class EvaluationController {

    @Autowired
    PerformanceServiceImpl performanceService;

    @Autowired
    CriterionServiceImpl criterionService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    MarkServiceImpl markService;

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "/evaluation", method = RequestMethod.GET)
    public String getCurrentPerformance(Model model) {
        if (PerformanceServiceImpl.getCURRENT_ID_PERFORMANCE_IN_EVALUATION() != 0) {
            Performance performance = performanceService.findPerformanceById(PerformanceServiceImpl.getCURRENT_ID_PERFORMANCE_IN_EVALUATION());
            List<Criterion> criteria = performance.getMember().getCategory().getCriterions();
            List<Evaluate> evaluates = new ArrayList<>();
            for (Criterion element : criteria
            ) {
                Evaluate evaluate = new Evaluate(element.getId(), 0);
                evaluates.add(evaluate);
            }
            EvaluateWrapper evaluateWrapper=new EvaluateWrapper(evaluates);
            if (errorMessage != null) {
                model.addAttribute("errorMessage", errorMessage);
                errorMessage = null;
            }

            model.addAttribute("performance", performance);
            model.addAttribute("evaluateWrapper",evaluateWrapper);
        }
        return "evaluation/evaluation";

    }
    @RequestMapping(value = "/evaluation/evaluate", method = RequestMethod.POST)
    public String setMarks(@ModelAttribute("evaluateWrapper") EvaluateWrapper evaluateWrapper, Model model, Principal principal, long performanceId) {

        Performance performance = performanceService.findPerformanceById(performanceId);
        User user = userService.findUserByLogin(principal.getName());

        List<Mark> alreadyEvaluated = markService.findMarkByUserAndCriterion(performance, user);

        if (alreadyEvaluated.isEmpty()) {
            for (Evaluate evaluate : evaluateWrapper.getEvaluateList()
            ) {
                Mark mark = new Mark();
                mark.setValue(evaluate.getValue());
                mark.setUser(userService.findUserByLogin(principal.getName()));
                mark.setPerformance(performanceService.findPerformanceById(performanceId));
                mark.setCriterion(criterionService.findCriterionById(evaluate.getId()));
                markService.save(mark);
            }
        }else{
            errorMessage = messageSource.getMessage("error.marksAlreadyExists", null, Locale.getDefault());
        }





        return "redirect:/evaluation?lang=" + Locale.getDefault();

    }
}
