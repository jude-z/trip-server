package core.api.controller.group;

import core.api.common.resolver.annotation.Id;
import core.api.request.group.AddGroupRequest;
import core.api.response.ApiResponse;
import core.service.group.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<ApiResponse> addGroup(@RequestParam Long destinationId, @RequestBody AddGroupRequest addGroupRequest, @Id Long id) {
        return ResponseEntity.ok(groupService.addGroup(addGroupRequest, id, destinationId));
    }

    @PutMapping("/{groupId}/participate")
    public ResponseEntity<ApiResponse> participateGroup(@PathVariable Long groupId, @Id Long id) {
        return ResponseEntity.ok(groupService.participateGroup(groupId, id));
    }

    @PutMapping("/{groupId}/permit")
    public ResponseEntity<ApiResponse> permitGroup(@PathVariable Long groupId, @Id Long id, @RequestParam Long enrollId) {
        return ResponseEntity.ok(groupService.permitGroup(groupId, id, enrollId));
    }

    @PutMapping("/{groupId}/leave")
    public ResponseEntity<ApiResponse> leaveGroup(@PathVariable Long groupId, @Id Long id) {
        return ResponseEntity.ok(groupService.leaveGroup(groupId, id));
    }

    @GetMapping("/{groupId}/apply")
    public ResponseEntity<ApiResponse> applyGroups(@PathVariable Long groupId, @Id Long id) {
        return ResponseEntity.ok(groupService.applyGroups(groupId, id));
    }

    @GetMapping("/{groupId}/participate")
    public ResponseEntity<ApiResponse> participateGroups(@PathVariable Long groupId, @Id Long id) {
        return ResponseEntity.ok(groupService.participateGroups(groupId, id));
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse> deleteGroup(@PathVariable Long groupId, @Id Long id) {
        return ResponseEntity.ok(groupService.deleteGroup(groupId, id));
    }

    @PostMapping("/{groupId}/like")
    public ResponseEntity<ApiResponse> groupLike(@PathVariable Long groupId, @Id Long id) {
        return ResponseEntity.ok(groupService.groupLike(groupId, id));
    }

    @DeleteMapping("/{groupId}/like")
    public ResponseEntity<ApiResponse> deleteGroupLike(@PathVariable Long groupId, @Id Long id) {
        return ResponseEntity.ok(groupService.deleteGroupLike(groupId, id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> groups(@RequestParam(defaultValue = "1") Integer pageNum,
                                               @RequestParam(defaultValue = "10") Integer pageSize,
                                               @Id Long id) {
        return ResponseEntity.ok(groupService.groups(pageNum, pageSize, id));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse> groupDetail(@PathVariable Long groupId, @Id Long id) {
        return ResponseEntity.ok(groupService.groupDetail(groupId, id));
    }
}
