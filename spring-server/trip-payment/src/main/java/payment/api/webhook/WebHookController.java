package payment.api.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import payment.service.WebHookService;

@RestController("/payment")
@RequiredArgsConstructor
public class WebHookController {
    private final WebHookService webHookService;
    @PostMapping("/webhook")
    public ResponseEntity<Void> processWebHook(@RequestBody JsonNode jsonNode){

        webHookService.processWebHook(jsonNode);
        return ResponseEntity.ok().build();
    }
}
