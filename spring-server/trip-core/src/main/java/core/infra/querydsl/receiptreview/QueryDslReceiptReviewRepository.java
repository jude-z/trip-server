package core.infra.querydsl.receiptreview;

import com.querydsl.jpa.impl.JPAQueryFactory;
import core.domain.entity.member.QMember;
import core.domain.entity.receiptreview.QReceiptReview;
import core.domain.entity.receiptreview.ReceiptReview;
import core.domain.entity.receiptreviewimage.QReceiptReviewImage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QueryDslReceiptReviewRepository {

    private final JPAQueryFactory queryFactory;

    public Page<ReceiptReview> findAllWithImagesAndMember(Pageable pageable) {
        QReceiptReview receiptReview = QReceiptReview.receiptReview;
        QReceiptReviewImage image = QReceiptReviewImage.receiptReviewImage;
        QMember member = QMember.member;

        List<ReceiptReview> content = queryFactory
                .selectFrom(receiptReview)
                .leftJoin(receiptReview.images, image).fetchJoin()
                .join(receiptReview.member, member).fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(receiptReview.count())
                .from(receiptReview)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    public Optional<ReceiptReview> findByIdWithImagesAndMember(Long id) {
        QReceiptReview receiptReview = QReceiptReview.receiptReview;
        QReceiptReviewImage image = QReceiptReviewImage.receiptReviewImage;
        QMember member = QMember.member;

        return Optional.ofNullable(queryFactory
                .selectFrom(receiptReview)
                .leftJoin(receiptReview.images, image).fetchJoin()
                .join(receiptReview.member, member).fetchJoin()
                .where(receiptReview.receiptReviewId.eq(id))
                .fetchOne());
    }
}
