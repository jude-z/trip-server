package core.infra.querydsl.kindplace;

import com.querydsl.jpa.impl.JPAQueryFactory;
import core.domain.entity.kindplace.KindPlace;
import core.domain.entity.kindplace.QKindPlace;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QueryDslKindPlaceRepository {

    private final JPAQueryFactory queryFactory;

    public List<KindPlace> fetchKindPlaces(String address, Pageable pageable) {
        QKindPlace kindPlace = QKindPlace.kindPlace;
        return queryFactory
                .selectFrom(kindPlace)
                .where(kindPlace.address.contains(address))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
