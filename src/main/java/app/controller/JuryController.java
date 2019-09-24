package app.controller;

import app.model.UserContact;
import app.service.impl.UserServiceImpl;
import app.utils.EncrytedPasswordUtils;
import app.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import static app.utils.WebUtils.*;


@Controller
public class JuryController {

    @Autowired
    UserServiceImpl userService;

    @Autowired
    ReloadableResourceBundleMessageSource messageSource;

    @RequestMapping(value = "/jury", method = RequestMethod.GET)
    public String juryList(Model model, Principal principal) {
        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        List<app.model.User> juries = userService.findAllUsers();
        String userInfo = WebUtils.toString(loginedUser);
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("juries", juries);
        model.addAttribute("title", messageSource.getMessage("pageTitle.jury", null, Locale.getDefault()));
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            errorMessage = null;
        }
        return "jury/jury";
    }


    @RequestMapping(value = "/jury/form")
    public String showform(Model model) {
        model.addAttribute("command", new app.model.User());
        model.addAttribute("title", messageSource.getMessage("pageTitle.jury.new", null, Locale.getDefault()));
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            errorMessage = null;
        }
        return "jury/juryForm";
    }

    @RequestMapping(value = "/jury/save", method = RequestMethod.POST)
    public String save(@ModelAttribute("user") app.model.User user, @ModelAttribute("user_contacts") UserContact contact) {
        if (userService.findUserByLogin(user.getLogin()) != null) {
            errorMessage = messageSource.getMessage("error.addUserWithExistsLogin", null, Locale.getDefault());
            return "redirect:/jury/form?lang=" + Locale.getDefault();
        } else {
            user.setEncrytedPassword(EncrytedPasswordUtils.encrytePassword(user.getEncrytedPassword()));
            user.setUserContact(contact);
            userService.save(user);
            return "redirect:/jury?lang=" + Locale.getDefault();
        }
    }

    @RequestMapping(value = "/jury/edit/{id}")
    public String edit(@PathVariable int id, Model model) {
        app.model.User user = userService.findUserById(id);

        model.addAttribute("jury", user);
        model.addAttribute("title", messageSource.getMessage("pageTitle.jury.edit", null, Locale.getDefault()));
        return "jury/juryEditForm";
    }

    @RequestMapping(value = "/jury/editsave", method = RequestMethod.POST)
    public String editsave(@ModelAttribute("jury") app.model.User user, @ModelAttribute("user_contacts") UserContact contact) {
        //Проверка: изменился ли пароль? если да, тогда перекодировать его и сохранить в базе
        if (!userService.findUserById(user.getUserId()).getEncrytedPassword().equals(user.getEncrytedPassword())) {
            user.setEncrytedPassword(EncrytedPasswordUtils.encrytePassword(user.getEncrytedPassword()));
        }
        //Проверка: изменился ли логин? Проверяем, чтобы он был уникальный в базе
        if (!userService.findUserById(user.getUserId()).getLogin().equals(user.getLogin()) && userService.findUserByLogin(user.getLogin()) != null) {
            errorMessage = messageSource.getMessage("error.addUserWithExistsLogin", null, Locale.getDefault());
            System.out.println(errorMessage);
        } else {
            user.setUserContact(contact);
            //Проверка, если изменилась роль с админа на юзера, а этот админ последний, тогда ошибка
            if (user.getRole().equals(messageSource.getMessage("option.admin", null, Locale.getDefault()))) {
                user.setRole("admin");
            } else {
                user.setRole("user");
            }
            if (userService.findUserById(user.getUserId()).getRole().equals("admin") &&
                    !user.getRole().equals("admin") &&
                    userService.findAllAdmins().size() == 1) {
                errorMessage = messageSource.getMessage("error.deleteLastAdmin", null, Locale.getDefault());
            } else {
                userService.update(user);
            }
        }
        return "redirect:/jury?lang=" + Locale.getDefault();
    }

    @RequestMapping(value = "/jury/delete/{id}", method = RequestMethod.GET)
    public String deleteJury(@PathVariable long id) {
        app.model.User user = userService.findUserById(id);
        if (user.getRole().equals("admin") && userService.findAllAdmins().size() == 1) {
            errorMessage = messageSource.getMessage("error.deleteLastAdmin", null, Locale.getDefault());
        } else {
            if (!user.getMarks().isEmpty()) {
                errorMessage = messageSource.getMessage("error.deleteJuryWithMarks", null, Locale.getDefault());
            } else {
                userService.delete(user);
            }

        }
        return "redirect:/jury?lang=" + Locale.getDefault();
    }
}
