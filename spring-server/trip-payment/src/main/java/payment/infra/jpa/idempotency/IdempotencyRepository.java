package payment.infra.jpa.idempotency;

import payment.domain.pay.idempotency.Idempotency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyRepository extends JpaRepository<Idempotency, Long> {
}
