package app.repository;

import app.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    @Query("Select c from Category c where c.name = :name")
    Category findCategoryByName(@Param("name") String name);

    @Query("Select c from Category c where c.id = :id")
    Category findCategoryById(@Param("id") Long id);
}
