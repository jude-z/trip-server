package com.tripplannerai.util.init;

import com.tripplannerai.service.address.AddressService;
import com.tripplannerai.service.category.CategoryService;
import com.tripplannerai.service.course.CourseService;
import com.tripplannerai.service.destination.DestinationService;
import com.tripplannerai.service.kindplace.KindPlaceService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("aws")
public class DbInit {
    private final AddressService addressService;
    private final CategoryService categoryService;
    private final DestinationService destinationService;
    private final CourseService courseService;
    private final KindPlaceService kindPlaceService;
    @PostConstruct
    public void init() {
        try {
            initializeData();
        } catch (Exception e) {
            log.error("데이터베이스 초기화 실패: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void initializeData() throws Exception {
//
//        log.info("주소 데이터 저장 시작...");
//        addressService.saveAddressFromApi();
//        log.info("주소 데이터 저장 완료");
//
//        log.info("카테고리 데이터 저장 시작...");
//        categoryService.saveCategoryFromApi();
//        log.info("카테고리 데이터 저장 완료");
//
//        log.info("관광지 데이터 저장 시작...");
//        destinationService.saveDestinationFromApi();
//        log.info("관광지 데이터 저장 완료");
//
//        log.info("코스 데이터 저장 시작...");
//        courseService.saveCourseData();
//        log.info("코스 데이터 저장 완료");
//
//        log.info("착한 업소 저장 시작...");
//        kindPlaceService.saveKindPlace();
//        log.info("착한 업소 저장 완료");

    }
}
