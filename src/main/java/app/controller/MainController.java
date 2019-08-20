package app.controller;

import app.model.Category;
import app.model.Member;
import app.model.Performance;
import app.model.UserContact;
import app.service.impl.*;
import app.utils.EncrytedPasswordUtils;
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
import java.util.Collection;
import java.util.List;
import java.util.Locale;

@Controller
public class MainController {
    @Autowired
    UserServiceImpl userService;

    @Autowired
    MemberServiceImpl memberService;

    @Autowired
    CategoryServiceImpl categoryService;

    @Autowired
    ReloadableResourceBundleMessageSource messageSource;

    @Autowired
    PerformanceServiceImpl performanceService;

    private String errorMessage = null;

    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public String welcomePage(WebRequest webRequest, Model model) {

        if (webRequest.getParameter("lang") != null){
            Locale.setDefault(Locale.forLanguageTag(webRequest.getParameter("lang")));
        }else{
            Locale.setDefault(Locale.forLanguageTag("ru"));
        }

        model.addAttribute("title", "Welcome");
        model.addAttribute("message", messageSource.getMessage("welcome", null, Locale.getDefault()));
        return "welcomePage";
    }

    @RequestMapping(value = "/start", method = RequestMethod.GET)
    public String start(Model model, Principal principal) {
        return "redirect:/?lang=" + Locale.getDefault();
    }


    @RequestMapping(value = "/jury", method = RequestMethod.GET)
    public String juryList(Model model, Principal principal) {
        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        List<app.model.User> juries = userService.findAllUsers();
        String userInfo = WebUtils.toString(loginedUser);
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("juries", juries);
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            errorMessage = null;
        }
        return "jury";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage(Model model) {

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
    public String showform(Model model) {
        model.addAttribute("command", new app.model.User());
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            errorMessage = null;
        }
        return "juryform";
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
        return "editform";
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
            if(userService.findUserById(user.getUserId()).getRole().equals("admin") &&
                                                        !user.getRole().equals("admin") &&
                                                        userService.findAllAdmins().size()==1){
                errorMessage = messageSource.getMessage("error.deleteLastAdmin", null, Locale.getDefault());
            }else{
                userService.update(user);
            }
        }
        return "redirect:/jury?lang=" + Locale.getDefault();
    }

    @RequestMapping(value = "/jury/delete/{id}", method = RequestMethod.GET)
    public String deleteJury(@PathVariable long id) {
        app.model.User user  = userService.findUserById(id);
        if(user.getRole().equals("admin") && userService.findAllAdmins().size()==1){
            errorMessage = messageSource.getMessage("error.deleteLastAdmin", null, Locale.getDefault());
        }else{
            if(!user.getMarks().isEmpty()){
                errorMessage = messageSource.getMessage("error.deleteJuryWithMarks", null, Locale.getDefault());
            }else{
                userService.delete(user);
            }

        }
        return "redirect:/jury?lang=" + Locale.getDefault();
    }




    // Members
    @RequestMapping(value = "/members", method = RequestMethod.GET)
    public String memberList(Model model) {
        List<Member> members = memberService.findAllMembers();
        model.addAttribute("members", members);
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            errorMessage = null;
        }
        return "members";
    }

    @RequestMapping(value = "/member/form")
    public String memberForm(Model model) {
        if(categoryService.findAllCategories().isEmpty()){
            errorMessage = messageSource.getMessage("error.addMemberWithEmptyCategoryList", null, Locale.getDefault());
        }else{
            model.addAttribute("command", new app.model.User());
            model.addAttribute("categories", categoryService.findAllCategories());

        }
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            errorMessage = null;
        }
        return "memberform";

    }

    @RequestMapping(value = "/member/editsave", method = RequestMethod.POST)
    public String editsaveMember(@ModelAttribute("member") Member member, @ModelAttribute("category") Category category) {
        System.out.println(member);
        Category newCategory=categoryService.findCategoryByName(category.getCategoryName());
        member.setCategory(newCategory);
        memberService.update(member);
        return "redirect:/members?lang=" + Locale.getDefault();
    }

    @RequestMapping(value = "/member/save", method = RequestMethod.POST)
    public String saveMember(@ModelAttribute("member") Member member, @ModelAttribute("category") Category category) {
        member.setCategory(categoryService.findCategoryByName(category.getCategoryName()));
        memberService.save(member);
        return "redirect:/members?lang=" + Locale.getDefault();
    }

    @RequestMapping(value = "/member/edit/{id}")
    public String editMember(@PathVariable int id, Model model) {
        Member member =memberService.findMemberById(id);
        model.addAttribute("member", member);
        model.addAttribute("categories",categoryService.findAllCategories());
        return "memberEditform";
    }

    @RequestMapping(value = "/member/delete/{id}", method = RequestMethod.GET)
    public String deleteMember(@PathVariable long id) {
        Member member  = memberService.findMemberById(id);
        if(!member.getPerformances().isEmpty()){
            errorMessage = messageSource.getMessage("error.deleteMemberWithPerformances", null, Locale.getDefault());
        }else{
            memberService.delete(member);
        }

        return "redirect:/members?lang=" + Locale.getDefault();
    }

    @RequestMapping(value = "/performance/{id}") //memberID
    public String performances(@PathVariable int id, Model model) {
        Member member =memberService.findMemberById(id);
        model.addAttribute("performances", member.getPerformances());
        model.addAttribute("memberId",member.getId());
        return "performance/performances";
    }

    @RequestMapping(value = "/performance/edit/{id}")//Performance Id
    public String editPerformance(@PathVariable int id, Model model) {
        Performance performance =performanceService.findPerformanceById(id);
        model.addAttribute("performance", performance);
        model.addAttribute("memberId",performance.getMember().getId());
        return "performance/editform";
    }

    @RequestMapping(value = "/performance/editsave", method = RequestMethod.POST)
    public String editsave(@ModelAttribute("performance") Performance performance) {
        performanceService.update(performance);
        return "redirect:/members?lang=" + Locale.getDefault();
    }

    @RequestMapping(value = "/performance/form/{id}") //id Member - new performance
    public String performanceForm(@PathVariable int id, Model model) {
        model.addAttribute("command", new Performance());
        model.addAttribute("memberId",id);
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            errorMessage = null;
        }
        return "performance/performanceForm";

    }

    @RequestMapping(value = "/performance/save", method = RequestMethod.POST)
    public String performanceSave(@ModelAttribute("performance") Performance performance, Long memberId) {
    Member member=memberService.findMemberById(memberId);
    performance.setMember(member);
    if(performanceService.findAllPerformances().isEmpty()){
        performance.setTurnNumber(1);
    }else{
        performance.setTurnNumber(performanceService.findLastTurnNumber()+1);
    }
        performanceService.save(performance);

      // performanceService.update(performance);
        return "redirect:/performance/"+memberId+"?lang=" + Locale.getDefault();
    }

    @RequestMapping(value = "/performance/delete/{id}", method = RequestMethod.GET)
    public String deletePerformance(@PathVariable long id) {
        Performance performance  = performanceService.findPerformanceById(id);
        performanceService.delete(performance);
        return "redirect:/performance/"+performance.getMember().getId()+"?lang=" + Locale.getDefault();
    }
}
