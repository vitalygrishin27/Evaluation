package app.controller;

import app.model.*;
import app.service.impl.CriterionServiceImpl;
import app.service.impl.MarkServiceImpl;
import app.service.impl.PerformanceServiceImpl;
import app.service.impl.UserServiceImpl;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        if (PerformanceServiceImpl.getCURRENT_ID_PERFORMANCE_IN_EVALUATION() != -1) {
            Performance performance = performanceService.findPerformanceById(PerformanceServiceImpl.getCURRENT_ID_PERFORMANCE_IN_EVALUATION());
            List<Criterion> criteria = performance.getMember().getCategory().getCriterions();
            List<Evaluate> evaluates = new ArrayList<>();
            for (Criterion element : criteria
            ) {
                Evaluate evaluate = new Evaluate(element.getId(), element.getName(), 0);
                evaluates.add(evaluate);
            }
            EvaluateWrapper evaluateWrapper = new EvaluateWrapper(evaluates);
            if (errorMessage != null) {
                model.addAttribute("errorMessage", errorMessage);
                errorMessage = null;
            }
            model.addAttribute("title", messageSource.getMessage("pageTitle.evaluation", null, Locale.getDefault()));
            model.addAttribute("performance", performance);
            model.addAttribute("evaluateWrapper", evaluateWrapper);
        }else{
            model.addAttribute("performance",new Performance());
        }
        return "evaluation/evaluation";

    }

    @RequestMapping(value = "/evaluation/fragment", method = RequestMethod.GET)
    public String getCurrentPerformanceFragment(Model model) {
        if (PerformanceServiceImpl.getCURRENT_ID_PERFORMANCE_IN_EVALUATION() != -1) {
            Performance performance = performanceService.findPerformanceById(PerformanceServiceImpl.getCURRENT_ID_PERFORMANCE_IN_EVALUATION());
            List<Criterion> criteria = performance.getMember().getCategory().getCriterions();
            List<Evaluate> evaluates = new ArrayList<>();
            for (Criterion element : criteria
            ) {
                Evaluate evaluate = new Evaluate(element.getId(), element.getName(), 0);
                evaluates.add(evaluate);
            }
            EvaluateWrapper evaluateWrapper = new EvaluateWrapper(evaluates);
            if (errorMessage != null) {
                model.addAttribute("errorMessage", errorMessage);
                errorMessage = null;
            }
            model.addAttribute("title", messageSource.getMessage("pageTitle.evaluation", null, Locale.getDefault()));
            model.addAttribute("performance", performance);
            model.addAttribute("evaluateWrapper", evaluateWrapper);
        }else{
            model.addAttribute("performance",new Performance());
        }
        return "evaluation/evaluationFragment :: updateFragment";

    }


    @RequestMapping(value = "/evaluation/evaluate", method = RequestMethod.POST)
    public void setMarks(@ModelAttribute("evaluateWrapper") EvaluateWrapper evaluateWrapper, Model model, Principal principal, long performanceId) {

        Performance performance = performanceService.findPerformanceById(performanceId);
        User user = userService.findUserByLogin(principal.getName());

        List<Mark> alreadyEvaluated = markService.findMarkByJuryAndPerformance(performance, user);

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
        } else {
            errorMessage = messageSource.getMessage("error.marksAlreadyExists", null, Locale.getDefault());
        }
       // return "evaluation/evaluation";

    }


    @RequestMapping(value = "/evaluation/isPerformanceNew", method = RequestMethod.POST)
    public void isPerformanceNew(Principal principal, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject jsonObjectResponse = new JSONObject();
        if (PerformanceServiceImpl.getCURRENT_ID_PERFORMANCE_IN_EVALUATION() == -1) {
            jsonObjectResponse.put("isNeedLogo", true);
            jsonObjectResponse.put("needToLoadNewPerformance", false);
        } else {
            org.springframework.security.core.userdetails.User loginedUser = (org.springframework.security.core.userdetails.User) ((Authentication) principal).getPrincipal();
            User currentUser = userService.findUserByLogin(loginedUser.getUsername());
            List<Mark> alreadyEvaluated = markService.findMarkByJuryAndPerformance(performanceService.findPerformanceById(PerformanceServiceImpl.getCURRENT_ID_PERFORMANCE_IN_EVALUATION()), currentUser);
            Integer performanceIDInitilizer = req.getParameter("performanceId") == null || req.getParameter("performanceId").equals("")  ? -1 : Integer.valueOf(req.getParameter("performanceId"));
            if (alreadyEvaluated.isEmpty() && PerformanceServiceImpl.getCURRENT_ID_PERFORMANCE_IN_EVALUATION() != performanceIDInitilizer) {
                jsonObjectResponse.put("isNeedLogo", false);
                jsonObjectResponse.put("needToLoadNewPerformance", true);
            } else if (alreadyEvaluated.isEmpty()){
                jsonObjectResponse.put("isNeedLogo", false);
                jsonObjectResponse.put("needToLoadNewPerformance", false);
            }else {
                jsonObjectResponse.put("isNeedLogo", true);
                jsonObjectResponse.put("needToLoadNewPerformance", false);
            }
        }

        resp.getWriter().write(String.valueOf(jsonObjectResponse));
        resp.flushBuffer();
    }

}


// public String start(Model model, Principal principal) {
//        try {
//            User loginedUser = (User) ((Authentication) principal).getPrincipal();
//            SimpleGrantedAuthority simpleGrantedAuthority = (SimpleGrantedAuthority) loginedUser.getAuthorities().toArray()[0];
//            if (simpleGrantedAuthority.getAuthority().equals("user")) {
//                return "redirect:/evaluation?lang=" + Locale.getDefault();
//            }else {
//                return "redirect:/?lang=" + Locale.getDefault();