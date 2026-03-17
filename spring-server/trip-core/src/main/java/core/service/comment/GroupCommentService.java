package core.service.comment;

import core.api.response.ApiStatusResponse;
import core.api.response.ApiDataResponse;
import core.api.response.ApiResponse;
import core.api.request.comment.AddCommentRequest;
import core.api.request.comment.UpdateCommentRequest;
import core.common.Status;
import core.common.exception.CommonException;
import core.domain.entity.comment.GroupComment;
import core.domain.entity.comment.GroupCommentLike;
import core.domain.entity.group.Group;
import core.domain.entity.member.Member;
import core.infra.jpa.comment.GroupCommentLikeRepository;
import core.infra.jpa.comment.GroupCommentRepository;
import core.infra.jpa.group.GroupRepository;
import core.infra.jpa.member.MemberRepository;
import core.infra.projection.comment.CommentElement;
import core.infra.querydsl.comment.QueryDslGroupCommentRepository;
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
public class GroupCommentService {
    private final MemberRepository memberRepository;
    private final GroupCommentRepository groupCommentRepository;
    private final GroupCommentLikeRepository groupCommentLikeRepository;
    private final GroupRepository groupRepository;
    private final QueryDslGroupCommentRepository queryDslGroupCommentRepository;
    public ApiResponse addComment(Long groupId, AddCommentRequest addCommentRequest,Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_GROUP));
        String content = addCommentRequest.getContent();
        GroupComment groupComment = GroupComment.of(member,group,content);
        groupCommentRepository.save(groupComment);
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    public ApiResponse updateComment(Long groupId, Long groupCommentId, UpdateCommentRequest updateCommentRequest, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_GROUP));
        GroupComment groupComment = groupCommentRepository.findById(groupCommentId)
                .orElseThrow(()-> new CommonException(Status.NOT_FOUND_GROUP_COMMENT));
        String content = updateCommentRequest.getContent();
        Long groupCommentMemberId = groupComment.getMember().getId();
        Long memberId = member.getId();
        boolean authorize = groupCommentMemberId.equals(memberId);
        if(!authorize) throw new CommonException(Status.NOT_AUTHORIZED);
        groupComment.changeContent(content);
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    public ApiResponse deleteComment(Long groupId, Long groupCommentId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_GROUP));
        GroupComment groupComment = groupCommentRepository.findById(groupCommentId)
                .orElseThrow(()-> new CommonException(Status.NOT_FOUND_GROUP_COMMENT));
        Long commentMemberId = groupComment.getMember().getId();
        Long memberId = member.getId();
        boolean authorize = commentMemberId.equals(memberId);
        if(!authorize) throw new CommonException(Status.NOT_AUTHORIZED);
        groupComment.changeStatus(true);
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    public ApiResponse comments(Long groupId, Long id, Integer pageNum, Integer pageSize) {
        memberRepository.findById(id)
                .orElseThrow(()->new CommonException(Status.NOT_FOUND_MEMBER));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_GROUP));
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.Direction.ASC, "group_comment_id");
        Page<CommentElement> page = queryDslGroupCommentRepository.findCommentsByGroup(groupId, pageRequest);
        List<CommentElement> comments = page.getContent();
        boolean hasNext = page.hasNext();
        return ApiDataResponse.of(Map.of("comments", comments, "hasNext", hasNext), Status.SUCCESS);
    }

    public ApiResponse likeComment(Long groupCommentId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        GroupComment groupComment = groupCommentRepository.findById(groupCommentId)
                .orElseThrow(()-> new CommonException(Status.NOT_FOUND_GROUP_COMMENT));
        Optional<GroupCommentLike> optionalCommentLike = groupCommentLikeRepository.findByGroupCommentAndMember(groupComment, member);
        if(optionalCommentLike.isPresent()) throw new CommonException(Status.ALREADY_COMMENT_LIKE);
        GroupCommentLike groupCommentLike = GroupCommentLike.of(groupComment, member);
        groupCommentLikeRepository.save(groupCommentLike);
        groupComment.plusCount();
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    public ApiResponse deleteLikeComment(Long groupCommentId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        GroupComment groupComment = groupCommentRepository.findById(groupCommentId)
                .orElseThrow(()-> new CommonException(Status.NOT_FOUND_GROUP_COMMENT));
        GroupCommentLike groupCommentLike = groupCommentLikeRepository.findByGroupCommentAndMember(groupComment, member)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_COMMENT_LIKE));
        groupCommentLikeRepository.delete(groupCommentLike);
        groupComment.minusCount();
        return ApiStatusResponse.of(Status.SUCCESS);
    }
}
