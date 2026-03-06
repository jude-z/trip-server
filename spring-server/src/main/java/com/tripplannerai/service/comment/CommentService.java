package com.tripplannerai.service.comment;
import com.tripplannerai.common.exception.comment.AlreadyCommentLikeException;
import com.tripplannerai.common.exception.comment.NotFoundCommentException;
import com.tripplannerai.common.exception.comment.NotFoundCommentLikeException;
import com.tripplannerai.common.exception.comment.NotFoundReceiptReviewExeption;
import com.tripplannerai.common.exception.member.NotAuthorizeException;
import com.tripplannerai.common.exception.member.NotFoundMemberException;
import com.tripplannerai.dto.request.comment.AddCommentRequest;
import com.tripplannerai.dto.request.comment.UpdateCommentRequest;
import com.tripplannerai.dto.response.comment.*;
import com.tripplannerai.entity.comment.Comment;
import com.tripplannerai.entity.comment.CommentLike;
import com.tripplannerai.entity.member.Member;
import com.tripplannerai.entity.receiptreview.ReceiptReview;
import com.tripplannerai.repository.comment.CommentLikeRepository;
import com.tripplannerai.repository.comment.CommentRepository;
import com.tripplannerai.repository.member.MemberRepository;
import com.tripplannerai.repository.receiptreview.ReceiptReviewRepository;
import com.tripplannerai.util.CommentUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.tripplannerai.util.CommentUtil.*;
import static com.tripplannerai.util.ConstClass.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ReceiptReviewRepository reviewRepository;

    public LikeCommentResponse likeComment(Long commentId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("Not Found Member!!"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new NotFoundCommentException("Not Found Comment!!"));
        Optional<CommentLike> optionalCommentLike = commentLikeRepository.findByCommentAndMember(comment, member);
        if(optionalCommentLike.isPresent()) throw new AlreadyCommentLikeException("Already CommentLike!!");
        CommentLike commentLike = CommentLike.of(comment, member);
        commentLikeRepository.save(commentLike);
        comment.plusCount();
        return LikeCommentResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

    public LikeCommentResponse deleteLikeComment(Long commentId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("Not Found Member!!"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new NotFoundCommentException("Not Found Comment!!"));
        CommentLike commentLike = commentLikeRepository.findByCommentAndMember(comment, member)
                .orElseThrow(() -> new NotFoundCommentLikeException("Not Found CommentLike!!"));
        commentLikeRepository.delete(commentLike);
        comment.minusCount();
        return LikeCommentResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

    public AddCommentResponse addComment(Long reviewId, AddCommentRequest addCommentRequest,  Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("Not Found Member!!"));
        ReceiptReview receiptReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundReceiptReviewExeption("Not Found ReceiptReview!!"));
        Long parentId = addCommentRequest.getParentId();
        Comment parentComment = null;
        int depth = 0;
        if(parentId != null){
            parentComment = commentRepository.findById(parentId)
                    .orElseThrow(() -> new NotFoundCommentException("Not Found Comment!"));
            depth = parentComment.getDepth() + 1;
        }
        Comment lastComment = commentRepository.findLastCommentByParentComment(parentId);
        String path = getNextPath(lastComment,depth);
        String content = addCommentRequest.getContent();
        Comment comment = Comment.of(member,receiptReview,content,path,parentComment,depth);
        commentRepository.save(comment);
        return AddCommentResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

    public UpdateCommentResponse updateComment(Long reviewId, Long commentId, UpdateCommentRequest updateCommentRequest, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("Not Found Member!!"));
        reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundReceiptReviewExeption("Not Found ReceiptReview!!"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new NotFoundCommentException("Not Found Comment!!"));
        String content = updateCommentRequest.getContent();
        Long commentMemberId = comment.getMember().getId();
        Long memberId = member.getId();
        boolean authorize = commentMemberId.equals(memberId);
        if(!authorize) throw new NotAuthorizeException("Not Authorize!!");
        comment.changeContent(content);
        return UpdateCommentResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

    public DeleteCommentResponse deleteComment(Long reviewId, Long commentId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("Not Found Member!!"));
        reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundReceiptReviewExeption("Not Found ReceiptReview!!"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new NotFoundCommentException("Not Found Comment!!"));
        Long commentMemberId = comment.getMember().getId();
        Long memberId = member.getId();
        boolean authorize = commentMemberId.equals(memberId);
        if(!authorize) throw new NotAuthorizeException("Not Authorize!!");
        //TODO
        List<Long> ids = commentRepository.findByRecursive(commentId);
        commentRepository.deleteAllById(ids);
        return DeleteCommentResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

    public CommentResponse comments(Long reviewId, Long id, Integer pageNum, Integer pageSize) {
        reviewRepository.findById(reviewId)
                .orElseThrow(()-> new NotFoundReceiptReviewExeption("Not Found Review!!"));
        memberRepository.findById(id)
                .orElseThrow(()->new NotFoundMemberException("Not Found Member!!"));
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.Direction.ASC, "path");
        Page<CommentElement> page = commentRepository.findCommentsByReceiptReview(reviewId, pageRequest);
        List<CommentElement> comments = page.getContent();
        boolean hasNext = page.hasNext();
        return CommentResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,comments,hasNext);
    }
}
