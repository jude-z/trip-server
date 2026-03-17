package core.api.controller.keep;

import core.api.common.resolver.IdResolver;
import core.api.common.resolver.UsernameResolver;
import core.api.response.ApiStatusResponse;
import core.common.Status;
import core.service.keep.KeepService;
import core.support.MockMvcTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KeepController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({IdResolver.class, UsernameResolver.class})
class KeepControllerTest extends MockMvcTestSupport {

    @MockitoBean
    private KeepService keepService;

    @Test
    @DisplayName("POST /api/destination/{contentId}/create - 북마크 생성 성공")
    void createKeep_success() throws Exception {
        given(keepService.createKeep(anyString(), anyLong()))
                .willReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(post("/api/destination/CONTENT001/create"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/destination/{contentId}/delete - 북마크 삭제 성공")
    void deleteKeep_success() throws Exception {
        given(keepService.deleteKeep(anyString(), anyLong()))
                .willReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(post("/api/destination/CONTENT001/delete"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/destination/keeps - 북마크 목록 조회 성공")
    void keeps_success() throws Exception {
        given(keepService.keeps(anyLong(), anyInt(), anyInt()))
                .willReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(get("/api/destination/keeps"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/destination/{contentId}/keep - 북마크 상세 조회 성공")
    void detailKeep_success() throws Exception {
        given(keepService.detailKeep(anyString(), anyLong()))
                .willReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(get("/api/destination/CONTENT001/keep"))
                .andExpect(status().isOk());
    }
}
