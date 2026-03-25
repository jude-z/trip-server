package payment.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import payment.api.request.PaymentRequest;
import payment.api.request.CancelPaymentRequest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Slf4j
public class PaymentClient {
    private final RestTemplate restTemplate;
    @Value("${toss.widget.secretKey}")
    private String widgetSecretKey;
    @Value("${toss.server.url}")
    private String url;
    @Value("${toss.cancel.url}")
    private String cancelUrl;

    public PaymentClient(){
        this.restTemplate = new RestTemplate();
    }

    public boolean confirm(PaymentRequest paymentRequest){
        try {
            HttpHeaders headers = getHeaders();
            HttpEntity<PaymentRequest> requestEntity = new HttpEntity<>(paymentRequest, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            HttpStatusCode responseCode = response.getStatusCode();
            return responseCode == HttpStatus.OK;
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return false;
        }
    }
    public boolean cancel(CancelPaymentRequest cancelPaymentRequest){

        try {
            String paymentKey = cancelPaymentRequest.getPaymentKey();
            HttpHeaders headers = getHeaders();
            HttpEntity<CancelPaymentRequest> reasonHttpEntity = new HttpEntity<>(cancelPaymentRequest, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(cancelUrl.formatted(paymentKey), reasonHttpEntity, String.class);
            HttpStatusCode responseCode = response.getStatusCode();
            return responseCode == HttpStatus.OK;
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return false;
        }
    }
    public boolean checkPaymentStatus(String paymentKey){
        try {
            String checkUrl = url.replace("/confirm", "/" + paymentKey);
            HttpHeaders headers = getHeaders();
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(checkUrl, HttpMethod.GET, requestEntity, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return false;
        }
    }

    private HttpHeaders getHeaders(){
        String auth = widgetSecretKey + ":";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + encodedAuth);
        return headers;
    }
}
