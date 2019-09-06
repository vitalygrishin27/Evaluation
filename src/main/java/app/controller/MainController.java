package app.controller;

import app.model.*;
import app.service.impl.CategoryServiceImpl;
import app.service.impl.PerformanceServiceImpl;
import app.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.security.Principal;
import java.util.*;

@Controller
public class MainController {

    @Autowired
    ReloadableResourceBundleMessageSource messageSource;

    @Autowired
    PerformanceServiceImpl performanceService;

    @Autowired
    CategoryServiceImpl categoryService;

    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public String welcomePage(WebRequest webRequest, Model model) {

        if (webRequest.getParameter("lang") != null) {
            Locale.setDefault(Locale.forLanguageTag(webRequest.getParameter("lang")));
        } else {
            Locale.setDefault(Locale.forLanguageTag("ru"));
        }
        model.addAttribute("title", messageSource.getMessage("pageTitle.welcome", null, Locale.getDefault()));
        return "welcomePage";
    }

    //default page after login (WebSecurityConfig)
    @RequestMapping(value = "/start", method = RequestMethod.GET)
    public String start(Model model, Principal principal) {

        return "redirect:/?lang=" + Locale.getDefault();
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage(Model model) {
        model.addAttribute("title", messageSource.getMessage("pageTitle.login", null, Locale.getDefault()));
        return "loginPage";
    }

    //Убрать, когда будет страница для жюри
    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String userInfo(Model model, Principal principal) {
        String userName = principal.getName();
        User loginedUser = (User) ((Authentication) principal).getPrincipal();

        String userInfo = WebUtils.toString(loginedUser);
        model.addAttribute("userInfo", userInfo);

        return "main";
    }

    @RequestMapping(value = "/online", method = RequestMethod.GET)
    public String online(Model model, Principal principal) {
        PerformanceWrapperListByCategory performanceWrapperListByCategory = new PerformanceWrapperListByCategory(new ArrayList<PerformancesWrapperList>());
        for (Category category : categoryService.findAllCategories()
        ) {
            List<PerformancesWrapper> list = new ArrayList<>();
            for (Performance performance : performanceService.findPerformancesByCategory(category)
            ) {
                list.add(new PerformancesWrapper(performance.getPerformanceId(),
                        performance.getPerformanceName(),
                        performance.getMember().getLastName() + " " + performance.getMember().getName() + " " + performance.getMember().getSecondName(),
                        performance.getTurnNumber()
                ));
            }
            PerformancesWrapperList performancesWrapperList = new PerformancesWrapperList(category.getCategoryName(), list);
            performanceWrapperListByCategory.addToPWLBC(performancesWrapperList);
        }

        model.addAttribute("performancesWrapperListByCategory", performanceWrapperListByCategory);
        return "online/online";
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


    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public String accessDenied(Model model, Principal principal) {
        if (principal != null) {
            User loginedUser = (User) ((Authentication) principal).getPrincipal();
            String userInfo = WebUtils.toString(loginedUser);
            model.addAttribute("userInfo", userInfo);
            String message = "Hi " + principal.getName() //
                    + "<br> You do not have permission to access this page!";
            model.addAttribute("message", message);
            model.addAttribute("title", messageSource.getMessage("pageTitle.403", null, Locale.getDefault()));
        }
        return "403Page";
    }
}
