package com.tripplannerai.repository.payment;

import com.tripplannerai.dto.response.payment.PaymentElement;
import com.tripplannerai.entity.pay.payment.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentKey(String paymentKey);

    @Query("select new com.tripplannerai.dto.response.payment.PaymentElement" +
            "(p.paymentId,p.paymentKey,p.amount,p.orderId,p.cancelled) " +
            "from Payment p left join p.member m where m.id = :id")
    Page<PaymentElement> fetchPayments(PageRequest pageRequest, Long id);
}
