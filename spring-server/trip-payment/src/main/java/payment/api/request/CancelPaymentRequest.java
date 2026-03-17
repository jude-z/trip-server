package payment.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelPaymentRequest {
    private String paymentKey;
    private String orderId;
    private Long amount;
    private String cancelReason;
}
