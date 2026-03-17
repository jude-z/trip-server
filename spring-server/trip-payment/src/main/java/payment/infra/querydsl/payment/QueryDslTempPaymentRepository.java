package payment.infra.querydsl.payment;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import payment.domain.pay.payment.QTempPayment;
import payment.domain.pay.payment.TempPayment;
import payment.domain.pay.status.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class QueryDslTempPaymentRepository {

    private final JPAQueryFactory queryFactory;

    public List<TempPayment> fetchPendingPayments() {
        QTempPayment tp = QTempPayment.tempPayment;
        LocalDateTime now = LocalDateTime.now();

        BooleanExpression retryCondition = tp.retry.eq(0).and(tp.createdTime.loe(now.minusMinutes(10)))
                .or(tp.retry.eq(1).and(tp.createdTime.loe(now.minusMinutes(30))))
                .or(tp.retry.eq(2).and(tp.createdTime.loe(now.minusMinutes(60))));

        return queryFactory
                .selectFrom(tp)
                .where(
                        tp.status.eq(PaymentStatus.PENDING),
                        retryCondition
                )
                .orderBy(tp.id.asc())
                .limit(100)
                .fetch();
    }
}
