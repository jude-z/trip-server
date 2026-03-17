package core.fixture;

import core.domain.entity.address.Address;
import core.domain.entity.category.Category;
import core.domain.entity.destination.Destination;
import core.domain.entity.enroll.Enroll;
import core.domain.entity.group.Group;
import core.domain.entity.group.GroupLike;
import core.domain.entity.member.Member;

import java.time.LocalDateTime;

public class GroupFixture {

    public static Group.GroupBuilder defaultGroup(Member member, Destination destination) {
        return Group.builder()
                .groupId(1L)
                .title("Test Group")
                .status(true)
                .description("Test group description")
                .count(0)
                .participateCount(0)
                .groupLikeCount(0)
                .maxCount(10)
                .startDate(LocalDateTime.of(2026, 4, 1, 10, 0))
                .endDate(LocalDateTime.of(2026, 4, 5, 18, 0))
                .destination(destination)
                .member(member);
    }

    public static Group createGroup(Member member, Destination destination) {
        return defaultGroup(member, destination).build();
    }

    public static Destination.DestinationBuilder defaultDestination() {
        return Destination.builder()
                .destinationId(1L)
                .name("Test Destination")
                .addr1("Seoul, Korea")
                .contentId("CONTENT001")
                .latitude("37.5665")
                .longitude("126.9780");
    }

    public static Destination createDestination() {
        return defaultDestination().build();
    }

    public static Enroll.EnrollBuilder defaultEnroll(Member member, Group group) {
        return Enroll.builder()
                .enrollId(1L)
                .accepted(false)
                .member(member)
                .group(group);
    }

    public static Enroll createEnroll(Member member, Group group) {
        return defaultEnroll(member, group).build();
    }

    public static GroupLike createGroupLike(Member member, Group group) {
        return GroupLike.of(member, group);
    }

    public static Address createAddress() {
        return Address.builder()
                .addressId(1L)
                .areaCode("1")
                .sigunguCode("1")
                .name("Seoul")
                .build();
    }

    public static Category createCategory() {
        return Category.builder()
                .categoryId(1L)
                .categoryCode("A01")
                .name("Nature")
                .build();
    }
}
