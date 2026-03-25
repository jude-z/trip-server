package payment.domain.pay.payment;

import core.domain.entity.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import payment.domain.pay.status.PaymentStatus;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class TempPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "payment_key")
    private String paymentKey;
    @Column(name = "order_id")
    private String orderId;
    private Long amount;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private Integer retry;
    private LocalDateTime createdTime;

}
