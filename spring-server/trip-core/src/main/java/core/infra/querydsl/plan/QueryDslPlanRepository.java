package core.infra.querydsl.plan;

import com.querydsl.jpa.impl.JPAQueryFactory;
import core.domain.entity.itinerary.QItinerary;
import core.domain.entity.plan.Plan;
import core.domain.entity.plan.QPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QueryDslPlanRepository {

    private final JPAQueryFactory queryFactory;

    public List<Plan> findAllByMemberId(Long memberId) {
        QPlan plan = QPlan.plan;
        QItinerary itinerary = QItinerary.itinerary;
        return queryFactory
                .selectFrom(plan)
                .leftJoin(plan.itineraries, itinerary).fetchJoin()
                .where(plan.member.id.eq(memberId))
                .fetch();
    }
}
