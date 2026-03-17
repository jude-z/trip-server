package payment.api.controller;

import payment.api.common.resolver.annotation.Id;
import payment.api.request.PaymentRequest;
import payment.api.request.CancelPaymentRequest;
import payment.api.request.TempPaymentRequest;
import payment.api.response.ApiResponse;
import payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/api/pay")
    public ResponseEntity<ApiResponse> pay(@RequestBody TempPaymentRequest tempPaymentRequest, @Id Long id){
        ApiResponse apiResponse = paymentService.pay(tempPaymentRequest,id);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/api/confirm")
    public ResponseEntity<ApiResponse> confirm(@RequestHeader("Idempotency-Key") String idempotencyKey,
                                                @RequestBody PaymentRequest paymentRequest, @Id Long id) {
        return ResponseEntity.ok(paymentService.confirm(idempotencyKey, paymentRequest, id));
    }

    @PostMapping("/api/cancel")
    public ResponseEntity<ApiResponse> cancel(@RequestHeader("Idempotency-Key") String idempotencyKey,
                                              @RequestBody CancelPaymentRequest cancelPaymentRequest, @Id Long id) {
        return ResponseEntity.ok(paymentService.cancel(idempotencyKey,cancelPaymentRequest,id));
    }

    @GetMapping("/api/payments")
    public ResponseEntity<ApiResponse> fetchPayments(@Id Long id, @RequestParam Integer pageNum) {
        return ResponseEntity.ok(paymentService.fetchPayments(id, pageNum));
    }


}
