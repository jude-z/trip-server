package core.infra.projection.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupResponse {
    private String code;
    private String message;
    private Long groupId;
    private String title;
    private String description;
    private int count;
    private int participateCount;
    private int groupLikeCount;
    private int maxCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long memberId;

    public static GroupResponse of(String code, String message, GroupElement groupElement) {
        return GroupResponse.builder()
                .code(code)
                .message(message)
                .groupId(groupElement.getGroupId())
                .title(groupElement.getTitle())
                .description(groupElement.getDescription())
                .count(groupElement.getCount())
                .participateCount(groupElement.getParticipateCount())
                .groupLikeCount(groupElement.getGroupLikeCount())
                .maxCount(groupElement.getMaxCount())
                .startDate(groupElement.getStartDate())
                .endDate(groupElement.getEndDate())
                .memberId(groupElement.getMemberId())
                .build();
    }
}
