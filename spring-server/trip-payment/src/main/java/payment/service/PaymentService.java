package payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.infra.jpa.member.MemberRepository;
import org.springframework.data.redis.core.RedisTemplate;
import payment.api.request.TempPaymentRequest;
import payment.api.response.ApiResponse;
import payment.api.response.ApiStatusResponse;
import payment.api.response.ApiDataResponse;
import payment.api.request.PaymentRequest;
import payment.api.request.CancelPaymentRequest;
import core.common.Status;
import core.common.exception.CommonException;
import core.domain.entity.member.Member;
import payment.client.PaymentClient;
import payment.domain.mapper.PaymentFactory;
import payment.infra.jpa.idempotency.IdempotencyRepository;
import payment.infra.jpa.payment.TempPaymentRepository;
import payment.infra.projection.payment.PaymentElement;
import payment.domain.pay.idempotency.Idempotency;
import payment.infra.jpa.payment.PaymentRepository;
import payment.infra.querydsl.idempotency.QueryDslIdempotencyRepository;
import payment.infra.querydsl.payment.QueryDslPaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TempPaymentRepository tempPaymentRepository;
    private final MemberRepository memberRepository;
    private final QueryDslIdempotencyRepository queryDslIdempotencyRepository;
    private final QueryDslPaymentRepository queryDslPaymentRepository;
    private final IdempotencyRepository idempotencyRepository;
    private final RedisTemplate<String,String> redisTemplate;
    private final PaymentClient paymentClient;
    private final PaymentFacade paymentFacade;


    public ApiResponse pay(TempPaymentRequest tempPaymentRequest, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        tempPaymentRepository.save(PaymentFactory.from(tempPaymentRequest,member));
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ApiResponse confirm(String idempotencyKey, PaymentRequest paymentRequest, Long id){

        Boolean success = redisTemplate.opsForValue().setIfAbsent(idempotencyKey,"true", Duration.of(24L, ChronoUnit.HOURS));
        if(success == null) throw new CommonException(Status.REDIS_SERVER_ERROR);
        if(!success) throw new CommonException(Status.ALREADY_PAYMENT_REQUEST);
        Optional<Idempotency> optionalIdempotency = queryDslIdempotencyRepository.findByIdempotencyKey(idempotencyKey);
        if (optionalIdempotency.isPresent()) {
            throw new CommonException(Status.ALREADY_PAYMENT_REQUEST);
        }
        idempotencyRepository.save(Idempotency.of(idempotencyKey));
        boolean confirm = paymentClient.confirm(paymentRequest);


        if(confirm){
            paymentFacade.processConfirm(paymentRequest, id);
        }
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ApiResponse cancel(String idempotencyKey,CancelPaymentRequest cancelPaymentRequest, Long id) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(idempotencyKey,"true", Duration.of(24L, ChronoUnit.HOURS));
        if(success == null) throw new CommonException(Status.REDIS_SERVER_ERROR);
        if(!success) throw new CommonException(Status.ALREADY_PAYMENT_REQUEST);
        Optional<Idempotency> optionalIdempotency = queryDslIdempotencyRepository.findByIdempotencyKey(idempotencyKey);
        if (optionalIdempotency.isPresent()) {
            throw new CommonException(Status.ALREADY_PAYMENT_REQUEST);
        }
        idempotencyRepository.save(Idempotency.of(idempotencyKey));
        boolean cancel = paymentClient.cancel(cancelPaymentRequest);
        if(cancel){
            paymentFacade.processCancel(cancelPaymentRequest, id);
        }
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    public ApiResponse fetchPayments(Long id, Integer pageNum) {
        memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        PageRequest pageRequest = PageRequest.of(pageNum - 1, 10);
        Page<PaymentElement> page = queryDslPaymentRepository.fetchPayments(pageRequest, id);
        boolean hasNext = page.hasNext();
        List<PaymentElement> content = page.getContent();
        return ApiDataResponse.of(Map.of("content", content, "hasNext", hasNext), Status.SUCCESS);
    }

}
