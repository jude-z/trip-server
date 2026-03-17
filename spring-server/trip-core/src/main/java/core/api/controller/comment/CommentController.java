package core.api.controller.comment;

import core.api.common.resolver.annotation.Id;
import core.api.request.comment.AddCommentRequest;
import core.api.request.comment.UpdateCommentRequest;
import core.api.response.ApiResponse;
import core.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/review/comment/{commentId}/like")
    public ResponseEntity<ApiResponse> likeComment(@PathVariable Long commentId, @Id Long id) {
        return ResponseEntity.ok(commentService.likeComment(commentId, id));
    }

    @DeleteMapping("/api/review/comment/{commentId}/like")
    public ResponseEntity<ApiResponse> deleteLikeComment(@PathVariable Long commentId, @Id Long id) {
        return ResponseEntity.ok(commentService.deleteLikeComment(commentId, id));
    }

    @PostMapping("/api/review/{reviewId}/comment")
    public ResponseEntity<ApiResponse> addComment(@PathVariable Long reviewId,
                                                   @RequestBody AddCommentRequest addCommentRequest, @Id Long id) {
        return ResponseEntity.ok(commentService.addComment(reviewId, addCommentRequest, id));
    }

    @PutMapping("/api/review/{reviewId}/comment/{commentId}")
    public ResponseEntity<ApiResponse> updateComment(@PathVariable Long reviewId, @PathVariable Long commentId,
                                                      @RequestBody UpdateCommentRequest updateCommentRequest, @Id Long id) {
        return ResponseEntity.ok(commentService.updateComment(reviewId, commentId, updateCommentRequest, id));
    }

    @DeleteMapping("/api/review/{reviewId}/comment/{commentId}")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable Long reviewId, @PathVariable Long commentId,
                                                      @Id Long id) {
        return ResponseEntity.ok(commentService.deleteComment(reviewId, commentId, id));
    }

    @GetMapping("/api/review/{reviewId}/comments")
    public ResponseEntity<ApiResponse> comments(@PathVariable Long reviewId, @Id Long id,
                                                 @RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(commentService.comments(reviewId, id, page, pageSize));
    }
}
