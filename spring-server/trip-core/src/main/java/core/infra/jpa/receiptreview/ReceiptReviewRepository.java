package core.infra.jpa.receiptreview;

import core.domain.entity.receiptreview.ReceiptReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptReviewRepository extends JpaRepository<ReceiptReview,Long> {
}
