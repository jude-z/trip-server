package core.infra.querydsl.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import core.domain.entity.image.QImage;
import core.domain.entity.member.Member;
import core.domain.entity.member.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QueryDslMemberRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<Member> findByEmail(String email) {
        QMember member = QMember.member;
        QImage image = QImage.image;
        return Optional.ofNullable(queryFactory
                .selectFrom(member)
                .leftJoin(member.image, image).fetchJoin()
                .where(member.email.eq(email))
                .fetchOne());
    }

    public Optional<Member> fetchById(Long memberId) {
        QMember member = QMember.member;
        QImage image = QImage.image;
        return Optional.ofNullable(queryFactory
                .selectFrom(member)
                .leftJoin(member.image, image).fetchJoin()
                .where(member.id.eq(memberId))
                .fetchOne());
    }
}
