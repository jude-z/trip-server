package com.tripplannerai.controller.keep;

import com.tripplannerai.common.annotation.Id;
import com.tripplannerai.dto.response.keep.CreateKeepResponse;
import com.tripplannerai.dto.response.keep.DeleteKeepResponse;
import com.tripplannerai.dto.response.keep.DetailKeepResponse;
import com.tripplannerai.dto.response.keep.KeepsResponse;
import com.tripplannerai.service.keep.KeepService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class KeepController {
    private final KeepService keepService;

    @PostMapping("/api/destination/{contentId}/create")
    public ResponseEntity<CreateKeepResponse> createKeep(@PathVariable String contentId, @Id Long id){
        CreateKeepResponse createKeepResponse = keepService.createKeep(contentId,id);
        return new ResponseEntity<>(createKeepResponse, HttpStatus.OK);
    }
    @PostMapping("/api/destination/{contentId}/delete")
    public ResponseEntity<DeleteKeepResponse> deleteKeep(@PathVariable String contentId, @Id Long id){
        DeleteKeepResponse deleteKeepResponse = keepService.deleteKeep(contentId,id);
        return new ResponseEntity<>(deleteKeepResponse, HttpStatus.OK);
    }
    @GetMapping("/api/destination/keeps")
    public ResponseEntity<KeepsResponse> keeps(@Id Long id
            , @RequestParam(defaultValue = "1") Integer pageNum
            , @RequestParam(defaultValue = "10") Integer pageSize){
        KeepsResponse keepsResponse = keepService.keeps(id,pageNum,pageSize);
        return new ResponseEntity<>(keepsResponse, HttpStatus.OK);
    }
    @GetMapping("/api/destination/{contentId}/keep")
    public ResponseEntity<DetailKeepResponse> detailKeep(@PathVariable String contentId, @Id Long id){
        DetailKeepResponse detailKeepResponse = keepService.detailKeep(contentId,id);
        return new ResponseEntity<>(detailKeepResponse, HttpStatus.OK);
    }


}
