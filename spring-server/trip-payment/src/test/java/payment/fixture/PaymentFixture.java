package payment.fixture;

import core.domain.entity.member.Member;
import payment.api.request.PaymentRequest;
import payment.api.request.CancelPaymentRequest;
import payment.domain.pay.idempotency.Idempotency;
import payment.domain.pay.payment.Payment;

public class PaymentFixture {

    public static final String DEFAULT_PAYMENT_KEY = "toss_payment_key_123";
    public static final String DEFAULT_ORDER_ID = "ORDER-20260314-001";
    public static final Integer DEFAULT_AMOUNT = 10000;
    public static final String DEFAULT_IDEMPOTENCY_KEY = "idem-key-001";

    public static Payment.PaymentBuilder defaultPayment(Member member) {
        return Payment.builder()
                .paymentId(1L)
                .paymentKey(DEFAULT_PAYMENT_KEY)
                .amount(DEFAULT_AMOUNT)
                .orderId(DEFAULT_ORDER_ID)
                .member(member)
                .cancelled(false);
    }

    public static Payment createPayment(Member member) {
        return defaultPayment(member).build();
    }

    public static TempPayment.TempPaymentBuilder defaultTempPayment(Member member) {
        return TempPayment.builder()
                .tempPaymentId(1L)
                .orderId(DEFAULT_ORDER_ID)
                .amount(DEFAULT_AMOUNT)
                .member(member);
    }

    public static TempPayment createTempPayment(Member member) {
        return defaultTempPayment(member).build();
    }

    public static Idempotency createIdempotency(String key) {
        return Idempotency.of(key);
    }

    public static PaymentRequest createPaymentRequest() {
        return PaymentRequest.builder()
                .paymentKey(DEFAULT_PAYMENT_KEY)
                .orderId(DEFAULT_ORDER_ID)
                .amount(DEFAULT_AMOUNT)
                .build();
    }

    public static CancelPaymentRequest createReason() {
        return new CancelPaymentRequest("Customer requested cancellation");
    }
}
