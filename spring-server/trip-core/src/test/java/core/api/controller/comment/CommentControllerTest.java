package core.api.controller.comment;

import core.api.common.resolver.IdResolver;
import core.api.common.resolver.UsernameResolver;
import core.api.request.comment.AddCommentRequest;
import core.api.request.comment.UpdateCommentRequest;
import core.api.response.ApiStatusResponse;
import core.common.Status;
import core.service.comment.CommentService;
import core.support.MockMvcTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({IdResolver.class, UsernameResolver.class})
class CommentControllerTest extends MockMvcTestSupport {

    @MockitoBean
    private CommentService commentService;

    @Test
    @DisplayName("likeComment_success")
    void likeComment_success() throws Exception {
        when(commentService.likeComment(anyLong(), anyLong()))
                .thenReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(post("/api/review/comment/1/like"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("deleteLikeComment_success")
    void deleteLikeComment_success() throws Exception {
        when(commentService.deleteLikeComment(anyLong(), anyLong()))
                .thenReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(delete("/api/review/comment/1/like"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("addComment_success")
    void addComment_success() throws Exception {
        AddCommentRequest request = new AddCommentRequest();
        request.setContent("Test comment content");
        request.setParentId(null);

        when(commentService.addComment(anyLong(), any(AddCommentRequest.class), anyLong()))
                .thenReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(post("/api/review/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("updateComment_success")
    void updateComment_success() throws Exception {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setContent("Updated comment content");

        when(commentService.updateComment(anyLong(), anyLong(), any(UpdateCommentRequest.class), anyLong()))
                .thenReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(put("/api/review/1/comment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("deleteComment_success")
    void deleteComment_success() throws Exception {
        when(commentService.deleteComment(anyLong(), anyLong(), anyLong()))
                .thenReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(delete("/api/review/1/comment/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("comments_success")
    void comments_success() throws Exception {
        when(commentService.comments(anyLong(), anyLong(), any(Integer.class), any(Integer.class)))
                .thenReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(get("/api/review/1/comments"))
                .andExpect(status().isOk());
    }
}
