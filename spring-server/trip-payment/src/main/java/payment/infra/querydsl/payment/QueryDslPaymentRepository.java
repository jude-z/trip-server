package payment.infra.querydsl.payment;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import payment.infra.projection.payment.PaymentElement;
import core.domain.entity.member.QMember;
import payment.domain.pay.payment.QPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QueryDslPaymentRepository {

    private final JPAQueryFactory queryFactory;

    public Page<PaymentElement> fetchPayments(PageRequest pageRequest, Long id) {
        QPayment payment = QPayment.payment;
        QMember member = QMember.member;

        List<PaymentElement> content = queryFactory
                .select(Projections.constructor(PaymentElement.class,
                        payment.paymentId,
                        payment.paymentKey,
                        payment.amount,
                        payment.orderId,
                        payment.cancelled))
                .from(payment)
                .leftJoin(payment.member, member)
                .where(member.id.eq(id))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(payment.count())
                .from(payment)
                .leftJoin(payment.member, member)
                .where(member.id.eq(id))
                .fetchOne();

        return new PageImpl<>(content, pageRequest, total != null ? total : 0L);
    }
}
