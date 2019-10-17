package app.controller;

import app.model.Mark;
import app.model.Member;
import app.model.Performance;
import app.service.impl.MarkServiceImpl;
import app.service.impl.MemberServiceImpl;
import app.service.impl.PerformanceServiceImpl;
import app.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Locale;

import static app.utils.WebUtils.*;

@Controller
public class PerformanceController {

    @Autowired
    ReloadableResourceBundleMessageSource messageSource;

    @Autowired
    PerformanceServiceImpl performanceService;

    @Autowired
    MemberServiceImpl memberService;

    @Autowired
    MarkServiceImpl markService;

    @RequestMapping(value = "/performance/{id}") //memberID
    public String performances(@PathVariable int id, Model model) {
        Member member = memberService.findMemberById(id);
        model.addAttribute("performances", member.getPerformances());
        model.addAttribute("memberId", member.getId());
        model.addAttribute("title", messageSource.getMessage("pageTitle.performance", null, Locale.getDefault()));
        return "performance/performances";
    }

    @RequestMapping(value = "/performance/edit/{id}")//Performance Id
    public String editPerformance(@PathVariable int id, Model model) {
        Performance performance = performanceService.findPerformanceById(id);
        model.addAttribute("performance", performance);
        model.addAttribute("memberId", performance.getMember().getId());
        model.addAttribute("title", messageSource.getMessage("pageTitle.performance.edit", null, Locale.getDefault()));
        return "performance/performanceEditForm";
    }

    @RequestMapping(value = "/performance/editsave", method = RequestMethod.POST)
    public String editsave(@ModelAttribute("performance") Performance performance) {
        performanceService.update(performance);
        return "redirect:/members?lang=" + Locale.getDefault();
    }

    @RequestMapping(value = "/performance/form/{id}") //id Member - new performance
    public String performanceForm(@PathVariable int id, Model model) {
        model.addAttribute("command", new Performance());
        model.addAttribute("memberId", id);
        model.addAttribute("title", messageSource.getMessage("pageTitle.performance.new", null, Locale.getDefault()));
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            errorMessage = null;
        }
        return "performance/performanceForm";

    }

    @RequestMapping(value = "/performance/save", method = RequestMethod.POST)
    public String performanceSave(@ModelAttribute("performance") Performance performance, Long memberId) {
        Member member = memberService.findMemberById(memberId);
        performance.setMember(member);
        if (performanceService.findAllPerformances().isEmpty()) {
            performance.setTurnNumber(1);
        } else {
            performance.setTurnNumber(performanceService.findLastTurnNumber() + 1);
        }
        performanceService.save(performance);
        return "redirect:/performance/" + memberId + "?lang=" + Locale.getDefault();
    }

    @RequestMapping(value = "/performance/delete/{id}", method = RequestMethod.GET)
    public String deletePerformance(@PathVariable long id) {
        Performance performance = performanceService.findPerformanceById(id);
        for (Mark mark: markService.findMarksByPerformance(performance)
             ) {
            markService.deleteMark(mark);
        }
        performanceService.delete(performance);
        return "redirect:/performance/" + performance.getMember().getId() + "?lang=" + Locale.getDefault();
    }
}
