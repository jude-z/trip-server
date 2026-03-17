package payment.service;

import core.common.Status;
import core.common.exception.CommonException;
import core.domain.entity.member.Member;
import core.domain.entity.point.Point;
import core.infra.jpa.member.MemberRepository;
import core.infra.jpa.point.PointRepository;
import org.springframework.transaction.annotation.Propagation;
import payment.api.request.CancelPaymentRequest;
import payment.api.request.PaymentRequest;
import payment.domain.mapper.PaymentFactory;
import payment.domain.pay.payment.Payment;
import payment.domain.pay.payment.TempPayment;
import payment.domain.pay.status.PaymentStatus;
import payment.infra.jpa.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import payment.infra.jpa.payment.TempPaymentRepository;

@Component
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class PaymentFacade {
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final PointRepository pointRepository;
    private final TempPaymentRepository tempPaymentRepository;

    public void processConfirm(PaymentRequest paymentRequest, Long id) {
        String paymentKey = paymentRequest.getPaymentKey();
        String orderId = paymentRequest.getOrderId();
        Long amount = paymentRequest.getAmount();
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        TempPayment tempPayment = tempPaymentRepository
                .findByPaymentKeyAndOrderIdAndAmountAndStatus(paymentKey, orderId, amount,PaymentStatus.PENDING)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_PAYMENT));
        paymentRepository.findByPaymentKeyAndOrderIdAndAmount(paymentKey, orderId, amount)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_PAYMENT));
        Payment payment = PaymentFactory.from(paymentRequest, member);
        paymentRepository.save(payment);
        Point point = pointRepository.findByMember(member)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_POINT));
        point.addAmount(amount);
        tempPaymentRepository.delete(tempPayment);
    }

    public void processCancel(CancelPaymentRequest cancelPaymentRequest,Long id) {
        String paymentKey = cancelPaymentRequest.getPaymentKey();
        String orderId = cancelPaymentRequest.getOrderId();
        Long amount = cancelPaymentRequest.getAmount();
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        Payment payment = paymentRepository.findByPaymentKeyAndOrderIdAndAmountAndStatus(paymentKey,orderId,amount,PaymentStatus.COMPLETE)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_PAYMENT));
        payment.changeStatus(PaymentStatus.CANCEL);
        Point point = pointRepository.findByMember(member)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_POINT));
        point.addAmount(-amount);
    }
}
