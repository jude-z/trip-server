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
public class GroupElement {
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
}
