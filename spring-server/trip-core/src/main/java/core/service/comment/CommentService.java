package core.service.comment;
import core.api.response.ApiStatusResponse;
import core.api.response.ApiDataResponse;
import core.api.response.ApiResponse;
import core.api.request.comment.AddCommentRequest;
import core.api.request.comment.UpdateCommentRequest;
import core.common.Status;
import core.common.exception.CommonException;
import core.domain.entity.comment.Comment;
import core.domain.entity.comment.CommentLike;
import core.domain.entity.member.Member;
import core.domain.entity.receiptreview.ReceiptReview;
import core.infra.jpa.comment.CommentLikeRepository;
import core.infra.jpa.comment.CommentRepository;
import core.infra.jpa.member.MemberRepository;
import core.infra.jpa.receiptreview.ReceiptReviewRepository;
import core.infra.projection.comment.CommentElement;
import core.infra.querydsl.comment.QueryDslCommentRepository;
import core.infra.querydsl.comment.QueryDslCommentLikeRepository;
import core.infra.jdbc.comment.JdbcCommentRepository;
import static core.common.util.CommentUtil.getNextPath;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ReceiptReviewRepository reviewRepository;
    private final QueryDslCommentRepository queryDslCommentRepository;
    private final QueryDslCommentLikeRepository queryDslCommentLikeRepository;
    private final JdbcCommentRepository jdbcCommentRepository;

    public ApiResponse likeComment(Long commentId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new CommonException(Status.NOT_FOUND_COMMENT));
        Optional<CommentLike> optionalCommentLike = queryDslCommentLikeRepository.findByCommentAndMember(comment, member);
        if(optionalCommentLike.isPresent()) throw new CommonException(Status.ALREADY_COMMENT_LIKE);
        CommentLike commentLike = CommentLike.of(comment, member);
        commentLikeRepository.save(commentLike);
        comment.plusCount();
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    public ApiResponse deleteLikeComment(Long commentId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new CommonException(Status.NOT_FOUND_COMMENT));
        CommentLike commentLike = queryDslCommentLikeRepository.findByCommentAndMember(comment, member)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_COMMENT_LIKE));
        commentLikeRepository.delete(commentLike);
        comment.minusCount();
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    public ApiResponse addComment(Long reviewId, AddCommentRequest addCommentRequest,  Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        ReceiptReview receiptReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_RECEIPT_REVIEW));
        Long parentId = addCommentRequest.getParentId();
        Comment parentComment = null;
        int depth = 0;
        if(parentId != null){
            parentComment = commentRepository.findById(parentId)
                    .orElseThrow(() -> new CommonException(Status.NOT_FOUND_COMMENT));
            depth = parentComment.getDepth() + 1;
        }
        Comment lastComment = queryDslCommentRepository.findLastCommentByParentComment(parentId);
        String path = getNextPath(lastComment,depth);
        String content = addCommentRequest.getContent();
        Comment comment = Comment.of(member,receiptReview,content,path,parentComment,depth);
        commentRepository.save(comment);
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    public ApiResponse updateComment(Long reviewId, Long commentId, UpdateCommentRequest updateCommentRequest, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_RECEIPT_REVIEW));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new CommonException(Status.NOT_FOUND_COMMENT));
        String content = updateCommentRequest.getContent();
        Long commentMemberId = comment.getMember().getId();
        Long memberId = member.getId();
        boolean authorize = commentMemberId.equals(memberId);
        if(!authorize) throw new CommonException(Status.NOT_AUTHORIZED);
        comment.changeContent(content);
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    public ApiResponse deleteComment(Long reviewId, Long commentId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_RECEIPT_REVIEW));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new CommonException(Status.NOT_FOUND_COMMENT));
        Long commentMemberId = comment.getMember().getId();
        Long memberId = member.getId();
        boolean authorize = commentMemberId.equals(memberId);
        if(!authorize) throw new CommonException(Status.NOT_AUTHORIZED);
        //TODO
        List<Long> ids = queryDslCommentRepository.findByRecursive(commentId);
        jdbcCommentRepository.deleteAllById(ids);
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    public ApiResponse comments(Long reviewId, Long id, Integer pageNum, Integer pageSize) {
        reviewRepository.findById(reviewId)
                .orElseThrow(()-> new CommonException(Status.NOT_FOUND_RECEIPT_REVIEW));
        memberRepository.findById(id)
                .orElseThrow(()->new CommonException(Status.NOT_FOUND_MEMBER));
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.Direction.ASC, "path");
        Page<CommentElement> page = queryDslCommentRepository.findCommentsByReceiptReview(reviewId, pageRequest);
        List<CommentElement> comments = page.getContent();
        boolean hasNext = page.hasNext();
        return ApiDataResponse.of(Map.of("comments", comments, "hasNext", hasNext), Status.SUCCESS);
    }
}
