package app.controller;

import app.model.*;
import app.service.PerformanceService;
import app.service.UserService;
import app.service.impl.CategoryServiceImpl;
import app.service.impl.ConfigurationServiceImpl;
import app.service.impl.MarkServiceImpl;
import app.service.impl.PerformanceServiceImpl;
import app.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

@Controller
public class MainController {

    @Autowired
    ReloadableResourceBundleMessageSource messageSource;

    @Autowired
    ConfigurationServiceImpl configurationService;

    @Autowired
    MarkServiceImpl markService;

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
        try {
            User loginedUser = (User) ((Authentication) principal).getPrincipal();
            SimpleGrantedAuthority simpleGrantedAuthority = (SimpleGrantedAuthority) loginedUser.getAuthorities().toArray()[0];
            if (simpleGrantedAuthority.getAuthority().equals("user")) {
                return "redirect:/evaluation?lang=" + Locale.getDefault();
            }else {
                return "redirect:/?lang=" + Locale.getDefault();
            }
        } catch (Exception e){
            System.out.println("Error with principal");
            return "redirect:/?lang=" + Locale.getDefault();
        }
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


    @RequestMapping(value = "/configuration", method = RequestMethod.GET)
    public String configure(Model model) {
        model.addAttribute("title", messageSource.getMessage("pageTitle.configuration", null, Locale.getDefault()));
        model.addAttribute("configuration",configurationService.getConfiguration());
        return "configuration/configuration";
    }

    @RequestMapping(value = "/configuration", method = RequestMethod.POST)
    public String configureSet(@ModelAttribute("configuration") Configuration configuration) {
        System.out.println(configuration.getContestName());
        configurationService.update(configuration);
        return "redirect:/?lang=" + Locale.getDefault();
    }

    @RequestMapping(value = "/configuration/deleteAllMarks", method = RequestMethod.GET)
    public String deleteAllMarks() {
       markService.deleteAllMarks();

        return "redirect:/?lang=" + Locale.getDefault();
    }


}
