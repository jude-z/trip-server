package com.tripplannerai.controller.coment;

import com.tripplannerai.common.annotation.Id;
import com.tripplannerai.dto.request.comment.AddCommentRequest;
import com.tripplannerai.dto.request.comment.UpdateCommentRequest;
import com.tripplannerai.dto.response.comment.*;
import com.tripplannerai.service.comment.GroupCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("")
@RequiredArgsConstructor
public class GroupCommentController {
    private final GroupCommentService groupCommentService;

    @PostMapping("/api/group/{groupId}/comment")
    public ResponseEntity<AddCommentResponse> addComment(@PathVariable Long groupId
            , @RequestBody AddCommentRequest addCommentRequest
            ,@Id Long id){
        AddCommentResponse addCommentResponse = groupCommentService.addComment(groupId,addCommentRequest,id);
        return new ResponseEntity<>(addCommentResponse, HttpStatus.OK);
    }
    @PutMapping("/api/group/{groupId}/comment/{groupCommentId}")
    public ResponseEntity<UpdateCommentResponse> updateComment(@PathVariable Long groupId, @PathVariable Long groupCommentId,
                                                               @RequestBody UpdateCommentRequest updateCommentRequest, @Id Long id){
        UpdateCommentResponse updateCommentResponse = groupCommentService.updateComment(groupId,groupCommentId,updateCommentRequest,id);
        return new ResponseEntity<>(updateCommentResponse, HttpStatus.OK);
    }

    @DeleteMapping("/api/group/{groupId}/comment/{groupCommentId}")
    public ResponseEntity<DeleteCommentResponse> deleteComment(@PathVariable Long groupId, @PathVariable Long groupCommentId,
                                                               @Id Long id){
        DeleteCommentResponse deleteCommentResponse = groupCommentService.deleteComment(groupId,groupCommentId,id);
        return new ResponseEntity<>(deleteCommentResponse, HttpStatus.OK);
    }

    @GetMapping("/api/group/{groupId}/comments")
    public ResponseEntity<CommentResponse> comments(@PathVariable Long groupId,@Id Long id
            ,@RequestParam(defaultValue = "1") Integer page
            , @RequestParam(defaultValue = "10") Integer pageSize){
        CommentResponse commentResponse = groupCommentService.comments(groupId,id,page,pageSize);
        return new ResponseEntity<>(commentResponse, HttpStatus.OK);
    }

    @PostMapping("/api/groupComment/{groupCommentId}/like")
    public ResponseEntity<LikeCommentResponse> likeComment(@PathVariable Long groupCommentId, @Id Long id){
        LikeCommentResponse likeCommentResponse = groupCommentService.likeComment(groupCommentId,id);
        return new ResponseEntity<>(likeCommentResponse, HttpStatus.OK);
    }

    @DeleteMapping("/api/groupComment/{groupCommentId}/like")
    public ResponseEntity<LikeCommentResponse> deleteLikeComment(@PathVariable Long groupCommentId, @Id Long id){
        LikeCommentResponse likeCommentResponse = groupCommentService.deleteLikeComment(groupCommentId,id);
        return new ResponseEntity<>(likeCommentResponse, HttpStatus.OK);
    }



}
