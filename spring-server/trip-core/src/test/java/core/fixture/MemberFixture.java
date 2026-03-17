package core.fixture;

import core.domain.entity.member.Member;

public class MemberFixture {

    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_EMAIL = "test@example.com";
    public static final String DEFAULT_PASSWORD = "encodedPassword";
    public static final String DEFAULT_NICKNAME = "tester";
    public static final String DEFAULT_PHONE = "010-1234-5678";
    public static final Integer DEFAULT_TICKET = 5;

    public static Member.MemberBuilder defaultMember() {
        return Member.builder()
                .id(DEFAULT_ID)
                .email(DEFAULT_EMAIL)
                .password(DEFAULT_PASSWORD)
                .nickname(DEFAULT_NICKNAME)
                .phoneNumber(DEFAULT_PHONE)
                .ticket(DEFAULT_TICKET);
    }

    public static Member create() {
        return defaultMember().build();
    }

    public static Member createWithId(Long id) {
        return defaultMember().id(id).build();
    }

    public static Member createWithEmail(String email) {
        return defaultMember().email(email).build();
    }
}
