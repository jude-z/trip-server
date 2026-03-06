package com.tripplannerai.service;

import com.tripplannerai.dto.request.member.EmailCertificationRequest;
import com.tripplannerai.common.email.EmailProvider;
import com.tripplannerai.service.fail.FailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class AsyncService {
    private final EmailProvider emailProvider;
    private final FailService failService;

    @Async("customAsyncExecutor")
    public void asyncTask(EmailCertificationRequest emailCertificationRequest){
        try {
            emailCertification(emailCertificationRequest);
        }catch (MessagingException e){

            log.error(e.getMessage(),e);
            failService.send(emailCertificationRequest);
        }
    }

    private void emailCertification(EmailCertificationRequest emailCertificationRequest) throws MessagingException {
        emailProvider.sendCertificationMail(emailCertificationRequest.getEmail(), certificationNumber());
    }

    private String certificationNumber(){
        int number = (int) (Math.random() * 9000) + 1000;
        return String.valueOf(number);
    }

}
