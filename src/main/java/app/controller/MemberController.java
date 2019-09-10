package app.controller;

import app.model.Category;
import app.model.Member;
import app.service.impl.CategoryServiceImpl;
import app.service.impl.MemberServiceImpl;
import app.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Locale;

import static app.utils.WebUtils.*;

@Controller
public class MemberController {
    @Autowired
    MemberServiceImpl memberService;

    @Autowired
    CategoryServiceImpl categoryService;

    @Autowired
    ReloadableResourceBundleMessageSource messageSource;

    // Members
    @RequestMapping(value = "/members", method = RequestMethod.GET)
    public String memberList(Model model) {
        List<Member> members = memberService.findAllMembers();
        model.addAttribute("members", members);
        model.addAttribute("title", messageSource.getMessage("pageTitle.members", null, Locale.getDefault()));
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            errorMessage = null;
        }
        return "member/members";
    }

    @RequestMapping(value = "/member/form")
    public String memberForm(Model model) {
        if (categoryService.findAllCategories().isEmpty()) {
            errorMessage = messageSource.getMessage("error.addMemberWithEmptyCategoryList", null, Locale.getDefault());
        } else {
            model.addAttribute("command", new app.model.User());
            model.addAttribute("categories", categoryService.findAllCategories());
            model.addAttribute("title", messageSource.getMessage("pageTitle.member.new", null, Locale.getDefault()));
        }
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            errorMessage = null;
        }
        return "member/memberForm";

    }

    @RequestMapping(value = "/member/editsave", method = RequestMethod.POST)
    public String editsaveMember(@ModelAttribute("member") Member member, @ModelAttribute("category") Category category) {
        System.out.println(member);
        Category newCategory = categoryService.findCategoryByName(category.getCategoryName());
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
        Member member = memberService.findMemberById(id);
        model.addAttribute("member", member);
        model.addAttribute("categories", categoryService.findAllCategories());
        model.addAttribute("title", messageSource.getMessage("pageTitle.member.edit", null, Locale.getDefault()));
        return "member/memberEditForm";
    }

    @RequestMapping(value = "/member/delete/{id}", method = RequestMethod.GET)
    public String deleteMember(@PathVariable long id) {
        Member member = memberService.findMemberById(id);
        if (!member.getPerformances().isEmpty()) {
            errorMessage = messageSource.getMessage("error.deleteMemberWithPerformances", null, Locale.getDefault());
        } else {
            memberService.delete(member);
        }
        return "redirect:/members?lang=" + Locale.getDefault();
    }
}
