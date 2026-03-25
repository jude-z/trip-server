package payment.domain.mapper;

import payment.api.request.PaymentRequest;
import payment.api.request.TempPaymentRequest;
import core.domain.entity.member.Member;
import payment.domain.pay.payment.Payment;
import payment.domain.pay.payment.TempPayment;
import payment.domain.pay.status.PaymentStatus;

import java.time.LocalDateTime;

public class PaymentFactory {
    public static Payment from(PaymentRequest paymentRequest, Member member){
        return Payment.builder()
                .member(member)
                .orderId(paymentRequest.getOrderId())
                .amount(paymentRequest.getAmount())
                .paymentKey(paymentRequest.getPaymentKey())
                .status(PaymentStatus.COMPLETE)
                .createdTime(LocalDateTime.now())
                .build();
    }
    public static Payment from(String paymentKey,String orderId,Long amount, Member member){
        return Payment.builder()
                .member(member)
                .orderId(orderId)
                .amount(amount)
                .paymentKey(paymentKey)
                .status(PaymentStatus.COMPLETE)
                .createdTime(LocalDateTime.now())
                .build();
    }

    public static TempPayment from(TempPaymentRequest tempPaymentRequest, Member member){
        return TempPayment.builder()
                .member(member)
                .orderId(tempPaymentRequest.getOrderId())
                .amount(tempPaymentRequest.getAmount())
                .paymentKey(tempPaymentRequest.getPaymentKey())
                .status(PaymentStatus.PENDING)
                .retry(0)
                .createdTime(LocalDateTime.now())
                .build();
    }
}
