package core.infra.jpa.category;

import core.domain.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    List<Category> category(Category category);
}
