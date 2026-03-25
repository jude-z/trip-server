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

    // v1: 멱등성 보장 없음 (Redis, DB 체크 없이 바로 결제)
    @PostMapping("/api/v1/confirm")
    public ResponseEntity<ApiResponse> confirmV1(@RequestBody PaymentRequest paymentRequest, @Id Long id) {
        return ResponseEntity.ok(paymentService.confirmNoIdempotency(paymentRequest, id));
    }

    // v2: Redis 장애 시뮬레이션 (Redis 스킵, DB만 체크 → 동시 요청 시 멱등성 뚫림)
    @PostMapping("/api/v2/confirm")
    public ResponseEntity<ApiResponse> confirmV2(@RequestHeader("Idempotency-Key") String idempotencyKey,
                                                  @RequestBody PaymentRequest paymentRequest, @Id Long id) {
        return ResponseEntity.ok(paymentService.confirmRedisFail(idempotencyKey, paymentRequest, id));
    }

    // v3: Redis 체크만, DB 저장 안함 (Redis 만료 후 웹훅으로 보완)
    @PostMapping("/api/v3/confirm")
    public ResponseEntity<ApiResponse> confirmV3(@RequestHeader("Idempotency-Key") String idempotencyKey,
                                                  @RequestBody PaymentRequest paymentRequest, @Id Long id) {
        return ResponseEntity.ok(paymentService.confirmNoDbSave(idempotencyKey, paymentRequest, id));
    }

    // v4: Redis+DB 멱등성 사용, 외부 서버 오류 시 외부 API 조회로 결제 상태 확인
    @PostMapping("/api/v4/confirm")
    public ResponseEntity<ApiResponse> confirmV4(@RequestHeader("Idempotency-Key") String idempotencyKey,
                                                  @RequestBody PaymentRequest paymentRequest, @Id Long id) {
        return ResponseEntity.ok(paymentService.confirmExternalCheck(idempotencyKey, paymentRequest, id));
    }

    @GetMapping("/api/payments")
    public ResponseEntity<ApiResponse> fetchPayments(@Id Long id, @RequestParam Integer pageNum) {
        return ResponseEntity.ok(paymentService.fetchPayments(id, pageNum));
    }


}
