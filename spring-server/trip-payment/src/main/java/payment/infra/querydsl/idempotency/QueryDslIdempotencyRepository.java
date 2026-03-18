package payment.infra.querydsl.idempotency;

import com.querydsl.jpa.impl.JPAQueryFactory;
import payment.domain.pay.idempotency.Idempotency;
import payment.domain.pay.idempotency.QIdempotency;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QueryDslIdempotencyRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<Idempotency> findByIdempotencyKey(String idempotencyKey) {
        QIdempotency idempotency = QIdempotency.idempotency;
        return Optional.ofNullable(queryFactory
                .selectFrom(idempotency)
                .where(idempotency.idempotencyKey.eq(idempotencyKey))
                .fetchOne());
    }
}
