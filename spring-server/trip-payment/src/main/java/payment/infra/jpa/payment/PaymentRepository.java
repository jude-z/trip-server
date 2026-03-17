package payment.infra.jpa.payment;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Lock;
import payment.domain.pay.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import payment.domain.pay.status.PaymentStatus;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Payment> findByPaymentKeyAndOrderIdAndAmountAndStatus(String paymentKey, String orderId, Long amount, PaymentStatus status);
    Optional<Payment> findByPaymentKeyAndOrderIdAndAmount(String paymentKey, String orderId, Long amount);

    List<Payment> findByPaymentKeyIn(List<String> paymentKey);
}
