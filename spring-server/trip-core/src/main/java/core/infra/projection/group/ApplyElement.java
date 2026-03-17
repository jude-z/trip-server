package core.infra.projection.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyElement {
    private Long enrollId;
    private String nickname;
    private boolean accepted;
}
