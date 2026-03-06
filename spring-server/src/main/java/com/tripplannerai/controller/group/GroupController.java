package com.tripplannerai.controller.group;

import com.tripplannerai.common.annotation.Id;
import com.tripplannerai.dto.request.group.AddGroupRequest;
import com.tripplannerai.dto.response.group.*;
import com.tripplannerai.service.group.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<AddGroupResponse> addGroup(@RequestParam Long destinationId, @RequestBody AddGroupRequest addGroupRequest,@Id Long id){
        AddGroupResponse addGroupResponse = groupService.addGroup(addGroupRequest,id,destinationId);
        return new ResponseEntity<>(addGroupResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{groupId}/participate")
    public ResponseEntity<ParticipateGroupResponse> participateGroup(@PathVariable Long groupId, @Id Long id){
        ParticipateGroupResponse participateGroupResponse = groupService.participateGroup(groupId,id);
        return new ResponseEntity<>(participateGroupResponse, HttpStatus.OK);
    }
    @PutMapping("/{groupId}/permit")
    public ResponseEntity<ParticipateGroupResponse> permitGroup(@PathVariable Long groupId, @Id Long id,@RequestParam Long enrollId){
        ParticipateGroupResponse participateGroupResponse = groupService.permitGroup(groupId,id,enrollId);
        return new ResponseEntity<>(participateGroupResponse, HttpStatus.OK);
    }
    @PutMapping("/{groupId}/leave")
    public ResponseEntity<LeaveGroupResponse> leaveGroup(@PathVariable Long groupId, @Id Long id){
        LeaveGroupResponse participateGroupResponse = groupService.leaveGroup(groupId,id);
        return new ResponseEntity<>(participateGroupResponse, HttpStatus.OK);
    }

    @GetMapping("/{groupId}/apply")
    public ResponseEntity<ApplyGroupResponse> applyGroups(@PathVariable Long groupId, @Id Long id){
        ApplyGroupResponse applyGroupResponse = groupService.applyGroups(groupId,id);
        return new ResponseEntity<>(applyGroupResponse, HttpStatus.OK);
    }

    @GetMapping("/{groupId}/participate")
    public ResponseEntity<ApplyGroupResponse> participateGroups(@PathVariable Long groupId, @Id Long id){
        ApplyGroupResponse applyGroupResponse = groupService.participateGroups(groupId,id);
        return new ResponseEntity<>(applyGroupResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<DeleteGroupResponse> deleteGroup(@PathVariable Long groupId, @Id Long id){
        DeleteGroupResponse deleteGroupResponse = groupService.deleteGroup(groupId,id);
        return new ResponseEntity<>(deleteGroupResponse, HttpStatus.OK);
    }
    @PostMapping("/{groupId}/like")
    public ResponseEntity<GroupLikeResponse> groupLike(@PathVariable Long groupId, @Id Long id){
        GroupLikeResponse groupLikeResponse = groupService.groupLike(groupId,id);
        return new ResponseEntity<>(groupLikeResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{groupId}/like")
    public ResponseEntity<GroupLikeResponse> deleteGroupLike(@PathVariable Long groupId, @Id Long id){
        GroupLikeResponse groupLikeResponse = groupService.deleteGroupLike(groupId,id);
        return new ResponseEntity<>(groupLikeResponse, HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<?> groups(@RequestParam(defaultValue = "1") Integer pageNum,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @Id Long id){
        GroupsResponse groupsResponse = groupService.groups(pageNum,pageSize,id);
        return new ResponseEntity<>(groupsResponse,HttpStatus.OK);

    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponse> groupDetail(@PathVariable Long groupId,@Id Long id){
        GroupResponse groupResponse = groupService.groupDetail(groupId,id);
        return new ResponseEntity<>(groupResponse,HttpStatus.OK);
    }


}
