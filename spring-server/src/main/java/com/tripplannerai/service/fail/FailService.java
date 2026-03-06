package com.tripplannerai.service.fail;

import com.tripplannerai.dto.request.member.EmailCertificationRequest;
import com.tripplannerai.dto.response.fail.CloseResponse;
import com.tripplannerai.dto.response.fail.FailResponse;
import com.tripplannerai.common.emitter.EmitterRepositoryImpl;
import com.tripplannerai.entity.fail.Fail;
import com.tripplannerai.common.exception.fail.SseSendException;
import com.tripplannerai.repository.fail.FailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import static com.tripplannerai.util.ConstClass.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FailService {

    private final FailRepository failRepository;
    private final EmitterRepositoryImpl emitterRepository;
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    public SseEmitter subscribe(String clientId, String lastEventId) {
        SseEmitter emitter = emitterRepository.save(clientId, new SseEmitter(DEFAULT_TIMEOUT));
        emitter.onCompletion(() -> {
            emitterRepository.deleteById(clientId);
        });
        emitter.onTimeout(() -> emitterRepository.deleteById(clientId));
        emitter.onError((e) -> {
            log.error(e.getMessage(),e);
            emitterRepository.deleteById(clientId);
        });
        sendToClient(emitter, clientId, "EventStream Created. [clientId=" + clientId + "]");
        return emitter;
    }

    public void send(EmailCertificationRequest emailCertificationRequest){
        String clientId = emailCertificationRequest.getClientId();
        String email = emailCertificationRequest.getEmail();
        SseEmitter sseEmitter = emitterRepository.findById(clientId);
        Fail fail = Fail.of(clientId,"fail send Mail");
        FailResponse failResponse = FailResponse.of(NOT_VALID_EMAIL_CODE, NOT_VALID_EMAIL_MESSAGE, clientId, email);
        sendToClient(sseEmitter,clientId,failResponse);
        failRepository.save(fail);
    }

    private void sendToClient(SseEmitter emitter, String clientId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .name("sse")
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(clientId);
            throw new SseSendException("sse send fail");
        }
    }

    public CloseResponse close(String clientId) {
        SseEmitter findEmitter = emitterRepository.findById(clientId);
        findEmitter.complete();
        return CloseResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }
}
