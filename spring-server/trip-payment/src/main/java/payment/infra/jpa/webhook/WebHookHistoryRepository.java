package payment.infra.jpa.webhook;

import org.springframework.data.jpa.repository.JpaRepository;
import payment.domain.pay.webhook.WebHookHistory;

import java.util.Optional;

public interface WebHookHistoryRepository extends JpaRepository<WebHookHistory, Long> {
    Optional<WebHookHistory> findByPaymentKeyAndProcessedTrue(String paymentKey);
}
