package com.tripplannerai.repository.category;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tripplannerai.entity.category.Category;
import com.tripplannerai.entity.category.QCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoryCustomRepositoryImpl implements CategoryCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
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

    private boolean isValid(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
