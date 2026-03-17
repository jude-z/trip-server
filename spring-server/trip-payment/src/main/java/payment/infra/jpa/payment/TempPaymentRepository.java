package payment.infra.jpa.payment;

import core.domain.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import payment.domain.pay.payment.TempPayment;
import payment.domain.pay.status.PaymentStatus;

import java.util.Optional;

public interface TempPaymentRepository extends JpaRepository<TempPayment, Long> {
    Optional<TempPayment> findByPaymentKeyAndOrderIdAndAmountAndStatus(String paymentKey, String orderId, Long amount, PaymentStatus status);
}
