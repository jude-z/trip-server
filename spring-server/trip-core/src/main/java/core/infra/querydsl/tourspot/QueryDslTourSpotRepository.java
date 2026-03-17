package core.infra.querydsl.tourspot;

import com.querydsl.jpa.impl.JPAQueryFactory;
import core.domain.entity.category.QCategory;
import core.domain.entity.destination.QDestination;
import core.domain.entity.tourspot.QTourSpot;
import core.domain.entity.tourspot.TourSpot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QueryDslTourSpotRepository {

    private final JPAQueryFactory queryFactory;

    public List<TourSpot> findAllByItineraryIdWithDetails(Long itineraryId) {
        QTourSpot tourSpot = QTourSpot.tourSpot;
        QDestination destination = QDestination.destination;
        QCategory category = QCategory.category1;

        return queryFactory
                .selectFrom(tourSpot)
                .join(tourSpot.destination, destination).fetchJoin()
                .join(destination.category, category).fetchJoin()
                .where(tourSpot.itinerary.itineraryId.eq(itineraryId))
                .orderBy(tourSpot.order.asc())
                .fetch();
    }
}
