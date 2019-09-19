package app.controller;

import app.model.Criterion;
import app.model.Mark;
import app.service.impl.CriterionServiceImpl;
import app.service.impl.MarkServiceImpl;
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
public class CriterionController {

    @Autowired
    CriterionServiceImpl criterionService;

    @Autowired
    MarkServiceImpl markService;

    @Autowired
    ReloadableResourceBundleMessageSource messageSource;


    @RequestMapping(value = "/criterions", method = RequestMethod.GET)
    public String criterionsList(Model model) {
        List<Criterion> criterions = criterionService.findAllCriterions();
        model.addAttribute("criterions", criterions);
        model.addAttribute("title", messageSource.getMessage("pageTitle.criterion", null, Locale.getDefault()));
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            errorMessage = null;
        }
        return "criterion/criterions";
    }

    @RequestMapping(value = "/criterion/form")
    public String criterionForm(Model model) {
        model.addAttribute("command", new Criterion());
        model.addAttribute("title", messageSource.getMessage("pageTitle.criterion.new", null, Locale.getDefault()));
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            errorMessage = null;
        }
        return "criterion/criterionForm";

    }

    @RequestMapping(value = "/criterion/save", method = RequestMethod.POST)
    public String save(@ModelAttribute("criterion") Criterion criterion) {
        if (criterionService.findCriterionByName(criterion.getName()) != null) {
            errorMessage = messageSource.getMessage("error.addCriterionWithExistsName", null, Locale.getDefault());
            return "redirect:/criterion/criterionForm?lang=" + Locale.getDefault();
        } else {
            criterionService.save(criterion);
            return "redirect:/criterions?lang=" + Locale.getDefault();
        }
    }

    @RequestMapping(value = "/criterion/delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable long id) {
        Criterion criterion = criterionService.findCriterionById(id);
        if (!markService.findAllMarkByCriterion(criterion).isEmpty()) {
            errorMessage = messageSource.getMessage("error.deleteCriterionWithMarks", null, Locale.getDefault());
        } else {
            criterionService.delete(criterion);
        }
        return "redirect:/criterions?lang=" + Locale.getDefault();
    }

    @RequestMapping(value = "/criterion/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Criterion criterion = criterionService.findCriterionById(id);
        model.addAttribute("criterion", criterion);
        model.addAttribute("title", messageSource.getMessage("pageTitle.criterion.edit", null, Locale.getDefault()));
        return "criterion/criterionEditForm";
    }

    @RequestMapping(value = "/criterion/editsave", method = RequestMethod.POST)
    public String saveAfterEdit(@ModelAttribute("criterion") Criterion criterion) {
        if (criterionService.findCriterionByName(criterion.getName()) != null) {
            errorMessage = messageSource.getMessage("error.addCriterionWithSameName", null, Locale.getDefault());
        } else {
            criterionService.update(criterion);
        }
        return "redirect:/criterions?lang=" + Locale.getDefault();
    }
}
