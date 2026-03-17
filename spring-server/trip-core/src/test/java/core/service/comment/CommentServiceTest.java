package core.service.comment;

import core.api.request.comment.AddCommentRequest;
import core.api.request.comment.UpdateCommentRequest;
import core.api.response.ApiResponse;
import core.common.Status;
import core.common.exception.CommonException;
import core.domain.entity.comment.Comment;
import core.domain.entity.comment.CommentLike;
import core.domain.entity.member.Member;
import core.domain.entity.receiptreview.ReceiptReview;
import core.fixture.CommentFixture;
import core.fixture.MemberFixture;
import core.infra.jdbc.comment.JdbcCommentRepository;
import core.infra.jpa.comment.CommentLikeRepository;
import core.infra.jpa.comment.CommentRepository;
import core.infra.jpa.member.MemberRepository;
import core.infra.jpa.receiptreview.ReceiptReviewRepository;
import core.infra.projection.comment.CommentElement;
import core.infra.querydsl.comment.QueryDslCommentLikeRepository;
import core.infra.querydsl.comment.QueryDslCommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private ReceiptReviewRepository reviewRepository;

    @Mock
    private QueryDslCommentRepository queryDslCommentRepository;

    @Mock
    private QueryDslCommentLikeRepository queryDslCommentLikeRepository;

    @Mock
    private JdbcCommentRepository jdbcCommentRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    @DisplayName("likeComment - success")
    void likeComment_success() {
        // given
        Member member = MemberFixture.create();
        Comment comment = mock(Comment.class);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));
        given(queryDslCommentLikeRepository.findByCommentAndMember(comment, member)).willReturn(Optional.empty());

        // when
        commentService.likeComment(1L, 1L);

        // then
        verify(commentLikeRepository).save(any(CommentLike.class));
        verify(comment).plusCount();
    }

    @Test
    @DisplayName("likeComment - already liked throws CommonException")
    void likeComment_alreadyLiked() {
        // given
        Member member = MemberFixture.create();
        Comment comment = mock(Comment.class);
        CommentLike commentLike = mock(CommentLike.class);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));
        given(queryDslCommentLikeRepository.findByCommentAndMember(comment, member)).willReturn(Optional.of(commentLike));

        // when & then
        assertThatThrownBy(() -> commentService.likeComment(1L, 1L))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.ALREADY_COMMENT_LIKE));
    }

    @Test
    @DisplayName("deleteLikeComment - success")
    void deleteLikeComment_success() {
        // given
        Member member = MemberFixture.create();
        Comment comment = mock(Comment.class);
        CommentLike commentLike = mock(CommentLike.class);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));
        given(queryDslCommentLikeRepository.findByCommentAndMember(comment, member)).willReturn(Optional.of(commentLike));

        // when
        commentService.deleteLikeComment(1L, 1L);

        // then
        verify(commentLikeRepository).delete(commentLike);
        verify(comment).minusCount();
    }

    @Test
    @DisplayName("deleteLikeComment - not found throws CommonException")
    void deleteLikeComment_notFound() {
        // given
        Member member = MemberFixture.create();
        Comment comment = mock(Comment.class);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));
        given(queryDslCommentLikeRepository.findByCommentAndMember(comment, member)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.deleteLikeComment(1L, 1L))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.NOT_FOUND_COMMENT_LIKE));
    }

    @Test
    @DisplayName("updateComment - success")
    void updateComment_success() {
        // given
        Member member = mock(Member.class);
        Comment comment = mock(Comment.class);
        Member commentOwner = mock(Member.class);

        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setContent("Updated content");

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(reviewRepository.findById(1L)).willReturn(Optional.of(mock(ReceiptReview.class)));
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));
        given(comment.getMember()).willReturn(commentOwner);
        given(commentOwner.getId()).willReturn(1L);
        given(member.getId()).willReturn(1L);

        // when
        commentService.updateComment(1L, 1L, request, 1L);

        // then
        verify(comment).changeContent("Updated content");
    }

    @Test
    @DisplayName("updateComment - not authorized throws CommonException")
    void updateComment_notAuthorized() {
        // given
        Member member = mock(Member.class);
        Comment comment = mock(Comment.class);
        Member commentOwner = mock(Member.class);

        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setContent("Updated content");

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(reviewRepository.findById(1L)).willReturn(Optional.of(mock(ReceiptReview.class)));
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));
        given(comment.getMember()).willReturn(commentOwner);
        given(commentOwner.getId()).willReturn(2L);
        given(member.getId()).willReturn(1L);

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(1L, 1L, request, 1L))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.NOT_AUTHORIZED));
    }

    @Test
    @DisplayName("deleteComment - success")
    void deleteComment_success() {
        // given
        Member member = mock(Member.class);
        Comment comment = mock(Comment.class);
        Member commentOwner = mock(Member.class);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(reviewRepository.findById(1L)).willReturn(Optional.of(mock(ReceiptReview.class)));
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));
        given(comment.getMember()).willReturn(commentOwner);
        given(commentOwner.getId()).willReturn(1L);
        given(member.getId()).willReturn(1L);
        given(queryDslCommentRepository.findByRecursive(1L)).willReturn(List.of(1L));

        // when
        commentService.deleteComment(1L, 1L, 1L);

        // then
        verify(jdbcCommentRepository).deleteAllById(List.of(1L));
    }

    @Test
    @DisplayName("deleteComment - not authorized throws CommonException")
    void deleteComment_notAuthorized() {
        // given
        Member member = mock(Member.class);
        Comment comment = mock(Comment.class);
        Member commentOwner = mock(Member.class);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(reviewRepository.findById(1L)).willReturn(Optional.of(mock(ReceiptReview.class)));
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));
        given(comment.getMember()).willReturn(commentOwner);
        given(commentOwner.getId()).willReturn(2L);
        given(member.getId()).willReturn(1L);

        // when & then
        assertThatThrownBy(() -> commentService.deleteComment(1L, 1L, 1L))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.NOT_AUTHORIZED));
    }

    @Test
    @DisplayName("comments - success with pagination")
    void comments_success() {
        // given
        Member member = MemberFixture.create();
        Page<CommentElement> page = new PageImpl<>(List.of());

        given(reviewRepository.findById(1L)).willReturn(Optional.of(mock(ReceiptReview.class)));
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(queryDslCommentRepository.findCommentsByReceiptReview(any(Long.class), any(PageRequest.class))).willReturn(page);

        // when
        ApiResponse result = commentService.comments(1L, 1L, 1, 10);

        // then
        assertThat(result).isNotNull();
    }
}
