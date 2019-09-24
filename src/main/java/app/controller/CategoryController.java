package app.controller;

import app.model.Category;
import app.model.Criterion;
import app.model.CriterionWrapper;
import app.service.impl.CategoryServiceImpl;
import app.service.impl.CriterionServiceImpl;

import app.utils.WebUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Locale;

import static app.utils.WebUtils.*;

@Controller
public class CategoryController {

    @Autowired
    CategoryServiceImpl categoryService;

    @Autowired
    CriterionServiceImpl criterionService;

    @Autowired
    ReloadableResourceBundleMessageSource messageSource;

    @PersistenceContext
    private EntityManager entityManager;


    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    public String categoryList(Model model) {
        List<Category> categories = categoryService.findAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("title", messageSource.getMessage("pageTitle.category", null, Locale.getDefault()));
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            errorMessage = null;
        }
        return "category/categories";
    }

    @RequestMapping(value = "/category/form")
    public String categoryForm(Model model) {
        model.addAttribute("command", new Category());
        CriterionWrapper criterionWrapper = new CriterionWrapper();
        List<Criterion> allCriterions = criterionService.findAllCriterions();
        criterionWrapper.setListCriterion(allCriterions);
        model.addAttribute("criterionWrapper", criterionWrapper);
        model.addAttribute("title", messageSource.getMessage("pageTitle.category.new", null, Locale.getDefault()));
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            errorMessage = null;
        }
        return "category/categoryForm";

    }

    @RequestMapping(value = "/category/save", method = RequestMethod.POST)
    public String save(@ModelAttribute("category") Category category, @ModelAttribute("criterionWrapper") CriterionWrapper criterionWrapper) {
        if (categoryService.findCategoryByName(category.getCategoryName()) != null) {
            errorMessage = messageSource.getMessage("error.addCategoryWithExistsName", null, Locale.getDefault());
            return "redirect:/category/categoryForm?lang=" + Locale.getDefault();
        } else {
            category.setCriterions(criterionWrapper.getListCriterion());
            categoryService.save(category);
            return "redirect:/categories?lang=" + Locale.getDefault();
        }
    }

    @RequestMapping(value = "/category/delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable long id) {
        Category category = categoryService.findCategoryById(id);
        if (!category.getMembers().isEmpty()) {
            errorMessage = messageSource.getMessage("error.deleteCategoryWithMembers", null, Locale.getDefault());
        } else {
            categoryService.delete(category);
        }
        return "redirect:/categories?lang=" + Locale.getDefault();
    }

    @RequestMapping(value = "/category/edit/{id}")
    public String edit(@PathVariable long id, Model model) {
        Category category = categoryService.findCategoryById(id);
        model.addAttribute("category", category);
        CriterionWrapper criterionWrapper = new CriterionWrapper();
        List<Criterion> allCriterions = criterionService.findAllCriterions();
        criterionWrapper.setListCriterion(allCriterions);
        model.addAttribute("criterionWrapper", criterionWrapper);
        model.addAttribute("title", messageSource.getMessage("pageTitle.category.edit", null, Locale.getDefault()));
        return "category/categoryEditForm";
    }

    @Transactional
    @RequestMapping(value = "/category/editsave", method = RequestMethod.POST)
    public String editsave(@ModelAttribute("category") Category category, @ModelAttribute("criterionWrapper") CriterionWrapper criterionWrapper) {
        // TODO: 24.09.2019 реализовать проверку нет ли оценок по данному критерию в данной категории 
        category.setCriterions(criterionWrapper.getListCriterion());
        try {
            entityManager.unwrap(Session.class).update(category);
        } catch (Exception e) {
            errorMessage = e.getMessage();
            return "redirect:/category/categoryForm?lang=" + Locale.getDefault();
        }
        return "redirect:/categories?lang=" + Locale.getDefault();

    }
}
