package core.api.controller.festival;

import core.api.common.resolver.annotation.Id;
import core.api.request.festival.FestivalBookmarkRequest;
import core.api.response.ApiDataResponse;
import core.api.response.ApiResponse;
import core.api.response.ApiStatusResponse;
import core.common.Status;
import core.service.festival.FestivalBookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/festival")
public class FestivalController {

    private final FestivalBookmarkService festivalBookmarkService;

    @PostMapping("/favorite")
    public ResponseEntity<ApiResponse> addFestivalBookmark(@Id Long userId, @RequestBody FestivalBookmarkRequest request) {
        festivalBookmarkService.addBookmark(userId, request.getContentId());
        return ResponseEntity.ok(ApiStatusResponse.of(Status.SUCCESS));
    }

    @DeleteMapping("/favorite")
    public ResponseEntity<ApiResponse> removeFestivalBookmark(@Id Long userId, @RequestParam String contentId) {
        festivalBookmarkService.removeBookmark(userId, contentId);
        return ResponseEntity.ok(ApiStatusResponse.of(Status.SUCCESS));
    }

    @GetMapping("/favorites-count")
    public ResponseEntity<ApiResponse> getFestivalBookmarkCount(@RequestParam String contentId) {
        return ResponseEntity.ok(ApiDataResponse.of(festivalBookmarkService.getBookmarkCount(contentId), Status.SUCCESS));
    }

    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse> getMyFestivalBookmarkIds(@Id Long userId) {
        return ResponseEntity.ok(ApiDataResponse.of(festivalBookmarkService.getBookmarksByUser(userId), Status.SUCCESS));
    }
}
