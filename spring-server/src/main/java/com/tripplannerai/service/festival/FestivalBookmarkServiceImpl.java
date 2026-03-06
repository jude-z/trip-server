package com.tripplannerai.service.festival;

import com.tripplannerai.entity.festival.Festival;
import com.tripplannerai.repository.festival.FestivalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FestivalBookmarkServiceImpl implements FestivalBookmarkService{
    private final FestivalRepository festivalRepository;
    @Override
    public void addBookmark(Long userId, String contentId) {
        if(!festivalRepository.existsByUserIdAndContentId(userId, contentId)) {
            festivalRepository.save(Festival.builder().userId(userId).contentId(contentId).build());
        }
    }

    @Override
    public void removeBookmark(Long userId, String contentId) {
        festivalRepository.findByUserIdAndContentId(userId, contentId)
                .ifPresent(festivalRepository::delete);
    }

    @Override
    public List<String> getBookmarksByUser(Long userId) {
        List<Festival> bookmarks = festivalRepository.findAllByUserId(userId);
        return bookmarks.stream()
                .map(Festival::getContentId)
                .toList();
    }

    @Override
    public Long getBookmarkCount(String contentId) {
        return festivalRepository.countByContentId(contentId);
    }
}
