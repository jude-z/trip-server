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
import payment.infra.redis.IdempotencyRedisManager;
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
@Transactional
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
    private final IdempotencyRedisManager idempotencyRedisManager;


    public ApiResponse pay(TempPaymentRequest tempPaymentRequest, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        tempPaymentRepository.save(PaymentFactory.from(tempPaymentRequest,member));
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ApiResponse confirm(String idempotencyKey, PaymentRequest paymentRequest, Long id){

        if(!idempotencyRedisManager.tryAcquire(idempotencyKey)) {
            throw new CommonException(Status.ALREADY_PAYMENT_REQUEST);
        }
        Optional<Idempotency> optionalIdempotency = queryDslIdempotencyRepository.findByIdempotencyKey(idempotencyKey);
        if (optionalIdempotency.isPresent()) {
            throw new CommonException(Status.ALREADY_PAYMENT_REQUEST);
        }
        idempotencyRepository.save(Idempotency.of(idempotencyKey));
        boolean confirm = paymentClient.confirm(paymentRequest);

        if(confirm){
            paymentFacade.processConfirm(paymentRequest, id);
            idempotencyRedisManager.complete(idempotencyKey);
        } else {
            idempotencyRedisManager.fail(idempotencyKey);
        }
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ApiResponse cancel(String idempotencyKey,CancelPaymentRequest cancelPaymentRequest, Long id) {
        if(!idempotencyRedisManager.tryAcquire(idempotencyKey)) {
            throw new CommonException(Status.ALREADY_PAYMENT_REQUEST);
        }
        Optional<Idempotency> optionalIdempotency = queryDslIdempotencyRepository.findByIdempotencyKey(idempotencyKey);
        if (optionalIdempotency.isPresent()) {
            throw new CommonException(Status.ALREADY_PAYMENT_REQUEST);
        }
        idempotencyRepository.save(Idempotency.of(idempotencyKey));
        boolean cancel = paymentClient.cancel(cancelPaymentRequest);
        if(cancel){
            paymentFacade.processCancel(cancelPaymentRequest, id);
            idempotencyRedisManager.complete(idempotencyKey);
        } else {
            idempotencyRedisManager.fail(idempotencyKey);
        }
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    // 시나리오 1: 멱등성 보장 없음 (Redis, DB 체크 없이 바로 결제)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ApiResponse confirmNoIdempotency(PaymentRequest paymentRequest, Long id){
        boolean confirm = paymentClient.confirm(paymentRequest);
        if(confirm){
            paymentFacade.processConfirm(paymentRequest, id);
        }
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    // 시나리오 2: Redis 사용하지만 Redis 장애 시 멱등성 뚫림 (실제 Redis 내려서 테스트)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ApiResponse confirmRedisFail(String idempotencyKey, PaymentRequest paymentRequest, Long id){
        try {
            if(!idempotencyRedisManager.tryAcquire(idempotencyKey)) {
                throw new CommonException(Status.ALREADY_PAYMENT_REQUEST);
            }
        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            // Redis 장애 시 로그만 남기고 통과 → 멱등성 뚫림
            log.warn("[V2] Redis 장애 발생 - 멱등성 체크 스킵. key={}", idempotencyKey);
        }

        boolean confirm = paymentClient.confirm(paymentRequest);
        if(confirm){
            paymentFacade.processConfirm(paymentRequest, id);
            try { idempotencyRedisManager.complete(idempotencyKey); } catch (Exception ignored) {}
        } else {
            try { idempotencyRedisManager.fail(idempotencyKey); } catch (Exception ignored) {}
        }
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    // 시나리오 3: DB에 결제 정보 저장했을 내려가는 시나리오
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ApiResponse confirmNoDbSave(String idempotencyKey, PaymentRequest paymentRequest, Long id){
        if(!idempotencyRedisManager.tryAcquire(idempotencyKey)) {
            throw new CommonException(Status.ALREADY_PAYMENT_REQUEST);
        }

        // DB 멱등성 저장 스킵 (DB 장애 시뮬레이션)
        log.warn("[NO_DB_SAVE] DB 멱등성 저장 스킵 - 웹훅으로 보완 필요. key={}", idempotencyKey);

        boolean confirm = paymentClient.confirm(paymentRequest);
        if(confirm){
//            paymentFacade.processConfirm(paymentRequest, id);
            idempotencyRedisManager.complete(idempotencyKey);
        } else {
            idempotencyRedisManager.fail(idempotencyKey);
        }
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    // 시나리오 4: Redis+DB 멱등성 사용, DB 저장 실패 , 웹훅 실패  → 외부 API로 결제 상태 확인
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ApiResponse confirmExternalCheck(String idempotencyKey, PaymentRequest paymentRequest, Long id){
        if(!idempotencyRedisManager.tryAcquire(idempotencyKey)) {
            throw new CommonException(Status.ALREADY_PAYMENT_REQUEST);
        }

        Optional<Idempotency> optionalIdempotency = queryDslIdempotencyRepository.findByIdempotencyKey(idempotencyKey);
        if (optionalIdempotency.isPresent()) {
            throw new CommonException(Status.ALREADY_PAYMENT_REQUEST);
        }
        idempotencyRepository.save(Idempotency.of(idempotencyKey));

        boolean confirm = paymentClient.confirm(paymentRequest);
        if(!confirm){
            // 외부 서버 오류 → 결제 상태 직접 조회
            log.warn("[EXTERNAL_CHECK] 외부 결제 서버 오류 - paymentKey로 결제 상태 직접 조회. key={}", idempotencyKey);
            boolean paymentCompleted = paymentClient.checkPaymentStatus(paymentRequest.getPaymentKey());
            if(paymentCompleted){
//                paymentFacade.processConfirm(paymentRequest, id);
                idempotencyRedisManager.complete(idempotencyKey);
                return ApiStatusResponse.of(Status.SUCCESS);
            }
            idempotencyRedisManager.fail(idempotencyKey);
            throw new CommonException(Status.PAYMENT_SERVER_ERROR);
        }
        paymentFacade.processConfirm(paymentRequest, id);
        idempotencyRedisManager.complete(idempotencyKey);
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
