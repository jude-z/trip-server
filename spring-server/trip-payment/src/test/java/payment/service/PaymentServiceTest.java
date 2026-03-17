package payment.service;

import core.common.Status;
import core.common.exception.CommonException;
import core.domain.entity.member.Member;
import core.infra.jpa.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import payment.api.request.PaymentRequest;
import payment.api.request.CancelPaymentRequest;
import payment.api.response.ApiResponse;
import payment.client.PaymentClient;
import payment.domain.pay.idempotency.Idempotency;
import payment.domain.pay.payment.Payment;
import payment.fixture.PaymentFixture;
import payment.infra.jpa.payment.PaymentRepository;
import payment.infra.projection.payment.PaymentElement;
import payment.infra.querydsl.idempotency.QueryDslIdempotencyRepository;
import payment.infra.querydsl.payment.QueryDslPaymentRepository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private QueryDslIdempotencyRepository queryDslIdempotencyRepository;

    @Mock
    private QueryDslPaymentRepository queryDslPaymentRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private PaymentFacade paymentFacade;

    private Member member;
    private PaymentRequest paymentRequest;
    private Payment payment;
    private String idempotencyKey;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .email("test@example.com")
                .password("pw")
                .nickname("nick")
                .ticket(5)
                .build();

        paymentRequest = PaymentFixture.createPaymentRequest();
        payment = PaymentFixture.createPayment(member);
        idempotencyKey = "test-idempotency-key";
    }

    // ==================== confirm tests ====================

    @Test
    @DisplayName("confirm - success")
    void confirm_success() {
        // given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(true);
        when(queryDslIdempotencyRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(paymentClient.confirm(paymentRequest)).thenReturn(true);

        // when
        ApiResponse response = paymentService.confirm(idempotencyKey, paymentRequest, 1L);

        // then
        assertThat(response).isNotNull();
//        verify(paymentFacade).processConfirm(idempotencyKey, paymentRequest, member);
    }

    @Test
    @DisplayName("confirm - Redis returns null throws REDIS_SERVER_ERROR")
    void confirm_redisNull() {
        // given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> paymentService.confirm(idempotencyKey, paymentRequest, 1L))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.REDIS_SERVER_ERROR));
    }

    @Test
    @DisplayName("confirm - Redis duplicate throws ALREADY_PAYMENT_REQUEST")
    void confirm_redisDuplicate() {
        // given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> paymentService.confirm(idempotencyKey, paymentRequest, 1L))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.ALREADY_PAYMENT_REQUEST));
    }

    @Test
    @DisplayName("confirm - idempotency key already exists in DB throws ALREADY_PAYMENT_REQUEST")
    void confirm_idempotencyKeyDuplicate() {
        // given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(true);
        when(queryDslIdempotencyRepository.findByIdempotencyKey(idempotencyKey))
                .thenReturn(Optional.of(Idempotency.of(idempotencyKey)));

        // when & then
        assertThatThrownBy(() -> paymentService.confirm(idempotencyKey, paymentRequest, 1L))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.ALREADY_PAYMENT_REQUEST));
    }

    @Test
    @DisplayName("confirm - member not found throws NOT_FOUND_MEMBER")
    void confirm_memberNotFound() {
        // given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(true);
        when(queryDslIdempotencyRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.confirm(idempotencyKey, paymentRequest, 1L))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.NOT_FOUND_MEMBER));
    }

    @Test
    @DisplayName("confirm - PG confirm fails throws PAYMENT_SERVER_ERROR")
    void confirm_pgFailed() {
        // given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(true);
        when(queryDslIdempotencyRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(paymentClient.confirm(paymentRequest)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> paymentService.confirm(idempotencyKey, paymentRequest, 1L))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.PAYMENT_SERVER_ERROR));
    }

    // ==================== cancel tests ====================

    @Test
    @DisplayName("cancel - success")
    void cancel_success() {
        // given
        String paymentKey = "test-payment-key";
        CancelPaymentRequest cancelPaymentRequest = PaymentFixture.createReason();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(true);
        when(queryDslIdempotencyRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(paymentRepository.findByPaymentKey(paymentKey)).thenReturn(Optional.of(payment));
        when(paymentClient.cancel(cancelPaymentRequest, paymentKey)).thenReturn(true);

        // when
        ApiResponse response = paymentService.cancel(idempotencyKey, 1L, cancelPaymentRequest, paymentKey);

        // then
        assertThat(response).isNotNull();
        verify(paymentFacade).processCancel(eq(idempotencyKey), eq(paymentKey), eq(payment.getAmount()), eq(1L));
    }

    @Test
    @DisplayName("cancel - payment not found throws NOT_FOUND_PAYMENT")
    void cancel_paymentNotFound() {
        // given
        String paymentKey = "test-payment-key";
        CancelPaymentRequest cancelPaymentRequest = PaymentFixture.createReason();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(true);
        when(queryDslIdempotencyRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(paymentRepository.findByPaymentKey(paymentKey)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.cancel(idempotencyKey, 1L, cancelPaymentRequest, paymentKey))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.NOT_FOUND_PAYMENT));
    }

    @Test
    @DisplayName("cancel - PG cancel fails throws PAYMENT_SERVER_ERROR")
    void cancel_pgFailed() {
        // given
        String paymentKey = "test-payment-key";
        CancelPaymentRequest cancelPaymentRequest = PaymentFixture.createReason();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(true);
        when(queryDslIdempotencyRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(paymentRepository.findByPaymentKey(paymentKey)).thenReturn(Optional.of(payment));
        when(paymentClient.cancel(cancelPaymentRequest, paymentKey)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> paymentService.cancel(idempotencyKey, 1L, cancelPaymentRequest, paymentKey))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.PAYMENT_SERVER_ERROR));
    }

    @Test
    @DisplayName("cancel - idempotency key duplicate throws ALREADY_PAYMENT_REQUEST")
    void cancel_idempotencyDuplicate() {
        // given
        String paymentKey = "test-payment-key";
        CancelPaymentRequest cancelPaymentRequest = PaymentFixture.createReason();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(true);
        when(queryDslIdempotencyRepository.findByIdempotencyKey(idempotencyKey))
                .thenReturn(Optional.of(Idempotency.of(idempotencyKey)));

        // when & then
        assertThatThrownBy(() -> paymentService.cancel(idempotencyKey, 1L, cancelPaymentRequest, paymentKey))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.ALREADY_PAYMENT_REQUEST));
    }

    // ==================== fetchPayments tests ====================

    @Test
    @DisplayName("fetchPayments - success")
    void fetchPayments_success() {
        // given
        Long memberId = 1L;
        Integer pageNum = 1;
        Page<PaymentElement> page = new PageImpl<>(List.of());

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(queryDslPaymentRepository.fetchPayments(any(PageRequest.class), eq(memberId))).thenReturn(page);

        // when
        ApiResponse response = paymentService.fetchPayments(memberId, pageNum);

        // then
        assertThat(response).isNotNull();
        verify(memberRepository).findById(memberId);
    }
}
