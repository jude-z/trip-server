package core.api.controller.destination;

import core.api.common.resolver.annotation.Id;
import core.api.request.destination.DestinationSearchRequest;
import core.api.response.ApiResponse;
import core.service.destination.DestinationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/destination")
@RequiredArgsConstructor
public class DestinationController {

    private final DestinationService destinationService;

    @GetMapping("/{destinationId}")
    public ResponseEntity<ApiResponse> fetchDestination(@PathVariable Long destinationId, @Id Long id) {
        return ResponseEntity.ok(destinationService.fetchDestination(destinationId, id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> fetchDestinations(@RequestParam Integer page, @RequestParam Integer size,
                                                          @RequestParam String name, @Id Long id) {
        return ResponseEntity.ok(destinationService.fetchDestinations(page, size, name, id));
    }

    @GetMapping("/category")
    public ResponseEntity<ApiResponse> fetchDestinationsByCategory(@RequestParam Integer page, @RequestParam Integer size, Long categoryId) {
        return ResponseEntity.ok(destinationService.fetchDestinationByCategory(page, size, categoryId));
    }

    @GetMapping("/total")
    public ResponseEntity<ApiResponse> fetchDestinationsByTotal() {
        return ResponseEntity.ok(destinationService.fetchDestinationsByTotal());
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse> fetchMyDestinations(@RequestParam Integer page, @RequestParam Integer size,
                                                            @Id Long id) {
        return ResponseEntity.ok(destinationService.fetchMyDestinations(page, size, id));
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse> getDestinationList(@RequestBody DestinationSearchRequest destinationSearchRequest) {
        return ResponseEntity.ok(destinationService.getDestinationsByContentIds(destinationSearchRequest.getContentIdList()));
    }
}
