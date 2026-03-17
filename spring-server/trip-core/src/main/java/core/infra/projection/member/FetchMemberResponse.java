package core.infra.projection.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FetchMemberResponse {
    private Long id;
    private String email;
    private String nickname;
    private String phoneNumber;
    private String provider;
    private String providerId;
    private boolean isWithdrawn;
    private String imageUrl;
    private String customerKey;
    private Integer ticket;
}
