package app.controller;

import app.model.UserContact;
import app.service.impl.UserServiceImpl;
import app.utils.EncrytedPasswordUtils;
import app.utils.WebUtils;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
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

    @RequestMapping(value = "/start", method = RequestMethod.GET)
    public String start(Model model, Principal principal){
        return "redirect:/?lang="+Locale.getDefault();
    }


    @RequestMapping(value = "/jury", method = RequestMethod.GET)
    public String adminPage(Model model, Principal principal){
        User loginedUser =(User)((Authentication)principal).getPrincipal();
        List<app.model.User> juries=serviceRepository.findAllUsers();
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

    @RequestMapping(value = "/jury/form")
    public String showform(Model model){
        model.addAttribute("command", new app.model.User());
        return "juryform";
    }

    @RequestMapping(value = "/jury/save", method = RequestMethod.POST)
    public String save(@ModelAttribute("user") app.model.User user, @ModelAttribute("user_contacts")UserContact contact){
        user.setUserContact(contact);
        serviceRepository.save(user);
        return "redirect:/jury";
    }

    @RequestMapping(value = "/jury/edit/{id}")
    public String edit(@PathVariable int id, Model model){
        app.model.User user=serviceRepository.findUserById(id);
        model.addAttribute("jury", user);
        return "/editform";
    }

    @RequestMapping(value = "/jury/editsave", method = RequestMethod.POST)
    public String editsave(@ModelAttribute("jury")app.model.User user, @ModelAttribute("user_contacts") UserContact contact){
        //Проверка: изменился ли пароль? если да, тогда перекодировать его и сохранить в базе
        if(!serviceRepository.findUserById(user.getUserId()).getEncrytedPassword().equals(user.getEncrytedPassword())){
            user.setEncrytedPassword(EncrytedPasswordUtils.encrytePassword(user.getEncrytedPassword()));
        }
        user.setUserContact(contact);
        serviceRepository.delete(serviceRepository.findUserById(user.getUserId()));
        serviceRepository.save(user);
        return "redirect:/jury";
    }

    @RequestMapping(value = "/jury/delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable long id){
        serviceRepository.delete(serviceRepository.findUserById(id));
        return "redirect:/jury";
    }
}
