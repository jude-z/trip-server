package com.tripplannerai.service.destination;

import com.tripplannerai.dto.response.destination.*;

import java.util.List;

public interface DestinationService {
    void saveDestinationFromApi() throws Exception;

    DestinationResponse fetchDestination(Long destinationId, Long id);

    DestinationsResponse fetchDestinations(Integer page, Integer size, String name, Long id);

    DestinationsCategoryResponse fetchDestinationByCategory(Integer page, Integer size, Long categoryId);

    TotalDestinationResponse fetchDestinationsByTotal();

    DestinationsResponse fetchMyDestinations(Integer page, Integer size, Long id);

    List<DestinationInfoResponse> getDestinationsByContentIds(List<String> contentIds);
}
