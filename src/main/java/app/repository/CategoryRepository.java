package app.repository;

import app.model.Category;
import app.model.Criterion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    @Query("Select c from Category c where c.categoryName = :categoryName")
    Category findCategoryByName(@Param("categoryName") String categoryName);

    @Query("Select c from Category c where c.id = :id")
    Category findCategoryById(@Param("id") Long id);


    //   public static final String SQL_UPDATE = "UPDATE " + Item.TABLE_NAME + " SET " + Item.NAME_COLUMN + " = ?" + COMMA + Item.WAREHOUSE_ID_COLUMN + " = ?" + " WHERE " + Item.ID_COLUMN + " = ?"
 //   @Modifying
 //   @Query("UPDATE Category c SET c.name = :name WHERE c.id = :id")
 //   void update(@Param("id") Long id, @Param("name") String name);
    @Transactional
    @Modifying
    @Query("UPDATE Category c SET c.categoryName = ?2 WHERE c.id = ?1")
    void update(Long id, String categoryName);


 /*   @Transactional
    @Modifying
    @Query("DELETE from c SET c.categoryName = ?2 WHERE c.id = ?1")
    void deleteLinkWithCategory(category.getId())
*/
}
