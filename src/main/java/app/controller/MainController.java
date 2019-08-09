package app.controller;

import app.service.impl.UserServiceImpl;
import app.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

@Controller
public class MainController {
    @Autowired
    UserServiceImpl serviceRepository;

    @Autowired
    ReloadableResourceBundleMessageSource messageSource;

    @RequestMapping(value = {"/"},method = RequestMethod.GET)
    public String welcomePage(WebRequest webRequest, Model model){

        if(webRequest.getParameter("lang")!=null)
            Locale.setDefault(Locale.forLanguageTag(webRequest.getParameter("lang")));
        model.addAttribute("title","Welcome");
        model.addAttribute("message",messageSource.getMessage("welcome",null, Locale.getDefault()));
      return "welcomePage";
    }

    @RequestMapping(value = "/jury", method = RequestMethod.GET)
    public String adminPage(Model model, Principal principal){
        User loginedUser =(User)((Authentication)principal).getPrincipal();
        List<app.model.User> juries=serviceRepository.findAllJuries();
        String userInfo= WebUtils.toString(loginedUser);
        model.addAttribute("userInfo",userInfo);
        model.addAttribute("juries",juries);
        return "jury";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage(Model model){

        System.out.println(Locale.getDefault());
        return "loginPage";
    }


    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String userInfo(Model model, Principal principal) {

        String userName = principal.getName();

        System.out.println("User Name: " + userName);

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

        }
        return "403Page";
    }

}
