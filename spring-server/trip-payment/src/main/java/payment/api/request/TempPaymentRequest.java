package payment.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TempPaymentRequest {

    private String paymentKey;
    private String orderId;
    private Long amount;
}
