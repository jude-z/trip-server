package com.tripplannerai.service.comment;

import com.tripplannerai.common.exception.comment.*;
import com.tripplannerai.common.exception.group.NotFoundGroupException;
import com.tripplannerai.common.exception.member.NotAuthorizeException;
import com.tripplannerai.common.exception.member.NotFoundMemberException;
import com.tripplannerai.dto.request.comment.AddCommentRequest;
import com.tripplannerai.dto.request.comment.UpdateCommentRequest;
import com.tripplannerai.dto.response.comment.*;
import com.tripplannerai.entity.comment.Comment;
import com.tripplannerai.entity.comment.CommentLike;
import com.tripplannerai.entity.comment.GroupComment;
import com.tripplannerai.entity.comment.GroupCommentLike;
import com.tripplannerai.entity.group.Group;
import com.tripplannerai.entity.member.Member;
import com.tripplannerai.entity.receiptreview.ReceiptReview;
import com.tripplannerai.repository.comment.CommentLikeRepository;
import com.tripplannerai.repository.comment.CommentRepository;
import com.tripplannerai.repository.comment.GroupCommentLikeRepository;
import com.tripplannerai.repository.comment.GroupCommentRepository;
import com.tripplannerai.repository.group.GroupRepository;
import com.tripplannerai.repository.member.MemberRepository;
import com.tripplannerai.repository.receiptreview.ReceiptReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.tripplannerai.util.ConstClass.SUCCESS_CODE;
import static com.tripplannerai.util.ConstClass.SUCCESS_MESSAGE;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupCommentService {
    private final MemberRepository memberRepository;
    private final GroupCommentRepository groupCommentRepository;
    private final GroupCommentLikeRepository groupCommentLikeRepository;
    private final GroupRepository groupRepository;
    public AddCommentResponse addComment(Long groupId, AddCommentRequest addCommentRequest,Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("Not Found Member!!"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundGroupException("Not Found Group!!"));
        String content = addCommentRequest.getContent();
        GroupComment groupComment = GroupComment.of(member,group,content);
        groupCommentRepository.save(groupComment);
        return AddCommentResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

    public UpdateCommentResponse updateComment(Long groupId, Long groupCommentId, UpdateCommentRequest updateCommentRequest, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("Not Found Member!!"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundGroupException("Not Found Group!!"));
        GroupComment groupComment = groupCommentRepository.findById(groupCommentId)
                .orElseThrow(()-> new NotFoundGroupCommentException("Not Found GroupComment!!"));
        String content = updateCommentRequest.getContent();
        Long groupCommentMemberId = groupComment.getMember().getId();
        Long memberId = member.getId();
        boolean authorize = groupCommentMemberId.equals(memberId);
        if(!authorize) throw new NotAuthorizeException("Not Authorize!!");
        groupComment.changeContent(content);
        return UpdateCommentResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

    public DeleteCommentResponse deleteComment(Long groupId, Long groupCommentId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("Not Found Member!!"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundGroupException("Not Found Group!!"));
        GroupComment groupComment = groupCommentRepository.findById(groupCommentId)
                .orElseThrow(()-> new NotFoundGroupCommentException("Not Found GroupComment!!"));
        Long commentMemberId = groupComment.getMember().getId();
        Long memberId = member.getId();
        boolean authorize = commentMemberId.equals(memberId);
        if(!authorize) throw new NotAuthorizeException("Not Authorize!!");
        groupComment.changeStatus(true);
        return DeleteCommentResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

    public CommentResponse comments(Long groupId, Long id, Integer pageNum, Integer pageSize) {
        memberRepository.findById(id)
                .orElseThrow(()->new NotFoundMemberException("Not Found Member!!"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundGroupException("Not Found Group!!"));
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.Direction.ASC, "group_comment_id");
        Page<CommentElement> page = groupCommentRepository.findCommentsByGroup(groupId, pageRequest);
        List<CommentElement> comments = page.getContent();
        boolean hasNext = page.hasNext();
        return CommentResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,comments,hasNext);
    }

    public LikeCommentResponse likeComment(Long groupCommentId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("Not Found Member!!"));
        GroupComment groupComment = groupCommentRepository.findById(groupCommentId)
                .orElseThrow(()-> new NotFoundGroupCommentException("Not Found GroupComment!!"));
        Optional<GroupCommentLike> optionalCommentLike = groupCommentLikeRepository.findByGroupCommentAndMember(groupComment, member);
        if(optionalCommentLike.isPresent()) throw new AlreadyCommentLikeException("Already CommentLike!!");
        GroupCommentLike groupCommentLike = GroupCommentLike.of(groupComment, member);
        groupCommentLikeRepository.save(groupCommentLike);
        groupComment.plusCount();
        return LikeCommentResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

    public LikeCommentResponse deleteLikeComment(Long groupCommentId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("Not Found Member!!"));
        GroupComment groupComment = groupCommentRepository.findById(groupCommentId)
                .orElseThrow(()-> new NotFoundGroupCommentException("Not Found GroupComment!!"));
        GroupCommentLike groupCommentLike = groupCommentLikeRepository.findByGroupCommentAndMember(groupComment, member)
                .orElseThrow(() -> new NotFoundCommentLikeException("Not Found CommentLike!!"));
        groupCommentLikeRepository.delete(groupCommentLike);
        groupComment.minusCount();
        return LikeCommentResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }
}
