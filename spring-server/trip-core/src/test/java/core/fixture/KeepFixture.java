package core.fixture;

import core.domain.entity.destination.Destination;
import core.domain.entity.keep.Keep;
import core.domain.entity.member.Member;

public class KeepFixture {

    public static Keep createKeep(Destination destination, Member member) {
        return Keep.of(destination, member);
    }

    public static Keep createKeepWithId(Long id, Destination destination, Member member) {
        return Keep.builder()
                .id(id)
                .destination(destination)
                .member(member)
                .build();
    }

    public static Destination createDestinationWithContentId(String contentId) {
        return Destination.builder()
                .destinationId(1L)
                .name("Test Destination")
                .addr1("Seoul")
                .contentId(contentId)
                .latitude("37.5665")
                .longitude("126.9780")
                .build();
    }
}
