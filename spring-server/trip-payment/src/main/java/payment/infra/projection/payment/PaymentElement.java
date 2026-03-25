package payment.infra.projection.payment;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import payment.domain.pay.status.PaymentStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentElement {
    private Long id;
    private String paymentKey;
    private Long amount;
    private String orderId;
    private PaymentStatus status;
}
