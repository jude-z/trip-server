package com.tripplannerai.service.payment;

import com.tripplannerai.common.exception.payment.NotFoundPaymentException;
import com.tripplannerai.dto.request.payment.PaymentRequest;
import com.tripplannerai.dto.request.payment.Reason;
import com.tripplannerai.dto.request.payment.TempPaymentRequest;
import com.tripplannerai.dto.response.payment.*;
import com.tripplannerai.entity.member.Member;
import com.tripplannerai.entity.pay.impotency.Impotency;
import com.tripplannerai.entity.pay.payment.Payment;
import com.tripplannerai.entity.pay.payment.TempPayment;
import com.tripplannerai.common.exception.member.NotFoundMemberException;
import com.tripplannerai.common.exception.payment.AlreadyPaymentRequestException;
import com.tripplannerai.common.exception.payment.NotFoundTempPaymentException;
import com.tripplannerai.common.exception.payment.PaymentServerErrorException;
import com.tripplannerai.mapper.PaymentFactory;
import com.tripplannerai.repository.impotency.ImpotencyRepository;
import com.tripplannerai.repository.member.MemberRepository;
import com.tripplannerai.repository.payment.PaymentRepository;
import com.tripplannerai.repository.payment.TempPaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static com.tripplannerai.util.ConstClass.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ImpotencyRepository impotencyRepository;
    private final MemberRepository memberRepository;
    private final TempPaymentRepository tempPaymentRepository;
    @Value("${toss.widget.secretKey}")
    private String widgetSecretKey;
    @Value("${toss.server.url}")
    private String url;
    @Value("${toss.cancel.url}")
    private String cancelUrl;

    public ConfirmResponse confirm(String impotencyKey, PaymentRequest paymentRequest, Long id){

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("not found member!!"));
        Optional<Impotency> optionalImpotency = impotencyRepository.findByImpotencyKey(impotencyKey);
        if (optionalImpotency.isPresent()) {
            throw new AlreadyPaymentRequestException("already request payment!!");
        }
        Impotency impotency = Impotency.of(impotencyKey);
        impotencyRepository.save(impotency);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHeaders();
        HttpEntity<PaymentRequest> requestEntity = new HttpEntity<>(paymentRequest, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                paymentRepository.save(PaymentFactory.from(paymentRequest, member));
                memberRepository.updateTicket(paymentRequest.getAmount() / 200, id);
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
            throw new PaymentServerErrorException("payment Server error!!");
        }

        return ConfirmResponse.of(SUCCESS_CODE, SUCCESS_MESSAGE);
    }
    public SaveTempResponse saveTemp(TempPaymentRequest tempPaymentRequest, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("not found member!"));
        TempPayment tempPayment = PaymentFactory.from(tempPaymentRequest, member);
        tempPaymentRepository.save(tempPayment);
        return SaveTempResponse.of(SUCCESS_CODE, SUCCESS_MESSAGE);
    }

    public CheckTempResponse checkTemp(TempPaymentRequest tempPaymentRequest, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("not found member!"));
        String orderId = tempPaymentRequest.getOrderId();
        Integer amount = tempPaymentRequest.getAmount();
        tempPaymentRepository.findByOrderIdAndAmountAndMember(orderId, amount, member)
                .orElseThrow(() -> new NotFoundTempPaymentException("not found tempPayment!!"));
        return CheckTempResponse.of(SUCCESS_CODE, SUCCESS_MESSAGE);
    }

    private HttpHeaders getHeaders(){
        String auth = widgetSecretKey + ":";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + encodedAuth);
        return headers;
    }

    public CancelResponse cancel(String impotencyKey, Long id, Reason reason,String paymentKey) {
        memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("not found member!!"));
        Optional<Impotency> optionalImpotency = impotencyRepository.findByImpotencyKey(impotencyKey);
        if (optionalImpotency.isPresent()) {
            throw new AlreadyPaymentRequestException("already request payment!!");
        }
        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new NotFoundPaymentException("Not Found Payment"));
        Impotency impotency = Impotency.of(impotencyKey);
        impotencyRepository.save(impotency);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHeaders();
        HttpEntity<Reason> reasonHttpEntity = new HttpEntity<>(reason, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(cancelUrl.formatted(paymentKey), reasonHttpEntity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                payment.changeCancelled();
                memberRepository.updateTicket((-1)*payment.getAmount(),id);
            }
        }catch (Exception e){
            throw new PaymentServerErrorException("payment Server error!!");
        }
        return CancelResponse.of(SUCCESS_CODE, SUCCESS_MESSAGE);
    }

    public FetchPaymentsResponse fetchPayments(Long id, Integer pageNum) {
        memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("not found member!!"));
        PageRequest pageRequest = PageRequest.of(pageNum - 1, 10);
        Page<PaymentElement> page = paymentRepository.fetchPayments(pageRequest, id);
        boolean hasNext = page.hasNext();
        List<PaymentElement> content = page.getContent();
        return FetchPaymentsResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,content,hasNext);
    }
}