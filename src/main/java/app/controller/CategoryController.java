package app.controller;

import app.model.Category;
import app.model.Criterion;
import app.model.CriterionWrapper;
import app.service.impl.CategoryServiceImpl;
import app.service.impl.CriterionServiceImpl;
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

@Controller
public class CategoryController {

    @Autowired
    CategoryServiceImpl categoryService;

    @Autowired
    CriterionServiceImpl criterionService;

    @Autowired
    ReloadableResourceBundleMessageSource messageSource;

    private String errorMessage = null;

    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    public String categoryList(Model model) {
        List<Category> categories = categoryService.findAllCategories();
        model.addAttribute("categories", categories);
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            errorMessage = null;
        }
        return "category/categories";
    }

    @RequestMapping(value = "/category/form")
    public String categoryForm(Model model) {
        model.addAttribute("command", new Category());
      //  model.addAttribute("criterions",criterionService.findAllCriterions());
        CriterionWrapper criterionWrapper=new CriterionWrapper();
        List<Criterion> allCriterions =criterionService.findAllCriterions();
        criterionWrapper.setListCriterion(allCriterions);
        model.addAttribute("criterionWrapper",criterionWrapper);
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
        return "category/editform";
    }

    @RequestMapping(value = "/category/editsave", method = RequestMethod.POST)
    public String editsave(@ModelAttribute("category") Category category) {
        if(categoryService.findCategoryByName(category.getCategoryName())!=null){
            errorMessage = messageSource.getMessage("error.addCategoryWithSameName", null, Locale.getDefault());
        }else{
            categoryService.update(category);
        }
       return "redirect:/categories?lang=" + Locale.getDefault();
    }

}
