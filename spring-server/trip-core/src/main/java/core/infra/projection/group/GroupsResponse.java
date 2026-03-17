package core.infra.projection.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupsResponse {
    private String code;
    private String message;
    private List<GroupElement> groups;
    private boolean hasNext;

    public static GroupsResponse of(String code, String message, List<GroupElement> content, boolean hasNext) {
        return new  GroupsResponse(code,message,content,hasNext);
    }
}
