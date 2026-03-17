package payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import core.common.Status;
import core.common.exception.CommonException;
import core.domain.entity.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import payment.domain.mapper.PaymentFactory;
import payment.domain.pay.idempotency.Idempotency;
import payment.domain.pay.payment.Payment;
import payment.domain.pay.payment.TempPayment;
import payment.domain.pay.status.PaymentStatus;
import payment.infra.jpa.idempotency.IdempotencyRepository;
import payment.infra.jpa.payment.PaymentRepository;
import payment.infra.jpa.payment.TempPaymentRepository;
import payment.infra.querydsl.idempotency.QueryDslIdempotencyRepository;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WebHookService {
    private final PaymentRepository paymentRepository;
    private final TempPaymentRepository tempPaymentRepository;
    private final IdempotencyRepository idempotencyRepository;
    private final RedisTemplate<String,String> redisTemplate;
    private final QueryDslIdempotencyRepository queryDslIdempotencyRepository;



    public void processWebHook(JsonNode jsonNode) {
        JsonNode data = jsonNode.get("data").get("status");
        String status = data.get("status").asText();
        String paymentKey = data.get("paymentKey").asText();
        String orderId = data.get("orderId").asText();
        long amount = Long.parseLong(data.get("totalAmount").asText());
        if(!status.equals("DONE")) return;
        Boolean success = redisTemplate.opsForValue().setIfAbsent(paymentKey,"true", Duration.of(24L, ChronoUnit.HOURS));
        if(success == null) throw new CommonException(Status.REDIS_SERVER_ERROR);
        if(!success) throw new CommonException(Status.ALREADY_CANCEL_REQUEST);
        Optional<Idempotency> optionalIdempotency = queryDslIdempotencyRepository.findByIdempotencyKey(paymentKey);
        if (optionalIdempotency.isPresent()) {
            throw new CommonException(Status.ALREADY_CANCEL_REQUEST);
        }
        idempotencyRepository.save(Idempotency.of(paymentKey));
        TempPayment tempPayment = tempPaymentRepository.findByPaymentKeyAndOrderIdAndAmountAndStatus(paymentKey, orderId, amount, PaymentStatus.PENDING)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_TEMP_PAYMENT));
        Member member = tempPayment.getMember();
        Optional<Payment> optionalPayment = paymentRepository.findByPaymentKeyAndOrderIdAndAmount(paymentKey, orderId, amount);
        if(optionalPayment.isEmpty()){
            paymentRepository.save(PaymentFactory.from(paymentKey,orderId,amount,member));
        }
    }
}
