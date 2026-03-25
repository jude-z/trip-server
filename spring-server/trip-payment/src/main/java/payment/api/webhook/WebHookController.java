package payment.api.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payment.service.WebHookService;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class WebHookController {
    private final WebHookService webHookService;
    @PostMapping("/webhook")
    public ResponseEntity<Void> processWebHook(
                                               @RequestBody JsonNode jsonNode){

        webHookService.processWebHook(jsonNode);
        return ResponseEntity.ok().build();
    }
}
