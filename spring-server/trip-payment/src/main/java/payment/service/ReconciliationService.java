package payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import core.domain.entity.member.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import payment.domain.mapper.PaymentFactory;
import payment.domain.pay.payment.Payment;
import payment.domain.pay.payment.TempPayment;
import payment.infra.jpa.payment.PaymentRepository;
import payment.infra.querydsl.payment.QueryDslTempPaymentRepository;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReconciliationService {

    private final QueryDslTempPaymentRepository queryDslTempPaymentRepository;
    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;
    private final String url;
    private final String apiSecretKey;


    public ReconciliationService(PaymentRepository paymentRepository,
                                 QueryDslTempPaymentRepository queryDslTempPaymentRepository,
                                 @Value("${payment.api.url}") String url,
                                 @Value("${payment.api.secretKey}") String apiSecretKey) {
        this.paymentRepository = paymentRepository;
        this.queryDslTempPaymentRepository = queryDslTempPaymentRepository;
        this.restTemplate = new RestTemplate();
        this.url = url;
        this.apiSecretKey = apiSecretKey;
    }

    @Scheduled(scheduler = "reconciliationScheduler",cron = "10/* * * * *")
    public void reconciliation(){
        List<TempPayment> tempPayments = queryDslTempPaymentRepository.fetchPendingPayments();
        List<String> paymentKeys = tempPayments.stream()
                .map(TempPayment::getPaymentKey)
                .toList();

        List<Payment> existPayments = paymentRepository.findByPaymentKeyIn(paymentKeys);

        Set<String> existKeys = existPayments.stream()
                .map(Payment::getPaymentKey)
                .collect(Collectors.toSet());

        List<TempPayment> filteredPaymentKeys = tempPayments.stream()
                .filter(paymentKey -> !existKeys.contains(paymentKey))
                .toList();

        List<Payment> payments = new ArrayList<>();
        filteredPaymentKeys
                .forEach(tempPayment -> {
                    ResponseEntity<JsonNode> jsonNodeResponseEntity = restTemplate.exchange(url + tempPayment.getPaymentKey(),
                            HttpMethod.GET,
                            new HttpEntity<>(getHeaders()),
                            JsonNode.class);
                    if(jsonNodeResponseEntity.getStatusCode() == HttpStatus.OK){
                        JsonNode jsonNode = jsonNodeResponseEntity.getBody();
                        String paymentKey = jsonNode.get("paymentKey").asText();
                        String orderId = jsonNode.get("orderId").asText();
                        long amount = Long.parseLong(jsonNode.get("totalAmount").asText());
                        Member member = tempPayment.getMember();
                        payments.add(PaymentFactory.from(paymentKey,orderId,amount,member));
                    }
                });
        paymentRepository.saveAll(payments);


    }
    private HttpHeaders getHeaders(){
        String encodedAuth = Base64.getEncoder().encodeToString(apiSecretKey.getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + encodedAuth);
        return headers;
    }

}
