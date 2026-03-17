package core.infra.jpa.receiptreview;

import core.domain.entity.receiptreviewimage.ReceiptReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptReviewImageRepository extends JpaRepository<ReceiptReviewImage,Long> {
}
