package core.api.controller.course;

import core.api.common.resolver.IdResolver;
import core.api.common.resolver.UsernameResolver;
import core.api.response.ApiDataResponse;
import core.api.response.ApiStatusResponse;
import core.common.Status;
import core.service.course.CourseLikeService;
import core.support.MockMvcTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseLikeController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({IdResolver.class, UsernameResolver.class})
class CourseLikeControllerTest extends MockMvcTestSupport {

    @MockitoBean
    private CourseLikeService courseLikeService;

    @Test
    @DisplayName("POST /api/courseLike/{courseId} - 코스 좋아요 추가 성공")
    void addLikeCourse_success() throws Exception {
        doNothing().when(courseLikeService).addLikeCourse(anyLong(), anyLong());

        mockMvc.perform(post("/api/courseLike/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/courseLike/{courseId} - 코스 좋아요 삭제 성공")
    void removeLikeCourse_success() throws Exception {
        doNothing().when(courseLikeService).removeLikeCourse(anyLong(), anyLong());

        mockMvc.perform(delete("/api/courseLike/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/courseLike/{courseId}/liked - 코스 좋아요 여부 조회 성공")
    void isLiked_success() throws Exception {
        given(courseLikeService.isLiked(anyLong(), anyLong()))
                .willReturn(true);

        mockMvc.perform(get("/api/courseLike/1/liked"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/courseLike/liked - 좋아요한 코스 목록 조회 성공")
    void getLikedCourses_success() throws Exception {
        given(courseLikeService.getLikedCourses(anyLong()))
                .willReturn(ApiDataResponse.of(List.of(), Status.GET_COURSES_LIKED));

        mockMvc.perform(get("/api/courseLike/liked"))
                .andExpect(status().isOk());
    }
}
