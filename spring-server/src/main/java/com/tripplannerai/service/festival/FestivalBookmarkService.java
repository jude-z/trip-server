package com.tripplannerai.service.festival;

import com.tripplannerai.entity.festival.Festival;

import java.util.List;

public interface FestivalBookmarkService {
    void addBookmark(Long userId, String contentId);
    void removeBookmark(Long userId, String contentId);
    List<String> getBookmarksByUser(Long userId);
    Long getBookmarkCount(String contentId);
}
