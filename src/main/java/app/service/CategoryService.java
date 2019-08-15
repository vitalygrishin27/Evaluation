package app.service;

import app.model.Category;
import java.util.List;

public interface CategoryService {
    List<Category> findAllCategories();

    Category findCategoryByName(String name);

    void save(Category category);

    void delete(Category category);

    Category findCategoryById(long id);
}
