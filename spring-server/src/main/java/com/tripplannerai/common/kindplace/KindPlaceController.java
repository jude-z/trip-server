package com.tripplannerai.common.kindplace;

import com.tripplannerai.dto.response.kindplace.FetchKindPlaces;
import com.tripplannerai.entity.kindplace.KindPlace;
import com.tripplannerai.service.kindplace.KindPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class KindPlaceController {
    private final KindPlaceService kindPlaceService;
    @GetMapping("/kindplace")
    public ResponseEntity<FetchKindPlaces> fetchKindPlaces(@RequestParam String address,@RequestParam Integer pageNum) {
        FetchKindPlaces fetchKindPlaces = kindPlaceService.fetchKindPlaces(address,pageNum);
        return new ResponseEntity<>(fetchKindPlaces, HttpStatus.OK);
    }

}
