package payment.service;

import core.domain.entity.member.Member;
import core.infra.jdbc.member.JdbcMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import payment.api.request.PaymentRequest;
import payment.domain.pay.idempotency.Idempotency;
import payment.domain.pay.payment.Payment;
import payment.fixture.PaymentFixture;
import payment.infra.jpa.idempotency.IdempotencyRepository;
import payment.infra.jpa.payment.PaymentRepository;
import payment.infra.jdbc.payment.JdbcPaymentRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentFacadeTest {

    @InjectMocks
    private PaymentFacade paymentFacade;

    @Mock
    private IdempotencyRepository idempotencyRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private JdbcPaymentRepository jdbcPaymentRepository;

    @Mock
    private JdbcMemberRepository jdbcMemberRepository;

    private Member member;
    private PaymentRequest paymentRequest;
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
        idempotencyKey = "test-idempotency-key";
    }

    @Test
    @DisplayName("processConfirm - saves idempotency and payment and updates ticket")
    void processConfirm_savesIdempotencyAndPaymentAndUpdatesTicket() {
        // given
        when(idempotencyRepository.save(any(Idempotency.class))).thenReturn(Idempotency.of(idempotencyKey));
        when(paymentRepository.save(any(Payment.class))).thenReturn(PaymentFixture.createPayment(member));

        // when
//        paymentFacade.processConfirm(idempotencyKey, paymentRequest, member);

        // then
        verify(idempotencyRepository).save(any(Idempotency.class));
        verify(paymentRepository).save(any(Payment.class));
        verify(jdbcMemberRepository).updateTicket(eq(paymentRequest.getAmount() / 200), eq(member.getId()));
    }

    @Test
    @DisplayName("processCancel - saves idempotency and cancels payment and updates ticket")
    void processCancel_savesIdempotencyAndCancelsAndUpdatesTicket() {
        // given
        String paymentKey = "test-payment-key";
        Integer amount = 1000;
        Long memberId = 1L;

        when(idempotencyRepository.save(any(Idempotency.class))).thenReturn(Idempotency.of(idempotencyKey));

        // when
        paymentFacade.processCancel(idempotencyKey, paymentKey, amount, memberId);

        // then
        verify(idempotencyRepository).save(any(Idempotency.class));
        verify(jdbcPaymentRepository).updateCancelled(paymentKey);
        verify(jdbcMemberRepository).updateTicket(eq((-1) * amount), eq(memberId));
    }
}
