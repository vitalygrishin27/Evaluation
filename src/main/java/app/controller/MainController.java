package app.controller;

import app.model.Performance;
import app.model.PerformancesWrapper;
import app.model.PerformancesWrapperList;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
public class MainController {

    @Autowired
    ReloadableResourceBundleMessageSource messageSource;

    @Autowired
    PerformanceServiceImpl performanceService;

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

    //Убрать, когда будет страница для жюри
    @RequestMapping(value = "/online", method = RequestMethod.GET)
    public String online(Model model, Principal principal) {
        List<PerformancesWrapper> list=new ArrayList<>();
        for (Performance performance:performanceService.findAllPerformances()
             ) {
           list.add(new PerformancesWrapper(performance.getPerformanceId(),
                    performance.getPerformanceName(),
                    performance.getMember().getLastName()+" "+performance.getMember().getName()+" "+performance.getMember().getSecondName(),
                    performance.getMember().getCategory().getCategoryName(),
                    performance.getTurnNumber()
                    ));
        }
        model.addAttribute("performancesWrapperList", new PerformancesWrapperList(list));
        return "online/online";
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
