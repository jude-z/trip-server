package com.tripplannerai.controller.festival;

import com.tripplannerai.common.annotation.Id;
import com.tripplannerai.dto.request.festival.FestivalBookmarkRequest;
import com.tripplannerai.dto.response.festival.FestivalLikeResponse;
import com.tripplannerai.service.festival.FestivalBookmarkService;
import com.tripplannerai.util.ConstClass;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/festival")
public class FestivalController {

    private final FestivalBookmarkService festivalBookmarkService;

    @PostMapping("/favorite")
    public ResponseEntity<FestivalLikeResponse> addFestivalBookmark(@Id Long userId, @RequestBody FestivalBookmarkRequest request) {
        festivalBookmarkService.addBookmark(userId, request.getContentId());
        return ResponseEntity.ok(FestivalLikeResponse.of(ConstClass.SUCCESS_CODE, ConstClass.SUCCESS_MESSAGE));
    }

    @DeleteMapping("/favorite")
    public ResponseEntity<FestivalLikeResponse> removeFestivalBookmark(@Id Long userId, @RequestParam String contentId) {
        festivalBookmarkService.removeBookmark(userId, contentId);
        return ResponseEntity.ok(FestivalLikeResponse.of(ConstClass.SUCCESS_CODE, ConstClass.SUCCESS_MESSAGE));
    }

    @GetMapping("/favorites-count")
    public ResponseEntity<Long> getFestivalBookmarkCount(@RequestParam String contentId) {
        return ResponseEntity.ok(festivalBookmarkService.getBookmarkCount(contentId));
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<String>> getMyFestivalBookmarkIds(@Id Long userId) {
        return ResponseEntity.ok(festivalBookmarkService.getBookmarksByUser(userId));
    }
}
