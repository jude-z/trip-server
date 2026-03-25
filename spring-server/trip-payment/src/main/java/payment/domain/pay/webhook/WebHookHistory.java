package payment.domain.pay.webhook;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "webhook_history")
public class WebHookHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_key")
    private String paymentKey;

    @Column(name = "order_id")
    private String orderId;

    private Long amount;

    private String status;

    @Column(name = "processed", nullable = false)
    private boolean processed;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    public static WebHookHistory of(String paymentKey, String orderId, Long amount, String status, boolean processed) {
        return WebHookHistory.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .status(status)
                .processed(processed)
                .createdTime(LocalDateTime.now())
                .build();
    }
}
