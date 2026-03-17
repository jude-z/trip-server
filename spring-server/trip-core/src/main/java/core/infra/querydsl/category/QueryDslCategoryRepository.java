package core.infra.querydsl.category;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import core.domain.entity.category.Category;
import core.domain.entity.category.QCategory;
import core.infra.projection.category.CategoryElement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QueryDslCategoryRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<Category> findCategoryByCodes(String cat1, String cat2, String cat3) {
        QCategory category = QCategory.category1;
        BooleanBuilder builder = new BooleanBuilder();

        if (isValid(cat3) && isValid(cat2) && isValid(cat1)) {
            builder.and(category.categoryCode.eq(cat3))
                    .and(category.category.categoryCode.eq(cat2))
                    .and(category.category.category.categoryCode.eq(cat1));
        } else if (isValid(cat2) && isValid(cat1)) {
            builder.and(category.categoryCode.eq(cat2))
                    .and(category.category.categoryCode.eq(cat1));
        } else if (isValid(cat1)) {
            builder.and(category.categoryCode.eq(cat1))
                    .and(category.category.isNull());
        }

        return Optional.ofNullable(queryFactory
                .selectFrom(category)
                .where(builder)
                .fetchOne());
    }

    public List<CategoryElement> fetchTotalCategory(Long categoryId) {
        QCategory category = QCategory.category1;
        return queryFactory
                .select(Projections.constructor(CategoryElement.class,
                        category.categoryId,
                        category.categoryCode,
                        category.name))
                .from(category)
                .where(category.category.categoryId.eq(categoryId))
                .fetch();
    }

    public List<CategoryElement> fetchTotalCategory() {
        QCategory category = QCategory.category1;
        return queryFactory
                .select(Projections.constructor(CategoryElement.class,
                        category.categoryId,
                        category.categoryCode,
                        category.name))
                .from(category)
                .where(category.category.categoryId.isNull())
                .fetch();
    }

    private boolean isValid(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
