package core.api.controller.keep;

import core.api.common.resolver.annotation.Id;
import core.api.response.ApiResponse;
import core.service.keep.KeepService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class KeepController {
    private final KeepService keepService;

    @PostMapping("/api/destination/{contentId}/create")
    public ResponseEntity<ApiResponse> createKeep(@PathVariable String contentId, @Id Long id) {
        return ResponseEntity.ok(keepService.createKeep(contentId, id));
    }

    @PostMapping("/api/destination/{contentId}/delete")
    public ResponseEntity<ApiResponse> deleteKeep(@PathVariable String contentId, @Id Long id) {
        return ResponseEntity.ok(keepService.deleteKeep(contentId, id));
    }

    @GetMapping("/api/destination/keeps")
    public ResponseEntity<ApiResponse> keeps(@Id Long id,
                                              @RequestParam(defaultValue = "1") Integer pageNum,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(keepService.keeps(id, pageNum, pageSize));
    }

    @GetMapping("/api/destination/{contentId}/keep")
    public ResponseEntity<ApiResponse> detailKeep(@PathVariable String contentId, @Id Long id) {
        return ResponseEntity.ok(keepService.detailKeep(contentId, id));
    }
}
