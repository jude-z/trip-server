package core.service.festival;

import core.domain.entity.festival.Festival;
import core.infra.jpa.festival.FestivalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FestivalBookmarkService {
    private final FestivalRepository festivalRepository;
    public void addBookmark(Long userId, String contentId) {
        if(!festivalRepository.existsByUserIdAndContentId(userId, contentId)) {
            festivalRepository.save(Festival.builder().userId(userId).contentId(contentId).build());
        }
    }

    public void removeBookmark(Long userId, String contentId) {
        festivalRepository.findByUserIdAndContentId(userId, contentId)
                .ifPresent(festivalRepository::delete);
    }

    public List<String> getBookmarksByUser(Long userId) {
        List<Festival> bookmarks = festivalRepository.findAllByUserId(userId);
        return bookmarks.stream()
                .map(Festival::getContentId)
                .toList();
    }

    public Long getBookmarkCount(String contentId) {
        return festivalRepository.countByContentId(contentId);
    }
}
