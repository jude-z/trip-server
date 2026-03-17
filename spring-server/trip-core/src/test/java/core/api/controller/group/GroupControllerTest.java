package core.api.controller.group;

import core.api.common.resolver.IdResolver;
import core.api.common.resolver.UsernameResolver;
import core.api.request.group.AddGroupRequest;
import core.api.response.ApiStatusResponse;
import core.common.Status;
import core.service.group.GroupService;
import core.support.MockMvcTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroupController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({IdResolver.class, UsernameResolver.class})
class GroupControllerTest extends MockMvcTestSupport {

    @MockitoBean
    private GroupService groupService;

    @Test
    @DisplayName("addGroup_success")
    void addGroup_success() throws Exception {
        AddGroupRequest request = new AddGroupRequest();
        request.setTitle("Test Group");
        request.setDescription("Test Description");
        request.setMaxCount(10);
        request.setStartDate(LocalDateTime.of(2026, 4, 1, 10, 0));
        request.setEndDate(LocalDateTime.of(2026, 4, 5, 18, 0));

        when(groupService.addGroup(any(AddGroupRequest.class), anyLong(), anyLong()))
                .thenReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(post("/api/group")
                        .param("destinationId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("participateGroup_success")
    void participateGroup_success() throws Exception {
        when(groupService.participateGroup(anyLong(), anyLong()))
                .thenReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(put("/api/group/1/participate"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("permitGroup_success")
    void permitGroup_success() throws Exception {
        when(groupService.permitGroup(anyLong(), anyLong(), anyLong()))
                .thenReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(put("/api/group/1/permit")
                        .param("enrollId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("leaveGroup_success")
    void leaveGroup_success() throws Exception {
        when(groupService.leaveGroup(anyLong(), anyLong()))
                .thenReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(put("/api/group/1/leave"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("deleteGroup_success")
    void deleteGroup_success() throws Exception {
        when(groupService.deleteGroup(anyLong(), anyLong()))
                .thenReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(delete("/api/group/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("groupLike_success")
    void groupLike_success() throws Exception {
        when(groupService.groupLike(anyLong(), anyLong()))
                .thenReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(post("/api/group/1/like"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("deleteGroupLike_success")
    void deleteGroupLike_success() throws Exception {
        when(groupService.deleteGroupLike(anyLong(), anyLong()))
                .thenReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(delete("/api/group/1/like"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("groups_success")
    void groups_success() throws Exception {
        when(groupService.groups(any(Integer.class), any(Integer.class), any(Long.class)))
                .thenReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(get("/api/group"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("groupDetail_success")
    void groupDetail_success() throws Exception {
        when(groupService.groupDetail(anyLong(), anyLong()))
                .thenReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(get("/api/group/1"))
                .andExpect(status().isOk());
    }
}
