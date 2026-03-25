package payment.infra.jpa.payment;

import core.domain.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import payment.domain.pay.payment.TempPayment;
import payment.domain.pay.status.PaymentStatus;

import java.util.List;
import java.util.Optional;

public interface TempPaymentRepository extends JpaRepository<TempPayment, Long> {
    Optional<TempPayment> findByPaymentKeyAndOrderIdAndAmountAndStatus(String paymentKey, String orderId, Long amount, PaymentStatus status);

    @Modifying
    @Query("DELETE FROM TempPayment t WHERE t.paymentKey IN :paymentKeys")
    void deleteByPaymentKeyInBatch(List<String> paymentKeys);
    @Modifying
    @Query("update TempPayment set retry = retry + 1 where paymentKey in :addRetryPaymentKeys")
    void addRetry(List<String> addRetryPaymentKeys);
}
