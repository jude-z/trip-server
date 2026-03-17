package core.api.controller.kindplace;

import core.api.response.ApiResponse;
import core.service.kindplace.KindPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KindPlaceController {
    private final KindPlaceService kindPlaceService;

    @GetMapping("/kindplace")
    public ResponseEntity<ApiResponse> fetchKindPlaces(@RequestParam String address, @RequestParam Integer pageNum) {
        return ResponseEntity.ok(kindPlaceService.fetchKindPlaces(address, pageNum));
    }
}
