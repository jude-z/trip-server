package payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import core.common.Status;
import core.common.exception.CommonException;
import core.domain.entity.member.Member;
import core.domain.entity.point.Point;
import core.infra.jpa.point.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import payment.domain.mapper.PaymentFactory;
import payment.domain.pay.payment.Payment;
import payment.domain.pay.payment.TempPayment;
import payment.domain.pay.status.PaymentStatus;
import payment.domain.pay.webhook.WebHookHistory;
import payment.infra.jpa.payment.PaymentRepository;
import payment.infra.jpa.payment.TempPaymentRepository;
import payment.infra.jpa.webhook.WebHookHistoryRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WebHookService {
    private final PaymentRepository paymentRepository;
    private final TempPaymentRepository tempPaymentRepository;
    private final PointRepository pointRepository;
    private final WebHookHistoryRepository webHookHistoryRepository;

    @Transactional
    public void processWebHook(JsonNode jsonNode) {
        JsonNode data = jsonNode.get("data");
        String status = data.get("status").asText();
        String paymentKey = data.get("paymentKey").asText();
        String orderId = data.get("orderId").asText();
        long amount = Long.parseLong(data.get("totalAmount").asText());

        if (!status.equals("DONE")) {
            webHookHistoryRepository.save(WebHookHistory.of(paymentKey, orderId, amount, status, false));
            return;
        }

        // DB로 멱등성 체크 - 이미 처리된 웹훅이면 무시
        Optional<WebHookHistory> existing = webHookHistoryRepository.findByPaymentKeyAndProcessedTrue(paymentKey);
        if (existing.isPresent()) return;

        TempPayment tempPayment = tempPaymentRepository
                .findByPaymentKeyAndOrderIdAndAmountAndStatus(paymentKey, orderId, amount, PaymentStatus.PENDING)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_TEMP_PAYMENT));

        Member member = tempPayment.getMember();
        Point point = pointRepository.findByMember(member)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_POINT));

        Optional<Payment> optionalPayment = paymentRepository.findByPaymentKeyAndOrderIdAndAmount(paymentKey, orderId, amount);
        if (optionalPayment.isEmpty()) {
            paymentRepository.save(PaymentFactory.from(paymentKey, orderId, amount, member));
            tempPaymentRepository.delete(tempPayment);
            point.addAmount(amount);
        }

        webHookHistoryRepository.save(WebHookHistory.of(paymentKey, orderId, amount, status, true));
    }
}
