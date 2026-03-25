package payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import core.domain.entity.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import core.common.Status;
import core.common.exception.CommonException;
import core.domain.entity.point.Point;
import core.infra.jpa.point.PointRepository;
import payment.domain.mapper.PaymentFactory;
import payment.domain.pay.payment.Payment;
import payment.domain.pay.payment.TempPayment;
import payment.infra.jpa.payment.PaymentRepository;
import payment.infra.jpa.payment.TempPaymentRepository;
import payment.infra.querydsl.payment.QueryDslTempPaymentRepository;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReconciliationService {

    private final QueryDslTempPaymentRepository queryDslTempPaymentRepository;
    private final TempPaymentRepository tempPaymentRepository;
    private final PaymentRepository paymentRepository;
    private final PointRepository pointRepository;
    private final RestTemplate restTemplate;
    private final String url;
    private final String apiSecretKey;


    public ReconciliationService(PaymentRepository paymentRepository,
                                 QueryDslTempPaymentRepository queryDslTempPaymentRepository,
                                 TempPaymentRepository tempPaymentRepository,
                                 PointRepository pointRepository,
                                 @Value("${payment.api.url}") String url,
                                 @Value("${payment.api.secretKey}") String apiSecretKey) {
        this.paymentRepository = paymentRepository;
        this.queryDslTempPaymentRepository = queryDslTempPaymentRepository;
        this.tempPaymentRepository = tempPaymentRepository;
        this.pointRepository = pointRepository;
        this.restTemplate = new RestTemplate();
        this.url = url;
        this.apiSecretKey = apiSecretKey;
    }

    @Scheduled(scheduler = "reconciliationScheduler",cron = "10 */1 * * * *")
    @Transactional
    public void reconciliation(){

        List<TempPayment> tempPayments = queryDslTempPaymentRepository.fetchPendingPayments();
        if(!tempPayments.isEmpty()) log.info("tempPayment 존재!!");
        List<String> paymentKeys = tempPayments.stream()
                .map(TempPayment::getPaymentKey)
                .toList();

        List<Payment> existPayments = paymentRepository.findByPaymentKeyIn(paymentKeys);

        Set<String> existKeys = existPayments.stream()
                .map(Payment::getPaymentKey)
                .collect(Collectors.toSet());

        // 이미 Payment가 존재하는 TempPayment는 삭제 처리
        List<String> alreadyProcessedKeys = tempPayments.stream()
                .filter(tp -> existKeys.contains(tp.getPaymentKey()))
                .map(TempPayment::getPaymentKey)
                .toList();

        List<TempPayment> filteredPaymentKeys = tempPayments.stream()
                .filter(tp -> !existKeys.contains(tp.getPaymentKey()))
                .toList();

        List<Payment> savePayments = new ArrayList<>();
        List<String> deletePaymentKeys = new ArrayList<>(alreadyProcessedKeys);
        List<String> addRetryPaymentKeys = new ArrayList<>();
        filteredPaymentKeys
                .forEach(tempPayment -> {
                    try{
                        ResponseEntity<JsonNode> jsonNodeResponseEntity = restTemplate.exchange(url + tempPayment.getPaymentKey(),
                                HttpMethod.GET,
                                new HttpEntity<>(getHeaders()),
                                JsonNode.class);
                        if(jsonNodeResponseEntity.getStatusCode() == HttpStatus.OK){
                            JsonNode jsonNode = jsonNodeResponseEntity.getBody();
                            JsonNode data = jsonNode.get("data");
                            String paymentKey = data.get("paymentKey").asText();
                            String orderId = data.get("orderId").asText();
                            long amount = Long.parseLong(data.get("totalAmount").asText());
                            Member member = tempPayment.getMember();
                            savePayments.add(PaymentFactory.from(paymentKey,orderId,amount,member));
                            deletePaymentKeys.add(paymentKey);
                            Point point = pointRepository.findByMember(member)
                                    .orElseThrow(() -> new CommonException(Status.NOT_FOUND_POINT));
                            point.addAmount(amount);
                        }else{
                            String paymentKey = tempPayment.getPaymentKey();
                            addRetryPaymentKeys.add(paymentKey);
                        }
                    }catch (Exception e){
                        log.error("error",e);
                        String paymentKey = tempPayment.getPaymentKey();
                        addRetryPaymentKeys.add(paymentKey);
                    }
                });
        paymentRepository.saveAll(savePayments);
        tempPaymentRepository.deleteByPaymentKeyInBatch(deletePaymentKeys);
        tempPaymentRepository.addRetry(addRetryPaymentKeys);




    }
    private HttpHeaders getHeaders(){
        String encodedAuth = Base64.getEncoder().encodeToString(apiSecretKey.getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + encodedAuth);
        return headers;
    }

}
