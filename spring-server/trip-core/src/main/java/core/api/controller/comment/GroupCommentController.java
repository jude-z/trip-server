package core.api.controller.comment;

import core.api.common.resolver.annotation.Id;
import core.api.request.comment.AddCommentRequest;
import core.api.request.comment.UpdateCommentRequest;
import core.api.response.ApiResponse;
import core.service.comment.GroupCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("")
@RequiredArgsConstructor
public class GroupCommentController {
    private final GroupCommentService groupCommentService;

    @PostMapping("/api/group/{groupId}/comment")
    public ResponseEntity<ApiResponse> addComment(@PathVariable Long groupId,
                                                   @RequestBody AddCommentRequest addCommentRequest,
                                                   @Id Long id) {
        return ResponseEntity.ok(groupCommentService.addComment(groupId, addCommentRequest, id));
    }

    @PutMapping("/api/group/{groupId}/comment/{groupCommentId}")
    public ResponseEntity<ApiResponse> updateComment(@PathVariable Long groupId, @PathVariable Long groupCommentId,
                                                      @RequestBody UpdateCommentRequest updateCommentRequest, @Id Long id) {
        return ResponseEntity.ok(groupCommentService.updateComment(groupId, groupCommentId, updateCommentRequest, id));
    }

    @DeleteMapping("/api/group/{groupId}/comment/{groupCommentId}")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable Long groupId, @PathVariable Long groupCommentId,
                                                      @Id Long id) {
        return ResponseEntity.ok(groupCommentService.deleteComment(groupId, groupCommentId, id));
    }

    @GetMapping("/api/group/{groupId}/comments")
    public ResponseEntity<ApiResponse> comments(@PathVariable Long groupId, @Id Long id,
                                                 @RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(groupCommentService.comments(groupId, id, page, pageSize));
    }

    @PostMapping("/api/groupComment/{groupCommentId}/like")
    public ResponseEntity<ApiResponse> likeComment(@PathVariable Long groupCommentId, @Id Long id) {
        return ResponseEntity.ok(groupCommentService.likeComment(groupCommentId, id));
    }

    @DeleteMapping("/api/groupComment/{groupCommentId}/like")
    public ResponseEntity<ApiResponse> deleteLikeComment(@PathVariable Long groupCommentId, @Id Long id) {
        return ResponseEntity.ok(groupCommentService.deleteLikeComment(groupCommentId, id));
    }
}
