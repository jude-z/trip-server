package payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.domain.entity.point.Point;
import core.infra.jpa.point.PointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import payment.infra.jpa.idempotency.IdempotencyRepository;
import payment.infra.jpa.payment.PaymentRepository;
import payment.infra.jpa.payment.TempPaymentRepository;
import payment.infra.jpa.webhook.WebHookHistoryRepository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
class PaymentIdempotencyTest {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String AUTH_URL = "http://localhost:8080";
    private final String PAYMENT_URL = "http://localhost:8081";
    private final String MOCK_URL = "http://localhost:8082";

    @Autowired
    private TempPaymentRepository tempPaymentRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private IdempotencyRepository idempotencyRepository;
    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private WebHookHistoryRepository webHookHistoryRepository;
    @Autowired
    RedisTemplate<String,String> redisTemplate;

    private volatile String token;
    private Long memberId;

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }

    private long getPoint() {
        return pointRepository.findByMemberId(memberId)
                .map(Point::getAmount).get();
    }

    private long getMockPaymentCount(String paymentKey) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    MOCK_URL + "/v1/paymentCount/" + paymentKey, String.class);
            return Long.parseLong(response.getBody());
        } catch (Exception e) {
            System.out.println("[mock 조회 실패] " + e.getMessage());
            return -1;
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
        paymentRepository.deleteAll();
        tempPaymentRepository.deleteAll();
        webHookHistoryRepository.deleteAll();
        idempotencyRepository.deleteAll();
        System.out.println("=== 데이터 정리 완료 ===");

        // 로그인
        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);
        String loginBody = """
                {
                    "email": "user1@gmail.com",
                    "password": "1234"
                }
                """;
        HttpEntity<String> loginRequest = new HttpEntity<>(loginBody, loginHeaders);
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                AUTH_URL + "/auth/login", loginRequest, String.class);
        JsonNode root = objectMapper.readTree(loginResponse.getBody());
        token = root.get("data").asText();
        System.out.println("발급된 토큰: " + token);

        // memberId 추출
        String payload = new String(java.util.Base64.getUrlDecoder().decode(token.split("\\.")[1]));
        JsonNode claims = objectMapper.readTree(payload);
        JsonNode sub = objectMapper.readTree(claims.get("sub").asText());
        memberId = sub.get("id").asLong();

        // 포인트 0으로 초기화
        pointRepository.resetPointByMemberId(memberId);
        System.out.println("포인트 초기화: " + getPoint());

        // /api/pay로 TempPayment 생성
        String payBody = """
                {
                    "paymentKey": "paymentKey1",
                    "orderId": "order1",
                    "amount": 10000
                }
                """;
        HttpEntity<String> payRequest = new HttpEntity<>(payBody, createHeaders());
        restTemplate.postForEntity(PAYMENT_URL + "/api/pay", payRequest, String.class);
        System.out.println("TempPayment 생성 완료");
    }
    @Test
    void test(){
        System.out.println(1);
    }

    @Test
    @DisplayName("V1: 멱등성 없음 - 동시 100건 요청 시 중복 결제 발생")
    void v1_noIdempotency_concurrentRequests() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        String body = """
                {
                    "paymentKey": "paymentKey1",
                    "orderId": "order1",
                    "amount": 10000
                }
                """;

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setBearerAuth(token);
                    HttpEntity<String> request = new HttpEntity<>(body, headers);
                    ResponseEntity<String> response = restTemplate.postForEntity(
                            PAYMENT_URL + "/api/v1/confirm", request, String.class);
                    System.out.println("[SUCCESS] " + response.getBody());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("[FAIL] " + e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        long mockCount = getMockPaymentCount("paymentKey1");

        System.out.println("=== V1 멱등성 없음 테스트 결과 ===");
        System.out.println("성공: " + successCount.get());
        System.out.println("실패: " + failCount.get());
        System.out.println("총 요청: " + threadCount);
        System.out.println("외부 PG 결제 요청 횟수: " + mockCount + " (1건 초과 시 중복 결제)");
    }

    @Test
    @DisplayName("V2: Redis 정상 - 동일 멱등성 키로 동시 100건 요청")
    void v2_redisNormal_concurrentRequests() throws InterruptedException {
        String idempotencyKey = "paymentKey1";

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        String body = """
                {
                    "paymentKey": "paymentKey1",
                    "orderId": "order1",
                    "amount": 10000
                }
                """;

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setBearerAuth(token);
                    headers.set("Idempotency-Key", idempotencyKey);
                    HttpEntity<String> request = new HttpEntity<>(body, headers);
                    ResponseEntity<String> response = restTemplate.postForEntity(
                            PAYMENT_URL + "/api/v2/confirm", request, String.class);
                    System.out.println("[SUCCESS] " + response.getBody());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("[FAIL] " + e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        long mockCount = getMockPaymentCount("paymentKey1");

        System.out.println("=== V2 Redis 정상 테스트 결과 ===");
        System.out.println("성공: " + successCount.get());
        System.out.println("실패: " + failCount.get());
        System.out.println("총 요청: " + threadCount);
        System.out.println("외부 PG 결제 요청 횟수: " + mockCount + " (1건 = 멱등성 보장)");
    }

    @Test
    @DisplayName("V2: Redis 장애 시 멱등성 뚫림 - 50건 → 10초 대기(Redis 내리기) → 50건")
    void v2_redisFail_concurrentRequests() throws InterruptedException {
        String idempotencyKey = "paymentKey1";

        String body = """
                {
                    "paymentKey": "paymentKey1",
                    "orderId": "order1",
                    "amount": 10000
                }
                """;

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // 1차: 50건 동시 요청 (Redis 정상 상태)
        int firstBatch = 50;
        ExecutorService executor1 = Executors.newFixedThreadPool(firstBatch);
        CountDownLatch latch1 = new CountDownLatch(firstBatch);

        for (int i = 0; i < firstBatch; i++) {
            executor1.submit(() -> {
                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setBearerAuth(token);
                    headers.set("Idempotency-Key", idempotencyKey);
                    HttpEntity<String> request = new HttpEntity<>(body, headers);
                    ResponseEntity<String> response = restTemplate.postForEntity(
                            PAYMENT_URL + "/api/v2/confirm", request, String.class);
                    System.out.println("[1차 SUCCESS] " + response.getBody());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("[1차 FAIL] " + e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    latch1.countDown();
                }
            });
        }

        latch1.await();
        executor1.shutdown();

        System.out.println("=== 1차 결과 (Redis 정상) ===");
        System.out.println("성공: " + successCount.get());
        System.out.println("실패: " + failCount.get());

        // 10초 대기 - 이 사이에 Redis를 내리면 됨
        System.out.println(">>> 10초 대기 - 지금 Redis를 내리세요! (sudo systemctl stop redis / docker stop redis) <<<");
        Thread.sleep(10000);

        // 2차: 50건 동시 요청 (Redis 장애 상태)
        AtomicInteger successCount2 = new AtomicInteger(0);
        AtomicInteger failCount2 = new AtomicInteger(0);
        int secondBatch = 50;
        ExecutorService executor2 = Executors.newFixedThreadPool(secondBatch);
        CountDownLatch latch2 = new CountDownLatch(secondBatch);

        for (int i = 0; i < secondBatch; i++) {
            executor2.submit(() -> {
                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setBearerAuth(token);
                    headers.set("Idempotency-Key", idempotencyKey);
                    HttpEntity<String> request = new HttpEntity<>(body, headers);
                    ResponseEntity<String> response = restTemplate.postForEntity(
                            PAYMENT_URL + "/api/v2/confirm", request, String.class);
                    System.out.println("[2차 SUCCESS] " + response.getBody());
                    successCount2.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("[2차 FAIL] " + e.getMessage());
                    failCount2.incrementAndGet();
                } finally {
                    latch2.countDown();
                }
            });
        }

        latch2.await();
        executor2.shutdown();

        long mockCount = getMockPaymentCount("paymentKey1");

        System.out.println("=== V2 최종 결과 ===");
        System.out.println("1차(Redis 정상) 성공: " + successCount.get() + ", 실패: " + failCount.get());
        System.out.println("2차(Redis 장애) 성공: " + successCount2.get() + ", 실패: " + failCount2.get());
        System.out.println("외부 PG 결제 요청 횟수: " + mockCount + " (1건 초과 시 멱등성 뚫림)");
    }

    @Test
    @DisplayName("V3: DB 저장 실패 시나리오 - 결제 승인 후 DB 저장 실패, 15분 뒤 mock서버 웹훅으로 보완")
    void v3_dbFail_webhookRecovery() throws Exception {
        String idempotencyKey = "paymentKey1";
        String body = """
                {
                    "paymentKey": "paymentKey1",
                    "orderId": "order1",
                    "amount": 10000
                }
                """;

        // 1) v3/confirm 요청 → 결제 승인되지만 DB에 Payment 저장 안 됨
        HttpHeaders headers = createHeaders();
        headers.set("Idempotency-Key", idempotencyKey);
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    PAYMENT_URL + "/api/v3/confirm", request, String.class);
            System.out.println("[V3 confirm] " + response.getBody());
        } catch (Exception e) {
            System.out.println("[V3 confirm FAIL] " + e.getMessage());
        }

        // 2) DB에 Payment 저장 안 됐는지, 포인트 변동 없는지 확인
        long paymentCount = paymentRepository.countByPaymentKey("paymentKey1");
        long pointAfterConfirm = getPoint();
        System.out.println("confirm 후 DB Payment 수 (0건 예상): " + paymentCount);
        System.out.println("confirm 후 포인트 (0 예상): " + pointAfterConfirm);

        // 3) mock서버가 웹훅을 보내는 것을 기다림
        System.out.println(">>> mock서버 웹훅 대기 중 (15분)... <<<");
        int waitMinutes = 15;
        for (int i = 1; i <= waitMinutes; i++) {
            Thread.sleep(60000);
            long current = paymentRepository.countByPaymentKey("paymentKey1");
            long currentPoint = getPoint();
            System.out.println("[" + i + "분 경과] DB Payment 수: " + current + ", 포인트: " + currentPoint);
            if (current > 0) {
                System.out.println(">>> 웹훅 수신 확인! <<<");
                break;
            }
        }

        // 4) 웹훅 후 DB 결과 확인
        long paymentCountAfterWebhook = paymentRepository.countByPaymentKey("paymentKey1");
        long pointAfterWebhook = getPoint();

        System.out.println("=== V3 테스트 결과 ===");
        System.out.println("confirm 후 DB 저장: " + paymentCount + "건 (0건 = DB 저장 실패)");
        System.out.println("confirm 후 포인트: " + pointAfterConfirm + " (0 = 미반영)");
        System.out.println("웹훅 보완 후 DB 저장: " + paymentCountAfterWebhook + "건 (1건 = 웹훅 복구)");
        System.out.println("웹훅 보완 후 포인트: " + pointAfterWebhook + " (10000 = 정상 반영)");
    }

    @Test
    @DisplayName("V4: DB 저장 실패 + 웹훅 실패 - 대사(Reconciliation)로 복구 확인")
    void v4_dbFail_webhookFail_reconciliationRecovery() throws Exception {
        String idempotencyKey = "paymentKey1";
        String body = """
                {
                    "paymentKey": "paymentKey1",
                    "orderId": "order1",
                    "amount": 10000
                }
                """;

        // 1) v4/confirm 요청 → mock 서버 실패 시 checkPaymentStatus로 외부 API 직접 조회
        HttpHeaders headers = createHeaders();
        headers.set("Idempotency-Key", idempotencyKey);
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    PAYMENT_URL + "/api/v4/confirm", request, String.class);
            System.out.println("[V4 confirm] " + response.getBody());
        } catch (Exception e) {
            System.out.println("[V4 confirm FAIL] " + e.getMessage());
        }

        // 2) confirm 직후 DB/포인트 확인
        long paymentCount = paymentRepository.countByPaymentKey("paymentKey1");
        long pointAfterConfirm = getPoint();
        System.out.println("confirm 후 DB Payment 수 (0건 예상): " + paymentCount);
        System.out.println("confirm 후 포인트 (0 예상): " + pointAfterConfirm);

        // 3) 대사(Reconciliation) 스케줄러가 TempPayment를 처리하는 것을 기다림
        // 대사는 1분마다 실행, 외부 API 조회 후 Payment 저장 + 포인트 반영
        System.out.println(">>> 대사(Reconciliation) 대기 중 (15분)... <<<");
        int waitMinutes = 15;
        for (int i = 1; i <= waitMinutes; i++) {
            Thread.sleep(60000);
            long current = paymentRepository.countByPaymentKey("paymentKey1");
            long currentPoint = getPoint();
            System.out.println("[" + i + "분 경과] DB Payment 수: " + current + ", 포인트: " + currentPoint);
            if (current > 0) {
                System.out.println(">>> 대사 처리 확인! <<<");
                break;
            }
        }

        // 4) 대사 후 DB 결과 확인
        long paymentCountFinal = paymentRepository.countByPaymentKey("paymentKey1");
        long pointFinal = getPoint();

        System.out.println("=== V4 테스트 결과 ===");
        System.out.println("confirm 후 DB 저장: " + paymentCount + "건 (0건 = DB 저장 실패)");
        System.out.println("confirm 후 포인트: " + pointAfterConfirm + " (0 = 미반영)");
        System.out.println("대사 후 DB 저장: " + paymentCountFinal + "건 (1건 = 대사 복구)");
        System.out.println("대사 후 포인트: " + pointFinal + " (10000 = 정상 반영)");
    }
}
